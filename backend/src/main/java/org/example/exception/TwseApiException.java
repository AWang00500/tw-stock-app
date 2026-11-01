package org.example.exception;

/** 表示 TWSE 連線、HTTP 回應或 JSON 解析失敗的例外。 */
public class TwseApiException extends RuntimeException {
    public TwseApiException(String message) { super(message); }
    public TwseApiException(String message, Throwable cause) { super(message, cause); }
}
