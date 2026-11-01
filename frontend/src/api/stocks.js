function createStockApiError(status, errorCode) {
  const error = new Error('Stock API request failed')
  error.name = 'StockApiError'
  error.status = status
  error.errorCode = errorCode
  return error
}

export async function fetchStockByCode(stockCode, { signal } = {}) {
  const response = await fetch(`/api/stocks/${encodeURIComponent(stockCode)}`, {
    method: 'GET',
    signal,
  })

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