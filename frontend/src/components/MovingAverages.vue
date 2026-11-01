<script setup>
// 功能說明：接收均線資料並提供可顯示的數值格式。
// 輸入來源：父元件傳入的 averages 物件。
// 預期輸出：供畫面顯示的 MA5、MA10 數值或無資料文字。
defineProps({
  averages: {
    type: Object,
    required: true,
  },
})

// 功能說明：處理均線值為空或有效數字的顯示格式。
// 輸入來源：averages 中的單一均線值。
// 預期輸出：無資料提示或格式化後的均線字串。
function formatAverage(value) {
  if (value === null) {
    return '資料不足'
  }

  return new Intl.NumberFormat('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(value)
}
</script>

<template>
  <!--
  功能說明：呈現 MA5 與 MA10 均線及補充說明。
  輸入來源：父元件傳入的 averages 資料。
  預期輸出：顯示可用均線數值，並標示無資料的項目。
  -->
  <article class="info-card">
    <p class="card-kicker">MOVING AVERAGES</p>
    <h3>移動平均</h3>
    <div class="average-list">
      <div class="average-row">
        <span class="average-period"><span class="period-mark ma5"></span>MA5</span>
        <strong :class="{ unavailable: averages.ma5 === null }">{{ formatAverage(averages.ma5) }}</strong>
      </div>
      <div class="average-row">
        <span class="average-period"><span class="period-mark ma10"></span>MA10</span>
        <strong :class="{ unavailable: averages.ma10 === null }">{{ formatAverage(averages.ma10) }}</strong>
      </div>
    </div>
    <p class="card-footnote">交易日不足時均線可能無資料</p>
  </article>
</template>

<style scoped>
/*
功能說明：設定均線卡片、週期標記及無資料數值的外觀。
輸入來源：本元件的卡片結構與 unavailable 狀態。
預期輸出：讓均線週期和數值狀態容易辨識。
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

.average-list {
  display: grid;
  gap: 16px;
  margin-top: 23px;
}

.average-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.average-period {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #667389;
  font-size: 11px;
  font-weight: 600;
}

.period-mark {
  width: 8px;
  height: 8px;
  border-radius: 3px;
}

.period-mark.ma5 {
  background: #4b83ef;
}

.period-mark.ma10 {
  background: #63b6a2;
}

.average-row strong {
  color: #263248;
  font-size: 15px;
  font-weight: 650;
  font-variant-numeric: tabular-nums;
  text-align: right;
}

.average-row strong.unavailable {
  color: #929cad;
  font-size: 11px;
  font-weight: 500;
}

.card-footnote {
  margin: 19px 0 0;
  padding-top: 12px;
  border-top: 1px solid #edf0f5;
  color: #9aa3b1;
  font-size: 10px;
}
</style>