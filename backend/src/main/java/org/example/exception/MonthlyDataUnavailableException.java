package org.example.exception;

/** 表示最近三個查詢月份都沒有可用成交資料。 */
public class MonthlyDataUnavailableException extends RuntimeException {
    public MonthlyDataUnavailableException(String message) {
        super(message);
    }
}
