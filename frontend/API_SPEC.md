# 台股查詢 API 前後端介接規格

本文依目前 Spring Boot 後端程式整理，供 Vue 3 前端開發時使用。後端目前提供單一股票查詢 API，尚未包含登入、收藏、搜尋清單或圖表專用 API。

## 1. API 基本資訊

| 項目 | 規格 |
|---|---|
| 預設 Base URL | `http://localhost:8080` |
| Endpoint | `/api/stocks/{stockCode}` |
| HTTP Method | `GET` |
| Path 參數 | `stockCode`：股票代號，僅接受數字字串，例如 `2330` |
| Request Body | 無 |
| 必要自訂 Header | 無；不需 Authorization 或 API key |
| 成功狀態碼 | `200 OK` |

範例：

```http
GET http://localhost:8080/api/stocks/2330
```

## 2. 查詢流程

1. 前端將使用者輸入的股票代號放入 URL path，例如 `2330`。
2. 後端先檢查代號是否為非空數字字串。
3. 後端優先查詢目前月份；該月沒有交易資料時，最多再查前兩個月份，總請求次數最多三次。
4. 每次查詢找到資料後，後端回傳實際採用月份 `queriedMonth`，格式為 `yyyy-MM`。
5. API 與 CLI 共用同一個 `StockService` 查詢及計算邏輯。

前端不需要傳日期或月份。月份選擇與回退由後端處理。

## 3. 成功回應

HTTP 狀態碼為 `200`。以下為回應範例：

```json
{
  "success": true,
  "data": {
    "stockCode": "2330",
    "title": "115年10月 2330 台積電 各日成交資訊",
    "queriedMonth": "2026-10",
    "yesterdayPrice": {
      "yesterdayDate": "2026-10-30",
      "yesterdayOpen": 2380.0,
      "yesterdayClose": 2370.0,
      "priceSpread": -45.0
    },
    "monthlyHighLow": {
      "highest": 2590.0,
      "lowest": 2360.0
    },
    "movingAverages": {
      "ma5": 2399.0,
      "ma10": 2410.0
    }
  },
  "errorCode": null,
  "message": "查詢成功"
}
```

範例數值會隨 TWSE 資料更新而改變。`title` 是上游回傳文字，內容及空白格式可能有所不同。

### 欄位說明

| 欄位 | 型別 | 用途／可為 `null` |
|---|---|---|
| `success` | Boolean | 請求是否成功。成功時為 `true`。 |
| `data` | Object 或 `null` | 成功時為股票資料；失敗時為 `null`。 |
| `data.stockCode` | String | 查詢的股票代號。 |
| `data.title` | String | TWSE 回傳的標題文字。 |
| `data.queriedMonth` | String | 實際取得資料的月份，格式 `yyyy-MM`。 |
| `data.yesterdayPrice` | Object | 最近一筆交易摘要。資料有效時不為 `null`。 |
| `data.yesterdayPrice.yesterdayDate` | String | 最近一筆交易日期，格式 `yyyy-MM-dd`。名稱雖為 `yesterdayDate`，實際代表查詢資料中的最後一筆交易日期，不保證是前一個日曆日。 |
| `data.yesterdayPrice.yesterdayOpen` | Number | 該筆交易開盤價。 |
| `data.yesterdayPrice.yesterdayClose` | Number | 該筆交易收盤價。 |
| `data.yesterdayPrice.priceSpread` | Number | 該筆交易漲跌價差。 |
| `data.monthlyHighLow` | Object | 實際採用月份資料中的最高價與最低價；資料有效時不為 `null`。 |
| `data.monthlyHighLow.highest` | Number | 月內最高價。 |
| `data.monthlyHighLow.lowest` | Number | 月內最低價。 |
| `data.movingAverages` | Object | MA5、MA10；資料有效時物件存在。 |
| `data.movingAverages.ma5` | Number 或 `null` | 有效交易日少於 5 筆時為 `null`。 |
| `data.movingAverages.ma10` | Number 或 `null` | 有效交易日少於 10 筆時為 `null`。 |
| `errorCode` | String 或 `null` | 成功時為 `null`；失敗時為錯誤代碼。 |
| `message` | String | 成功或失敗的訊息。 |

## 4. 失敗回應與錯誤代碼

已由後端全域例外處理器處理的錯誤，使用相同外層格式：

```json
{
  "success": false,
  "data": null,
  "errorCode": "MONTH_DATA_UNAVAILABLE",
  "message": "No trading data was available for stock code 2330 in the current or previous two months."
}
```

| HTTP 狀態碼 | `errorCode` | 意義 | 前端建議處理 |
|---:|---|---|---|
| `400` | `INVALID_STOCK_CODE` | 股票代號為空或含非數字字元。 | 顯示格式提示，保留輸入值。 |
| `404` | `STOCK_CODE_NOT_FOUND` | TWSE 回應明確指出股票代號不存在。 | 顯示查無此代號。TWSE 未明確指出時，後端不會使用此分類。 |
| `404` | `MONTH_DATA_UNAVAILABLE` | 目前月份及前兩個月份都沒有可用交易資料。也可能是上游無法明確區分的無資料代號。 | 告知近期查無資料；不要直接判定代號無效。 |
| `502` | `TWSE_API_ERROR` | TWSE 連線、HTTP 狀態、回應格式或非預期狀態異常。 | 顯示服務暫時不可用，提供重試操作。 |
| `500` | `INTERNAL_ERROR` | 未預期的後端錯誤。 | 顯示一般錯誤提示，避免把堆疊或內部細節呈現給使用者。 |

上述錯誤碼依目前 `GlobalExceptionHandler` 定義。對不存在的路徑、錯誤 HTTP method 等 Spring MVC 路由層錯誤，未必使用此 API JSON 外層格式。

## 5. Vue 前端串接注意事項

### CORS

目前後端程式沒有 CORS 設定（沒有 Controller `@CrossOrigin`、全域 CORS 設定或相關設定檔）。Vue 開發伺服器通常使用不同 Port，例如 `http://localhost:5173`，與後端 `http://localhost:8080` 屬於不同 Origin；瀏覽器直接呼叫時可能被 CORS 擋下。

前端開發期間可在 Vite 設定 `/api` proxy 指向 `http://localhost:8080`，並以相對路徑呼叫 `/api/stocks/2330`。若部署時前後端仍使用不同 Origin，需由後端設定允許的前端 Origin，或使用同源反向代理。curl、Postman 不受瀏覽器 CORS 限制。

### 載入、失敗與無資料狀態

- **載入中**：送出請求時顯示 loading 狀態；完成或失敗後清除。可停用重複送出。
- **成功**：確認 HTTP 為 2xx 且 `success === true`，再使用 `data` 更新畫面。`movingAverages.ma5` 和 `ma10` 要允許 `null`，可顯示「資料不足」或 `—`。
- **近期無資料**：HTTP 404 且 `errorCode === "MONTH_DATA_UNAVAILABLE"`，顯示近期查無交易資料。不要當成網路錯誤。
- **股票代號無效**：HTTP 400 或 `STOCK_CODE_NOT_FOUND`，顯示相應輸入提示／查無代號訊息。
- **上游或後端錯誤**：處理 502、500 和網路錯誤，顯示可重試訊息。Axios 等客戶端通常會將非 2xx 放入 rejected promise；需從 `error.response.data` 讀取固定 JSON。使用 Fetch 時需自行檢查 `response.ok`。

API 不需要 Cookie、登入狀態或自訂認證 Header。前端可送出一般 GET 請求，不需 Request Body。

## 6. 開發環境與測試

### 啟動後端

在 IntelliJ IDEA 執行 `org.example.TwStockTickerFetcherApplication` 的 `main` 方法，或在專案根目錄執行：

```bash
mvn spring-boot:run
```

目前沒有自訂 `server.port` 設定，Spring Boot 預設使用 Port `8080`。

### 測試 API

瀏覽器或 Postman：

```http
GET http://localhost:8080/api/stocks/2330
```

curl：

```bash
curl -i http://localhost:8080/api/stocks/2330
```

代號格式錯誤可用以下請求確認錯誤回應：

```bash
curl -i http://localhost:8080/api/stocks/ABC
```

測試會呼叫 TWSE；若 TWSE 當月無資料，後端會自動嘗試前兩個月。測試結果會依當下資料與上游服務狀態而變化。

## 7. 目前已知限制

- 目前只有單股票查詢 API，沒有股票清單、收藏、篩選、走勢圖或即時推播 API。
- 成功資料來自 TWSE 月資料；`queriedMonth` 表示實際查到的月份，可能不是目前月份。
- `yesterdayDate` 欄位實際是 TWSE 回傳資料的最後一個交易日，不一定是昨天。
- 均線資料不足時均線欄位回傳 `null`，其餘有效股票資料仍會回傳。
- 代號只做非空數字格式驗證；TWSE 沒有明確指出代號不存在時，三個月無資料會回報 `MONTH_DATA_UNAVAILABLE`。
- 後端目前沒有 CORS 設定；瀏覽器跨 Origin 開發需 Vite proxy 或後端／部署層設定。
- API 使用系統目前月份查詢；沒有由前端指定月份的參數。
- 當上游月份回應格式或狀態不是預期的「無資料」訊息時，會視為 `TWSE_API_ERROR`，不會繼續回退月份。
