<script setup>
// 功能說明：接收股票代碼、名稱文字與查詢月份，並整理顯示用名稱。
// 輸入來源：父頁面提供的股票摘要 props。
// 預期輸出：供摘要區顯示代碼、名稱及月份。
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

// 功能說明：從 API 標題文字擷取股票名稱，擷取不到時使用股票代碼。
// 輸入來源：props.title 與 props.stockCode。
// 預期輸出：回傳供摘要標題顯示的股票名稱。
const stockName = computed(() => {
  const titlePattern = new RegExp(
    `(?:^|\\s)${props.stockCode}\\s+(.+?)\\s*各日成交資訊\\s*$`,
  )
  const match = props.title.trim().match(titlePattern)

  return match?.[1]?.trim() || props.stockCode
})
</script>

<template>
  <!--
  功能說明：呈現股票代碼、整理後的名稱及查詢月份。
  輸入來源：父元件傳入的股票摘要 props。
  預期輸出：顯示股票識別與查詢期間摘要。
  -->
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
/*
功能說明：設定股票摘要卡片及窄螢幕下的排列。
輸入來源：摘要元件的結構與螢幕寬度。
預期輸出：清楚排列股票識別資訊與查詢月份。
*/
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