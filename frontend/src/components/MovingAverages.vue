<script setup>
defineProps({
  averages: {
    type: Object,
    required: true,
  },
})

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