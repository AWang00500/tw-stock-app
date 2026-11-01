<script setup>
import { computed } from 'vue'

const props = defineProps({
  stockCode: {
    type: String,
    required: true,
  },
  title: {
    type: String,
    required: true,
  },
  queriedMonth: {
    type: String,
    required: true,
  },
})

const stockName = computed(() => {
  const titlePattern = new RegExp(
    `(?:^|\\s)${props.stockCode}\\s+(.+?)\\s*各日成交資訊\\s*$`,
  )
  const match = props.title.trim().match(titlePattern)

  return match?.[1]?.trim() || props.stockCode
})
</script>

<template>
  <section class="summary" aria-label="股票摘要">
    <div class="identity">
      <span class="stock-code">{{ stockCode }}</span>
      <div>
        <h3>{{ stockName }}</h3>
      </div>
    </div>
    <div class="queried-month">
      <span>實際查詢月份</span>
      <strong>{{ queriedMonth }}</strong>
    </div>
  </section>
</template>

<style scoped>
.summary {
  display: flex;
  min-height: 100px;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  padding: 19px 22px;
  border: 1px solid #e7ebf2;
  border-radius: 12px;
  background: #fff;
}

.identity {
  display: flex;
  min-width: 0;
  align-items: center;
  gap: 14px;
}

.stock-code {
  display: grid;
  width: 52px;
  height: 52px;
  flex: 0 0 auto;
  place-items: center;
  border-radius: 11px;
  background: #eef4ff;
  color: #285dbf;
  font-size: 13px;
  font-weight: 700;
}

h3 {
  margin: 0 0 5px;
  color: #1c2638;
  font-size: 18px;
  font-weight: 700;
}

.queried-month {
  display: grid;
  flex: 0 0 auto;
  gap: 6px;
  text-align: right;
}

.queried-month span {
  color: #8993a4;
  font-size: 10px;
}

.queried-month strong {
  color: #344158;
  font-size: 13px;
  font-weight: 650;
}

@media (max-width: 520px) {
  .summary {
    min-height: 0;
    align-items: flex-start;
    padding: 17px 15px;
  }

  .identity {
    gap: 10px;
  }

  .stock-code {
    width: 44px;
    height: 44px;
  }

  h3 {
    font-size: 16px;
  }

  .queried-month span {
    max-width: 80px;
    line-height: 1.4;
  }
}
</style>