<script setup>
// 功能說明：接收當月高低價資料並提供價格格式化函式。
// 輸入來源：父元件傳入的 range 物件及其中的價格數值。
// 預期輸出：供畫面呈現的高低價與格式化字串。
defineProps({
  range: {
    type: Object,
    required: true,
  },
})

// 功能說明：將價格轉成繁體中文地區格式，最多顯示兩位小數。
// 輸入來源：range 中的價格數值。
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
  功能說明：呈現查詢月份的最高價與最低價。
  輸入來源：父元件傳入的 range 資料。
  預期輸出：顯示格式化後的月內價格區間。
  -->
  <article class="info-card">
    <p class="card-kicker">MONTHLY RANGE</p>
    <h3>月內價格區間</h3>
    <div class="range-values">
      <div>
        <span class="value-label">最高價</span>
        <strong>{{ formatPrice(range.highest) }}</strong>
      </div>
      <div>
        <span class="value-label">最低價</span>
        <strong>{{ formatPrice(range.lowest) }}</strong>
      </div>
    </div>
    <p class="card-footnote">依實際查詢月份統計</p>
  </article>
</template>

<style scoped>
/*
功能說明：設定月內高低價卡片的標題、數值排列與補充文字外觀。
輸入來源：本元件的卡片結構。
預期輸出：呈現清楚分列的高低價格資訊。
*/
.info-card {
  min-width: 0;
  padding: 20px;
  border: 1px solid #e7ebf2;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 14px rgb(22 39 74 / 3%);
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

.range-values {
  display: grid;
  gap: 17px;
  margin-top: 23px;
}

.range-values > div {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.value-label {
  color: #7d889a;
  font-size: 11px;
}

.range-values strong {
  color: #263248;
  font-size: 15px;
  font-weight: 650;
  font-variant-numeric: tabular-nums;
}

.card-footnote {
  margin: 19px 0 0;
  padding-top: 12px;
  border-top: 1px solid #edf0f5;
  color: #9aa3b1;
  font-size: 10px;
}
</style>