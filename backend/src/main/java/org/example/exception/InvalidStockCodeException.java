package org.example.exception;

/** 表示股票代號格式不符規則的例外。 */
public class InvalidStockCodeException extends RuntimeException {
    public InvalidStockCodeException(String message) { super(message); }
}
