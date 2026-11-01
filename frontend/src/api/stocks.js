// 功能說明：建立帶有 HTTP 狀態與 API 錯誤代碼的錯誤物件。
// 輸入來源：回應狀態碼與回應中的錯誤代碼。
// 預期輸出：回傳供查詢流程辨識的錯誤。
function createStockApiError(status, errorCode) {
  const error = new Error('Stock API request failed')
  error.name = 'StockApiError'
  error.status = status
  error.errorCode = errorCode
  return error
}

// 功能說明：向 API 查詢指定股票並驗證回應格式。
// 輸入來源：查詢頁傳入的股票代碼與可選取消訊號。
// 預期輸出：成功時回傳股票資料，失敗時拋出含狀態與代碼的錯誤。
export async function fetchStockByCode(stockCode, { signal } = {}) {
  // 功能說明：正式 Electron 頁面使用本機服務注入的 Token 驗證 API 請求。
  // 輸入來源：首頁 meta 欄位；開發模式沒有此欄位，仍由 Vite Proxy 處理。
  // 預期輸出：正式請求附帶驗證 header，並保留原本的取消請求 signal。
  const appToken =
    typeof document === 'undefined'
      ? null
      : document.querySelector('meta[name="desktop-api-token"]')?.getAttribute('content')
  const headers = appToken ? { 'X-App-Token': appToken } : undefined

  const response = await fetch(`/api/stocks/${encodeURIComponent(stockCode)}`, {
    method: 'GET',
    headers,
    signal,
  })

  // 功能說明：將伺服器回應內容解析為 JSON。
  // 輸入來源：fetch 收到的 HTTP 回應本文。
  // 預期輸出：取得可檢查的資料物件，解析失敗時拋出 API 錯誤。
  let payload
  try {
    payload = await response.json()
  } catch {
    throw createStockApiError(response.status, null)
  }

  if (
    !response.ok ||
    payload?.success !== true ||
    !payload.data ||
    typeof payload.data !== 'object'
  ) {
    throw createStockApiError(response.status, payload?.errorCode ?? null)
  }

  return payload.data
}
