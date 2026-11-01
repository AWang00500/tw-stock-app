package org.example.client;

import org.example.exception.TwseApiException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/** 封裝 TWSE 每日股價 API 的 HTTP 連線與 JSON 解析。 */
@Component
public class TwseStockClient {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 向 TWSE 取得指定代號與月份的每日成交資料；連線失敗會轉為應用程式錯誤。 */
    public JsonNode fetch(String stockCode, String date) {
        String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date="
                + date + "&stockNo=" + stockCode;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new TwseApiException("TWSE 回應 HTTP " + response.statusCode() + "。");
            }
            return objectMapper.readTree(response.body());
        } catch (TwseApiException e) {
            throw e;
        } catch (Exception e) {
            throw new TwseApiException("無法連線至 TWSE 或解析回應。", e);
        }
    }
}
