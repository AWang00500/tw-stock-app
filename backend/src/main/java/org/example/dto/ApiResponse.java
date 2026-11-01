package org.example.dto;

/** 所有 API 成功與失敗回應共用的 JSON 外層格式。 */
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final String errorCode;
    private final String message;

    private ApiResponse(boolean success, T data, String errorCode, String message) {
        this.success = success;
        this.data = data;
        this.errorCode = errorCode;
        this.message = message;
    }

    /** 建立成功回應，資料放在 data 欄位。 */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, "查詢成功");
    }

    /** 建立錯誤回應，供全域例外處理器回傳。 */
    public static <T> ApiResponse<T> failure(String errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }

    public boolean isSuccess() { return success; }
    public T getData() { return data; }
    public String getErrorCode() { return errorCode; }
    public String getMessage() { return message; }
}
