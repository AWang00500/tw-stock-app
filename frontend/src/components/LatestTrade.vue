<script setup>
// 功能說明：接收昨日交易資料並提供價格格式化函式。
// 輸入來源：父元件傳入的 trade 物件及其中的價格數值。
// 預期輸出：可供畫面呈現的交易資料與千分位價格文字。
defineProps({
  trade: {
    type: Object,
    required: true,
  },
})

// 功能說明：將價格轉成繁體中文地區格式，最多顯示兩位小數。
// 輸入來源：交易資料中的價格數值。
// 預期輸出：格式化後的價格字串。
function formatPrice(value) {
  return new Intl.NumberFormat('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(value)
}
</script>

<template>
  <!--
  功能說明：呈現最近交易日期、開收盤價與價差。
  輸入來源：父元件傳入的 trade 資料。
  預期輸出：顯示昨日行情及依價差正負套用的提示樣式。
  -->
  <article class="info-card latest-card">
    <div class="card-heading">
      <div>
        <p class="card-kicker">LATEST TRADE</p>
        <h3>最近交易日</h3>
      </div>
      <span class="date-chip">{{ trade.yesterdayDate }}</span>
    </div>
    <div class="price-list">
      <div class="price-row">
        <span>開盤價</span>
        <strong>{{ formatPrice(trade.yesterdayOpen) }}</strong>
      </div>
      <div class="price-row">
        <span>收盤價</span>
        <strong>{{ formatPrice(trade.yesterdayClose) }}</strong>
      </div>
    </div>
    <div class="spread-summary">
      <div class="spread-label">
        <span>與前一交易日價差</span>
        <small>前一交易日收盤</small>
      </div>
      <strong :class="trade.priceSpread < 0 ? 'negative' : trade.priceSpread > 0 ? 'positive' : ''">
        {{ trade.priceSpread > 0 ? '+' : '' }}{{ formatPrice(trade.priceSpread) }}
      </strong>
    </div>
  </article>
</template>

<style scoped>
/*
功能說明：設定最近交易卡片、價格列與漲跌提示的外觀。
輸入來源：行情卡片結構及價差狀態 class。
預期輸出：將交易資訊排列成容易閱讀的卡片。
*/
.info-card {
  min-width: 0;
  padding: 20px 20px 17px;
  border: 1px solid #e7ebf2;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 14px rgb(22 39 74 / 3%);
}

.card-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 18px;
}

.card-kicker {
  margin: 0 0 6px;
  color: #8792a4;
  font-size: 9px;
  font-weight: 700;
  letter-spacing: 1px;
}

h3 {
  margin: 0;
  color: #263248;
  font-size: 14px;
  font-weight: 700;
}

.date-chip {
  padding: 5px 7px;
  border-radius: 6px;
  background: #f3f5f9;
  color: #68758a;
  font-size: 9px;
  white-space: nowrap;
}

.price-list {
  display: grid;
  gap: 13px;
}

.price-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: #7d889a;
  font-size: 11px;
}

.price-row strong {
  color: #263248;
  font-size: 13px;
  font-weight: 650;
  font-variant-numeric: tabular-nums;
}

.price-row strong.negative {
  color: #16835d;
}

.price-row strong.positive {
  color: #d64d55;
}

.spread-summary {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 17px;
  padding: 11px 12px;
  border: 1px solid #edf0f5;
  border-radius: 8px;
  background: #f8f9fc;
}

.spread-label {
  display: grid;
  gap: 4px;
  color: #667389;
  font-size: 11px;
}

.spread-label small {
  color: #929cad;
  font-size: 9px;
}
</style>