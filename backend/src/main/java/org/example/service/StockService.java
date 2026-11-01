package org.example.service;

import org.example.model.MonthlyHighLow;
import org.example.model.MovingAverages;
import org.example.model.YesterdayPrice;
import org.example.client.TwseStockClient;
import org.example.dto.StockData;
import org.example.exception.InvalidStockCodeException;
import org.example.exception.MonthlyDataUnavailableException;
import org.example.exception.StockNotFoundException;
import org.example.exception.TwseApiException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/** 股票查詢核心服務，供 CLI 與 REST API 共用。 */
@Service
public class StockService {
    private static final int MAX_MONTHS_TO_CHECK = 3;
    private final TwseStockClient stockClient;

    public StockService(TwseStockClient stockClient) {
        this.stockClient = stockClient;
    }

    /** 驗證代號、取得 TWSE 資料，並計算股票摘要與均線。 */
    public StockData search(String stockCode) {
        return search(stockCode, YearMonth.now());
    }

    StockData search(String stockCode, YearMonth currentMonth) {
        if (stockCode == null || stockCode.isBlank() || !stockCode.matches("[0-9]+")) {
            throw new InvalidStockCodeException("Stock code must contain digits only.");
        }

        for (int monthsBack = 0; monthsBack < MAX_MONTHS_TO_CHECK; monthsBack++) {
            YearMonth queriedMonth = currentMonth.minusMonths(monthsBack);
            String date = queriedMonth.atDay(1).format(DateTimeFormatter.BASIC_ISO_DATE);
            JsonNode root = stockClient.fetch(stockCode, date);
            if (root == null || !root.isObject()) {
                throw new TwseApiException("TWSE returned an invalid JSON response.");
            }

            JsonNode statusNode = root.get("stat");
            String status = statusNode == null || statusNode.isNull() ? null : statusNode.asText();
            if (!"OK".equals(status)) {
                if (isExplicitUnknownStockCode(status)) {
                    throw new StockNotFoundException("TWSE explicitly reported that stock code "
                            + stockCode + " does not exist.");
                }
                if (isNoDataResponse(status)) {
                    continue;
                }
                throw new TwseApiException("TWSE returned an unexpected response status.");
            }

            JsonNode dataArray = root.path("data");
            if (!dataArray.isArray()) {
                throw new TwseApiException("TWSE response did not contain a valid data array.");
            }
            if (dataArray.isEmpty()) {
                continue;
            }

            YesterdayPrice yesterday = yesterdayPrice(dataArray);
            MonthlyHighLow range = monthlyHighLow(dataArray);
            MovingAverages averages = movingAverages(dataArray);
            return new StockData(stockCode, root.path("title").asText().trim(), queriedMonth.toString(),
                    yesterday, range, averages);
        }

        throw new MonthlyDataUnavailableException("No trading data was available for stock code "
                + stockCode + " in the current or previous two months.");
    }

    private boolean isNoDataResponse(String status) {
        if (status == null) return false;
        String normalized = status.toLowerCase(Locale.ROOT);
        return status.contains("沒有符合條件的資料")
                || status.contains("無符合條件資料")
                || normalized.contains("no data")
                || normalized.contains("no records");
    }

    private boolean isExplicitUnknownStockCode(String status) {
        if (status == null) return false;
        return status.contains("查無此證券代號")
                || status.contains("查無此股票代號")
                || status.contains("無此股票代號")
                || status.contains("股票代號不存在")
                || status.toLowerCase(Locale.ROOT).contains("stock code does not exist")
                || status.toLowerCase(Locale.ROOT).contains("invalid stock code");
    }

    private double parsePrice(String priceStr) {
        String cleaned = priceStr.replaceAll("[^0-9.+-]", "");
        return Double.parseDouble(cleaned);
    }

/** 將 TWSE 使用的民國年月日轉為 Java LocalDate。 */
    private LocalDate parseRocDate(String rocDateStr) {
        String[] parts = rocDateStr.split("/");
        int year = Integer.parseInt(parts[0]) + 1911;
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);
        return LocalDate.of(year, month, day);
    }

    private YesterdayPrice yesterdayPrice(JsonNode dataArray) {
        int lastIndex = dataArray.size() - 1;
        JsonNode lastRow = dataArray.get(lastIndex);
        LocalDate date = parseRocDate(lastRow.get(0).asText());
        double open = parsePrice(lastRow.get(3).asText());
        double close = parsePrice(lastRow.get(6).asText());
        double spread = parsePrice(lastRow.get(7).asText());
        return new YesterdayPrice(date, open, close, spread);
    }

/** 逐筆檢視當月每日高低價，並保留全月極值。 */
    private MonthlyHighLow monthlyHighLow(JsonNode dataArray) {
        double highest = parsePrice(dataArray.get(0).get(4).asText());
        double lowest = parsePrice(dataArray.get(0).get(5).asText());
        for (int i = 0; i < dataArray.size(); i++) {
            JsonNode row = dataArray.get(i);
            double dayHigh = parsePrice(row.get(4).asText());
            double dayLow = parsePrice(row.get(5).asText());
            highest = Math.max(highest, dayHigh);
            lowest = Math.min(lowest, dayLow);
        }
        return new MonthlyHighLow(highest, lowest);
    }

    /** 資料筆數不足時回傳 null；其他情況保留原有均線公式。 */
    private Double calculateMovingAverage(JsonNode dataArray, int days) {
        if (dataArray.size() < days) return null;
        double sum = 0;
        int lastIndex = dataArray.size() - 1;
        for (int i = lastIndex; i > lastIndex - days; i--) {
            double close = parsePrice(dataArray.get(i).get(6).asText());
            sum += close;
        }
        return sum / days;
    }

    private MovingAverages movingAverages(JsonNode dataArray) {
        Double ma5 = calculateMovingAverage(dataArray, 5);
        Double ma10 = calculateMovingAverage(dataArray, 10);
        return new MovingAverages(ma5, ma10);
    }
}
