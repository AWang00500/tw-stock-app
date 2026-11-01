<script setup>
// 功能說明：接收輸入值、提示與載入狀態，並定義回傳給父元件的事件。
// 輸入來源：父頁面透過 props 傳入的資料。
// 預期輸出：提供表單顯示所需狀態及更新、搜尋事件。
const props = defineProps({
  modelValue: {
    type: String,
    required: true,
  },
  notice: {
    type: String,
    default: '',
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:modelValue', 'search'])

// 功能說明：將輸入框的新文字通知父元件更新綁定值。
// 輸入來源：使用者輸入所觸發的 input 事件。
// 預期輸出：送出 update:modelValue 事件及輸入文字。
function handleInput(event) {
  emit('update:modelValue', event.target.value)
}

// 功能說明：送出表單中的股票代碼並移除前後空白。
// 輸入來源：目前的 modelValue 表單值。
// 預期輸出：送出 search 事件及整理後的代碼。
function handleSubmit() {
  emit('search', props.modelValue.trim())
}
</script>

<template>
  <!--
  功能說明：呈現股票代碼輸入框、搜尋按鈕與提示文字。
  輸入來源：父元件傳入的 modelValue、loading、notice，以及使用者輸入。
  預期輸出：顯示可輸入及送出查詢的表單，並透過事件回報父元件。
  -->
  <form class="search-panel" :aria-busy="loading" @submit.prevent="handleSubmit">
    <label for="stock-code">股票代號</label>
    <div class="search-controls">
      <div class="input-wrap">
        <input
          id="stock-code"
          :value="modelValue"
          name="stockCode"
          inputmode="numeric"
          autocomplete="off"
          placeholder="例如 2330"
          aria-describedby="search-hint"
          @input="handleInput"
        />
        <span class="input-suffix">TWSE</span>
      </div>
      <button type="submit">{{ loading ? '再次查詢' : '查詢股票' }}</button>
    </div>
    <p id="search-hint" class="form-hint" :class="{ 'has-notice': notice }" aria-live="polite">
      {{ notice || '輸入數字股票代號，例如 2330' }}
    </p>
  </form>
</template>

<style scoped>
/*
功能說明：設定查詢表單、輸入框、按鈕及提示文字的外觀。
輸入來源：本元件的表單結構與狀態 class。
預期輸出：呈現可辨識且適合窄螢幕的搜尋介面。
*/
.search-panel {
  padding: 22px 24px 17px;
  border: 1px solid #e7ebf2;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 5px 18px rgb(22 39 74 / 4%);
}

label {
  display: block;
  margin-bottom: 10px;
  color: #364156;
  font-size: 12px;
  font-weight: 650;
}

.search-controls {
  display: flex;
  gap: 10px;
}

.input-wrap {
  position: relative;
  flex: 1;
}

input {
  width: 100%;
  height: 46px;
  padding: 0 66px 0 14px;
  border: 1px solid #dce2ec;
  border-radius: 8px;
  background: #fff;
  color: #172033;
  font-size: 14px;
  transition: border-color 140ms ease, box-shadow 140ms ease;
}

input::placeholder {
  color: #a0a9b8;
}

input:focus {
  border-color: #6b95ed;
  box-shadow: 0 0 0 3px rgb(37 99 235 / 9%);
  outline: none;
}

.input-suffix {
  position: absolute;
  top: 50%;
  right: 13px;
  color: #9aa4b4;
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.6px;
  transform: translateY(-50%);
}

button {
  min-width: 112px;
  height: 46px;
  padding: 0 17px;
  border: 0;
  border-radius: 8px;
  background: #2563eb;
  color: #fff;
  font-size: 13px;
  font-weight: 650;
  transition: background 140ms ease, transform 140ms ease;
}

button:hover {
  background: #1d55d1;
}

button:active {
  transform: translateY(1px);
}

.form-hint {
  min-height: 15px;
  margin: 8px 0 0;
  color: #8a94a5;
  font-size: 11px;
}

.form-hint.has-notice {
  color: #52627b;
}

@media (max-width: 520px) {
  .search-panel {
    padding: 18px 16px 14px;
  }

  .search-controls {
    gap: 8px;
  }

  button {
    min-width: 96px;
    padding: 0 12px;
  }
}
</style>