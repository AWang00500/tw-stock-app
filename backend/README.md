# TW Stock Ticker Fetcher

一個用 Java 撰寫的台股資訊查詢小工具，透過台灣證券交易所（TWSE）官方公開資料，
查詢指定股票的近期價格資訊，包含昨日開收、5日/10日均線、本月最高最低價。

## 功能

- 輸入股票代號，查詢該股票的最新交易資訊
- 顯示昨日開盤價、收盤價、漲跌價差
- 計算並顯示 5 日、10 日移動平均線
- 顯示本月至今的最高價與最低價
- 輸入驗證：檢查代號格式、處理查無資料的情況並允許重新輸入

## 使用技術

- Java 21
- Maven
- Jackson（`jackson-databind`）：解析 TWSE 回傳的 JSON 資料
- Java 11+ 內建 `HttpClient`：發送 HTTP 請求
- 資料來源：[TWSE 證交所個股日成交資訊](https://www.twse.com.tw/exchangeReport/STOCK_DAY)

## 如何執行

1. clone 這個專案
2. 用 IntelliJ IDEA（或其他支援 Maven 的 IDE）開啟
3. 執行 `org.example.twStockTickerFetcher` 的 `main` 方法
4. 依畫面指示輸入股票代號（純數字，例如 2330）

## 未來規劃

- 串接 Discord Webhook，自動推送查詢結果
- 製作簡易 GUI 介面（Swing），取代目前的 console 互動
- 補上單元測試（JUnit），涵蓋價格解析、日期轉換等邏輯

## 技術筆記

- TWSE 資料為民國年格式，程式內會自動轉換為西元年
- 價格字串含千分位逗號與特殊符號（如 `X` 代表不比價），已做清理處理

-----------------------------------------------------------------
---

# TW Stock Ticker Fetcher

A Java-based CLI tool designed to fetch Taiwan Stock Exchange (TWSE) market data. It displays key price metrics for a given stock symbol, including yesterday's OHLC, 5-day / 10-day moving averages (MA), and monthly highest / lowest prices.

## Features

- **Stock Search**: Look up market data by entering a valid stock symbol.
- **Daily Summary**: Display yesterday's open price, close price, and price spread.
- **Moving Averages**: Calculate and display 5-day and 10-day moving averages (MA5 / MA10).
- **Monthly Range**: Show the highest and lowest prices of the current month.
- **Input Validation**: Validate symbol formats, handle empty/invalid API responses gracefully, and support re-entry.

## Tech Stack

- **Java 21**
- **Maven**
- **Jackson (`jackson-databind`)**: Parses JSON responses from TWSE.
- **Java 11+ `HttpClient`**: Handles asynchronous/synchronous HTTP requests.
- **Data Source**: [TWSE Stock Day Report API](https://www.twse.com.tw/exchangeReport/STOCK_DAY)

## How to Run

1.Clone this repository.
2.Open it using IntelliJ IDEA (or any Maven-supported IDE).
3.Run the main method in org.example.twStockTickerFetcher.
4.Follow the on-screen instructions to enter a stock ticker (digits only, e.g., 2330).

## Roadmap
- Integrate Discord Webhook to automatically push query results
- Build a simple GUI (Swing) to replace the current console interaction
- Add unit tests (JUnit) covering price parsing, date conversion, and related logic

## Technical Notes
- **Date Format:** TWSE data uses the Minguo calendar format; the application automatically converts it to Gregorian years.
- **Data Sanitization:** Price strings contain thousand separators (commas) and special characters (e.g., `X` indicating non-comparison/no price), which are automatically cleaned and handled.