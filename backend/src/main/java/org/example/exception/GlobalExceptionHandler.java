package org.example.exception;

import org.example.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 將應用程式例外轉換為固定格式的 API 錯誤回應。 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
/** 將代號格式錯誤轉為 HTTP 400 與統一錯誤格式。 */
    @ExceptionHandler(InvalidStockCodeException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCode(InvalidStockCodeException e) {
        return ResponseEntity.badRequest().body(ApiResponse.failure("INVALID_STOCK_CODE", e.getMessage()));
    }

/** 將查無資料或空資料轉為 HTTP 404。 */
    @ExceptionHandler(StockNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(StockNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("STOCK_CODE_NOT_FOUND", e.getMessage()));
    }

    @ExceptionHandler(MonthlyDataUnavailableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMonthlyDataUnavailable(MonthlyDataUnavailableException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("MONTH_DATA_UNAVAILABLE", e.getMessage()));
    }

/** 將 TWSE 上游連線錯誤轉為 HTTP 502。 */
    @ExceptionHandler(TwseApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleTwse(TwseApiException e) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.failure("TWSE_API_ERROR", e.getMessage()));
    }

/** 處理其他未預期錯誤，並回傳不暴露內部細節的通用訊息。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception e) {
        logger.error("Unexpected API error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.failure("INTERNAL_ERROR", "查詢時發生未預期錯誤。"));
    }
}
