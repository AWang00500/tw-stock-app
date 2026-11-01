<script setup>
// 功能說明：準備表單輸入值並連接股票查詢狀態。
// 輸入來源：使用者透過表單輸入的代碼及 useStockQuery 回傳資料。
// 預期輸出：供模板觸發查詢並依狀態顯示結果。
import { ref } from 'vue'
import LatestTrade from '../components/LatestTrade.vue'
import MonthlyRange from '../components/MonthlyRange.vue'
import MovingAverages from '../components/MovingAverages.vue'
import StockSearchForm from '../components/StockSearchForm.vue'
import StockSummary from '../components/StockSummary.vue'
import { useStockQuery } from '../composables/useStockQuery.js'

const stockCodeInput = ref('')
const {
  stock,
  status,
  loading,
  submittedCode,
  errorMessage,
  retryable,
  queryStock,
} = useStockQuery()
</script>

<template>
  <!--
  功能說明：呈現頁首品牌與目前連線環境標示。
  輸入來源：頁面固定文字。
  預期輸出：顯示股票應用程式名稱及 API 環境標籤。
  -->
  <div class="page-shell">
    <header class="topbar">
      <a class="brand" href="/" aria-label="台股查詢首頁">
        <span class="brand-mark" aria-hidden="true">T</span>
        <span>台股查詢</span>
      </a>
      <span class="environment-label"><span class="status-dot"></span>Spring Boot API</span>
    </header>

    <!--
    功能說明：呈現查詢介紹、表單、查詢狀態及成功後的股票摘要。
    輸入來源：表單輸入與查詢 composable 提供的狀態、錯誤和股票資料。
    預期輸出：依查詢進度顯示載入、錯誤、閒置或結果內容。
    -->
    <main class="main-content">
      <section class="intro" aria-labelledby="page-title">
        <p class="eyebrow">TAIWAN STOCKS</p>
        <h1 id="page-title">掌握一檔股票的關鍵資訊</h1>
        <p class="intro-copy">輸入股票代號，查看最近交易摘要與月內價格概況。</p>
      </section>

      <StockSearchForm
        v-model="stockCodeInput"
        :loading="loading"
        @search="queryStock"
      />

      <p v-if="loading" class="query-message loading-message" role="status" aria-live="polite">
        正在查詢 {{ submittedCode }}，再次送出會以新查詢取代目前結果。
      </p>
      <div v-else-if="status === 'error'" class="query-message error-message" role="alert">
        <p>{{ errorMessage }}</p>
        <button v-if="retryable" type="button" @click="queryStock(submittedCode)">
          重試查詢
        </button>
      </div>
      <p v-else-if="status === 'idle'" class="query-message idle-message">
        輸入股票代號後開始查詢。
      </p>

      <!--
      功能說明：查詢成功且有股票資料時顯示摘要及各項行情卡片。
      輸入來源：查詢回傳的 stock 欄位。
      預期輸出：呈現股票名稱、日期、成交與區間及均線資料。
      -->
      <section v-if="stock" class="results" aria-labelledby="result-heading">
        <div class="section-heading">
          <div>
            <p class="eyebrow">STOCK OVERVIEW</p>
            <h2 id="result-heading">查詢資訊</h2>
          </div>
        </div>

        <StockSummary
          :stock-code="stock.stockCode"
          :title="stock.title"
          :queried-month="stock.queriedMonth"
        />

        <div class="detail-grid">
          <LatestTrade :trade="stock.yesterdayPrice" />
          <MonthlyRange :range="stock.monthlyHighLow" />
          <MovingAverages :averages="stock.movingAverages" />
        </div>
        <p class="data-note">日期代表資料中的最近交易日，不一定是前一個日曆日。</p>
      </section>
    </main>

    <footer class="page-footer">台股查詢 <span>·</span> 個人行情資訊工具</footer>
  </div>
</template>

<style scoped>
/*
功能說明：設定查詢頁各區塊、狀態訊息及小螢幕版面的外觀。
輸入來源：本元件的 class 與畫面寬度。
預期輸出：形成有層次且可隨螢幕寬度調整的查詢頁版面。
*/
.page-shell {
  min-height: 100vh;
  padding: 0 32px;
}

.topbar {
  display: flex;
  width: min(1120px, 100%);
  height: 76px;
  align-items: center;
  justify-content: space-between;
  margin: 0 auto;
  border-bottom: 1px solid #e8ecf3;
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  color: #172033;
  font-size: 15px;
  font-weight: 700;
  text-decoration: none;
}

.brand-mark {
  display: grid;
  width: 31px;
  height: 31px;
  place-items: center;
  border-radius: 9px;
  background: #2563eb;
  color: white;
  font-size: 15px;
  font-weight: 700;
}

.environment-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #647086;
  font-size: 12px;
  font-weight: 600;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #e3a72f;
}

.main-content {
  width: min(940px, 100%);
  margin: 0 auto;
  padding: 57px 0 32px;
}

.intro {
  margin-bottom: 28px;
}

.eyebrow {
  margin: 0 0 10px;
  color: #748199;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 1.4px;
}

h1,
h2,
p {
  margin-top: 0;
}

h1 {
  margin-bottom: 10px;
  color: #172033;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.35;
}

.intro-copy {
  margin-bottom: 0;
  color: #788397;
  font-size: 14px;
  line-height: 1.7;
}

.results {
  margin-top: 43px;
}

.section-heading {
  display: flex;
  align-items: end;
  justify-content: space-between;
  margin-bottom: 18px;
}

.section-heading .eyebrow {
  margin-bottom: 6px;
}

h2 {
  margin-bottom: 0;
  color: #172033;
  font-size: 19px;
  font-weight: 700;
}

.query-message {
  margin: 15px 0 0;
  padding: 13px 15px;
  border: 1px solid #e2e8f2;
  border-radius: 9px;
  background: #fff;
  color: #647086;
  font-size: 12px;
  line-height: 1.6;
}

.query-message p {
  margin: 0;
}

.loading-message {
  border-color: #dce7ff;
  background: #f7f9ff;
  color: #315da9;
}

.error-message {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border-color: #f0d7d7;
  background: #fffafa;
  color: #9e3f46;
}

.error-message button {
  flex: 0 0 auto;
  padding: 7px 11px;
  border: 1px solid #e8c4c6;
  border-radius: 7px;
  background: #fff;
  color: #9e3f46;
  font-size: 11px;
  font-weight: 650;
}

.error-message button:hover {
  background: #fff1f1;
}

.idle-message {
  border-style: dashed;
  background: transparent;
}

.detail-grid {
  display: grid;
  grid-template-columns: 1.25fr 1fr 1fr;
  gap: 14px;
  margin-top: 14px;
}

.data-note {
  margin: 13px 2px 0;
  color: #8993a4;
  font-size: 11px;
}

.page-footer {
  width: min(940px, 100%);
  margin: 0 auto;
  padding: 21px 0 26px;
  border-top: 1px solid #e8ecf3;
  color: #8993a4;
  font-size: 11px;
}

.page-footer span {
  padding: 0 5px;
  color: #b0b8c5;
}

@media (max-width: 760px) {
  .page-shell {
    padding: 0 20px;
  }

  .main-content {
    padding-top: 42px;
  }

  .detail-grid {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 520px) {
  .page-shell {
    padding: 0 16px;
  }

  .topbar {
    height: 65px;
  }

  .main-content {
    padding-top: 34px;
  }

  h1 {
    font-size: 25px;
  }

  .intro-copy {
    font-size: 13px;
  }

  .results {
    margin-top: 35px;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .error-message {
    align-items: flex-start;
  }
}
</style>