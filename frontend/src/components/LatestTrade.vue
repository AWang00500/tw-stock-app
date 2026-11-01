<script setup>
defineProps({
  trade: {
    type: Object,
    required: true,
  },
})

function formatPrice(value) {
  return new Intl.NumberFormat('zh-TW', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2,
  }).format(value)
}
</script>

<template>
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