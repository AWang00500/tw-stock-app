import { computed, onScopeDispose, ref } from 'vue'
import { fetchStockByCode } from '../api/stocks.js'

const ERROR_MESSAGES = {
  INVALID_STOCK_CODE: '股票代號格式錯誤，請輸入數字代號。',
  STOCK_CODE_NOT_FOUND: '查無此股票代號。',
  MONTH_DATA_UNAVAILABLE: '近期查無此代號的交易資料。',
  TWSE_API_ERROR: '上游行情服務暫時無法使用，請稍後重試。',
  INTERNAL_ERROR: '查詢發生錯誤，請稍後重試。',
  NETWORK_ERROR: '無法連線至查詢服務，請確認後端已啟動後重試。',
}

const NON_RETRYABLE_ERROR_CODES = new Set([
  'INVALID_STOCK_CODE',
  'STOCK_CODE_NOT_FOUND',
  'MONTH_DATA_UNAVAILABLE',
])

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