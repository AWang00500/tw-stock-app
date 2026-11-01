import { computed, onScopeDispose, ref } from 'vue'
import { fetchStockByCode } from '../api/stocks.js'

// 功能說明：集中對照 API 錯誤代碼與畫面訊息。
// 輸入來源：查詢回應或網路錯誤的代碼。
// 預期輸出：查詢流程可顯示的繁體中文錯誤文字。
const ERROR_MESSAGES = {
  INVALID_STOCK_CODE: '股票代號格式錯誤，請輸入數字代號。',
  STOCK_CODE_NOT_FOUND: '查無此股票代號。',
  MONTH_DATA_UNAVAILABLE: '近期查無此代號的交易資料。',
  TWSE_API_ERROR: '上游行情服務暫時無法使用，請稍後重試。',
  INTERNAL_ERROR: '查詢發生錯誤，請稍後重試。',
  NETWORK_ERROR: '無法連線至查詢服務，請確認後端已啟動後重試。',
}

// 功能說明：列出不適合重試的輸入或資料錯誤。
// 輸入來源：查詢錯誤代碼。
// 預期輸出：供 retryable 狀態判斷是否顯示重試選項。
const NON_RETRYABLE_ERROR_CODES = new Set([
  'INVALID_STOCK_CODE',
  'STOCK_CODE_NOT_FOUND',
  'MONTH_DATA_UNAVAILABLE',
])

// 功能說明：依錯誤代碼或 HTTP 狀態整理畫面要顯示的錯誤資訊。
// 輸入來源：API 請求或網路請求拋出的錯誤。
// 預期輸出：包含錯誤代碼與繁體中文訊息的物件。
function getErrorDetails(error) {
  const errorCode = error?.errorCode || (error?.name === 'TypeError' ? 'NETWORK_ERROR' : '')

  if (ERROR_MESSAGES[errorCode]) {
    return { errorCode, message: ERROR_MESSAGES[errorCode] }
  }

  if (error?.status === 400) {
    return {
      errorCode: 'INVALID_STOCK_CODE',
      message: ERROR_MESSAGES.INVALID_STOCK_CODE,
    }
  }

  return {
    errorCode: errorCode || 'UNKNOWN_ERROR',
    message: '查詢服務回應異常，請稍後重試。',
  }
}

// 功能說明：管理股票查詢資料、狀態、重試條件及請求取消。
// 輸入來源：頁面呼叫 queryStock 時提供的股票代碼，以及股票 API 回應。
// 預期輸出：回傳供頁面顯示的查詢狀態與查詢函式。
export function useStockQuery() {
  const stock = ref(null)
  const status = ref('idle')
  const submittedCode = ref('')
  const errorCode = ref('')
  const errorMessage = ref('')
  const loading = computed(() => status.value === 'loading')
  const retryable = computed(
    () => status.value === 'error' && !NON_RETRYABLE_ERROR_CODES.has(errorCode.value),
  )

  let latestRequestId = 0
  let activeController = null

  // 功能說明：驗證輸入、取消舊請求並執行最新股票查詢，再依結果更新狀態。
  // 輸入來源：搜尋表單送出的原始股票代碼與 fetchStockByCode 的回應。
  // 預期輸出：更新股票資料、載入狀態或錯誤訊息；無效代碼時直接顯示錯誤。
  async function queryStock(rawStockCode) {
    const requestId = ++latestRequestId
    activeController?.abort()
    activeController = null

    const stockCode = String(rawStockCode ?? '').trim()
    submittedCode.value = stockCode
    stock.value = null
    errorCode.value = ''
    errorMessage.value = ''

    if (!stockCode) {
      status.value = 'error'
      errorCode.value = 'INVALID_STOCK_CODE'
      errorMessage.value = '請輸入股票代號。'
      return
    }

    if (!/^\d+$/.test(stockCode)) {
      status.value = 'error'
      errorCode.value = 'INVALID_STOCK_CODE'
      errorMessage.value = ERROR_MESSAGES.INVALID_STOCK_CODE
      return
    }

    const controller = new AbortController()
    activeController = controller
    status.value = 'loading'

    try {
      const result = await fetchStockByCode(stockCode, { signal: controller.signal })

      if (requestId !== latestRequestId) {
        return
      }

      stock.value = result
      status.value = 'success'
    } catch (error) {
      if (requestId !== latestRequestId || error?.name === 'AbortError') {
        return
      }

      const details = getErrorDetails(error)
      errorCode.value = details.errorCode
      errorMessage.value = details.message
      status.value = 'error'
    } finally {
      if (requestId === latestRequestId) {
        activeController = null
      }
    }
  }

  // 功能說明：查詢元件離開作用範圍時使未完成請求失效並取消請求。
  // 輸入來源：Vue 的作用範圍清理事件與目前活動中的控制器。
  // 預期輸出：避免離開頁面後仍處理舊查詢結果。
  onScopeDispose(() => {
    latestRequestId += 1
    activeController?.abort()
  })

  return {
    stock,
    status,
    submittedCode,
    errorCode,
    errorMessage,
    loading,
    retryable,
    queryStock,
  }
}