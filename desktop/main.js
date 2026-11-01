// 功能說明：載入 Electron 應用程式與桌面視窗建立工具。
// 輸入來源：Electron 套件。
// 預期輸出：提供主程序建立視窗所需的工具。
const { app, BrowserWindow, dialog } = require('electron');
const http = require('node:http');
const { spawn } = require('node:child_process');
const path = require('node:path');
const { startLocalServer } = require('./local-server');

const BACKEND_HEALTH_URL = 'http://127.0.0.1:8080/api/health';
const BACKEND_WORKING_DIRECTORY = path.resolve(__dirname, '../backend');
const BACKEND_SERVICE_NAME = 'tw-stock-ticker-fetcher';
const BACKEND_STARTUP_TIMEOUT_MS = 120000;
const BACKEND_CHECK_INTERVAL_MS = 500;
const BACKEND_CHECK_TIMEOUT_MS = 1500;

let backendProcess = null;
let backendProcessError = null;
let backendExitInfo = null;
let backendOutput = '';
let backendReady = false;
let allowAppQuit = false;
let cleanupInProgress = false;
let mainWindow = null;
let localServer = null;
let frontendUrl = null;

// 功能說明：建立主要視窗並載入已選定的 Vue 前端網址。
// 輸入來源：後端健康檢查後選出的 Vite 或 Electron 本機服務網址。
// 預期輸出：開啟套用安全設定的 1000×700 Vue 視窗。
function createWindow(frontendUrl) {
  mainWindow = new BrowserWindow({
    width: 1000,
    height: 700,
    webPreferences: {
      nodeIntegration: false,
      contextIsolation: true,
    },
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  mainWindow.loadURL(frontendUrl).catch((error) => {
    dialog.showErrorBox('前端載入失敗', error.message);
    app.quit();
  });
}

// 功能說明：向本機健康檢查網址確認 8080 連接埠提供的是本專案後端。
// 輸入來源：HTTP 回應的狀態碼與 JSON 服務識別資料。
// 預期輸出：正確後端回傳 true；沒有服務監聽時回傳 false；其他服務或檢查失敗時回報錯誤。
function checkBackendHealth() {
  return new Promise((resolve, reject) => {
    const request = http.get(BACKEND_HEALTH_URL, { family: 4 }, (response) => {
      let responseBody = '';

      response.setEncoding('utf8');
      response.on('data', (chunk) => {
        responseBody += chunk;
      });

      response.on('end', () => {
        let payload;

        try {
          payload = JSON.parse(responseBody);
        } catch {
          const error = new Error(
            `連接埠 8080 已有服務回應，但不是本專案後端（HTTP ${response.statusCode}）。`,
          );
          error.code = 'BACKEND_PORT_CONFLICT';
          reject(error);
          return;
        }

        if (
          response.statusCode === 200 &&
          payload?.service === BACKEND_SERVICE_NAME &&
          payload?.status === 'UP'
        ) {
          resolve(true);
          return;
        }

        const error = new Error(
          `連接埠 8080 已被其他服務占用，或健康檢查回應不符（HTTP ${response.statusCode}）。`,
        );
        error.code = 'BACKEND_PORT_CONFLICT';
        reject(error);
      });
    });

    request.setTimeout(BACKEND_CHECK_TIMEOUT_MS, () => {
      const error = new Error('連接埠 8080 有回應，但健康檢查逾時。');
      error.code = 'BACKEND_HEALTH_TIMEOUT';
      request.destroy(error);
    });

    request.on('error', (error) => {
      if (error.code === 'ECONNREFUSED') {
        resolve(false);
        return;
      }

      reject(error);
    });
  });
}

// 功能說明：啟動由 Electron 管理的 Maven Spring Boot 程序並收集啟動輸出。
// 輸入來源：backend 資料夾路徑、Windows 命令處理器及固定 Maven 啟動命令。
// 預期輸出：建立可追蹤及關閉的子程序，並保存最近的輸出供錯誤訊息使用。
function startBackendProcess() {
  const isWindows = process.platform === 'win32';
  const configuredMavenCommand = !app.isPackaged ? process.env.MAVEN_CMD?.trim() : '';
  const mavenCommand = configuredMavenCommand || 'mvn';
  const command = isWindows ? process.env.ComSpec || 'cmd.exe' : mavenCommand;
  const args = isWindows
    ? ['/d', '/s', '/c', `call "${mavenCommand}" spring-boot:run`]
    : ['spring-boot:run'];

  backendProcess = spawn(command, args, {
    cwd: BACKEND_WORKING_DIRECTORY,
    windowsHide: true,
    windowsVerbatimArguments: isWindows,
    stdio: ['ignore', 'pipe', 'pipe'],
  });

  const saveOutput = (chunk) => {
    backendOutput = `${backendOutput}${chunk.toString()}`.slice(-12000);
  };

  backendProcess.stdout.on('data', saveOutput);
  backendProcess.stderr.on('data', saveOutput);
  backendProcess.on('error', (error) => {
    backendProcessError = error;
  });
  backendProcess.on('exit', (code, signal) => {
    backendExitInfo = { code, signal };
  });
}

// 功能說明：等待本專案健康端點回應，並在 Maven 提前結束或等待逾時時停止啟動流程。
// 輸入來源：健康檢查結果、Maven 子程序狀態與啟動期限。
// 預期輸出：健康檢查成功時完成等待；失敗時拋出包含程序輸出的錯誤。
async function waitForBackendReady() {
  const deadline = Date.now() + BACKEND_STARTUP_TIMEOUT_MS;

  while (Date.now() < deadline) {
    if (backendProcessError) {
      throw new Error(`無法啟動 Maven：${backendProcessError.message}`);
    }

    if (backendExitInfo) {
      throw new Error(
        `Maven 在後端就緒前結束（結束碼 ${backendExitInfo.code ?? '未知'}）。\n${backendOutput}`,
      );
    }

    if (await checkBackendHealth()) {
      return;
    }

    await new Promise((resolve) => setTimeout(resolve, BACKEND_CHECK_INTERVAL_MS));
  }

  throw new Error(`等待 Spring Boot 健康檢查逾時。\n${backendOutput}`);
}

// 功能說明：先重用已回應正確識別的後端，否則才啟動 Maven 並等待健康檢查成功。
// 輸入來源：localhost:8080 的健康檢查回應與必要時啟動的 Maven 子程序。
// 預期輸出：確認本專案後端可用；若連接埠屬於其他服務或啟動失敗則拋出錯誤。
async function ensureBackendReady() {
  try {
    if (await checkBackendHealth()) {
      return;
    }
  } catch (error) {
    if (error.code === 'BACKEND_PORT_CONFLICT') {
      throw error;
    }

    if (error.code === 'BACKEND_HEALTH_TIMEOUT') {
      throw new Error(`${error.message} 為避免啟動重複服務，Electron 不會啟動 Maven。`);
    }

    throw new Error(`無法檢查本機後端：${error.message}`);
  }

  startBackendProcess();
  await waitForBackendReady();
}

// 功能說明：結束 Electron 自己啟動的 Maven 程序樹，並等待程序完全關閉。
// 輸入來源：Electron 記錄的後端子程序及目前作業系統。
// 預期輸出：Windows 終止該程序及其 Java 子程序；其他平台結束所啟動的 Maven 子程序。
function stopOwnedBackendProcess() {
  if (
    !backendProcess ||
    !backendProcess.pid ||
    backendProcess.exitCode !== null ||
    backendProcess.signalCode !== null
  ) {
    return Promise.resolve();
  }

  if (process.platform !== 'win32') {
    backendProcess.kill('SIGTERM');
  } else {
    return new Promise((resolve, reject) => {
      const terminator = spawn(
        'taskkill.exe',
        ['/PID', String(backendProcess.pid), '/T', '/F'],
        { windowsHide: true, stdio: 'ignore' },
      );

      terminator.once('error', reject);
      terminator.once('close', (code) => {
        if (code === 0 || backendProcess.exitCode !== null || backendProcess.signalCode !== null) {
          resolve();
          return;
        }

        reject(new Error(`taskkill 無法結束 Maven 程序樹（結束碼 ${code}）。`));
      });
    }).then(waitForBackendProcessExit);
  }

  return waitForBackendProcessExit();
}

// 功能說明：等待 Electron 啟動的後端程序發出關閉事件。
// 輸入來源：backendProcess 的目前狀態與 close 事件。
// 預期輸出：程序關閉後完成；超過等待期限則回報清理失敗。
function waitForBackendProcessExit() {
  if (
    !backendProcess ||
    backendProcess.exitCode !== null ||
    backendProcess.signalCode !== null
  ) {
    return Promise.resolve();
  }

  return new Promise((resolve, reject) => {
    const timeout = setTimeout(() => {
      reject(new Error('等待 Maven 與 Java 子程序關閉逾時。'));
    }, 10000);

    backendProcess.once('close', () => {
      clearTimeout(timeout);
      resolve();
    });
  });
}

// 功能說明：關閉 Electron 建立的前端服務，並清理本程式自行啟動的後端。
// 輸入來源：目前儲存的本機服務與 Maven 程序參照。
// 預期輸出：本機服務停止，且不影響 Electron 啟動前已存在的後端。
async function cleanupOwnedResources() {
  let cleanupError = null;

  if (localServer) {
    const serverToClose = localServer;
    localServer = null;

    try {
      await serverToClose.close();
    } catch (error) {
      cleanupError = error;
    }
  }

  try {
    await stopOwnedBackendProcess();
  } catch (error) {
    cleanupError ||= error;
  }

  if (cleanupError) {
    throw cleanupError;
  }
}

// 功能說明：等 Electron 初始化完成後建立視窗，並在 macOS 重新啟用時補開視窗。
// 輸入來源：Electron 的就緒與 activate 事件。
// 預期輸出：應用程式啟動或重新啟用時有可用視窗。
app.whenReady().then(() => {
  app.on('activate', () => {
    if (backendReady && frontendUrl && BrowserWindow.getAllWindows().length === 0) {
      createWindow(frontendUrl);
    }
  });

  ensureBackendReady()
    .then(async () => {
      if (app.isPackaged || process.env.ELECTRON_LOAD_BUILT_VUE === '1') {
        localServer = await startLocalServer();
        frontendUrl = localServer.url;
      } else {
        frontendUrl = 'http://localhost:5173';
      }
      backendReady = true;
      createWindow(frontendUrl);
    })
    .catch((error) => {
      dialog.showErrorBox('無法啟動台股服務', error.message);
      app.quit();
    });
});

app.on('before-quit', (event) => {
  const hasRunningOwnedBackend =
    backendProcess && backendProcess.exitCode === null && backendProcess.signalCode === null;

  if (allowAppQuit || (!localServer && !hasRunningOwnedBackend)) {
    return;
  }

  event.preventDefault();

  if (cleanupInProgress) {
    return;
  }

  cleanupInProgress = true;
  cleanupOwnedResources()
    .then(() => {
      allowAppQuit = true;
      app.quit();
    })
    .catch((error) => {
      cleanupInProgress = false;
      dialog.showErrorBox('無法關閉台股後端', error.message);
    });
});

// 功能說明：所有視窗關閉時依作業系統決定是否結束應用程式。
// 輸入來源：Electron 的 window-all-closed 事件與目前平台。
// 預期輸出：非 macOS 平台結束應用程式，macOS 保留應用程式執行。
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});
