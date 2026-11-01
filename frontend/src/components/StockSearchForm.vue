<script setup>
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

function handleInput(event) {
  emit('update:modelValue', event.target.value)
}

function handleSubmit() {
  emit('search', props.modelValue.trim())
}
</script>

<template>
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