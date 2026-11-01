package org.example.exception;

/** 表示 TWSE 查無股票或沒有可用資料的例外。 */
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) { super(message); }
}
