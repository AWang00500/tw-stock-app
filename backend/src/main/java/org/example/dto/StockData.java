package org.example.dto;

import org.example.model.MonthlyHighLow;
import org.example.model.MovingAverages;
import org.example.model.YesterdayPrice;

/** 股票查詢結果資料，作為 API 的 data 內容。 */
public class StockData {
    private final String stockCode;
    private final String title;
    private final String queriedMonth;
    private final YesterdayPrice yesterdayPrice;
    private final MonthlyHighLow monthlyHighLow;
    private final MovingAverages movingAverages;

    public StockData(String stockCode, String title, String queriedMonth, YesterdayPrice yesterdayPrice,
                     MonthlyHighLow monthlyHighLow, MovingAverages movingAverages) {
        this.stockCode = stockCode;
        this.title = title;
        this.queriedMonth = queriedMonth;
        this.yesterdayPrice = yesterdayPrice;
        this.monthlyHighLow = monthlyHighLow;
        this.movingAverages = movingAverages;
    }

    public String getStockCode() { return stockCode; }
    public String getTitle() { return title; }
    public String getQueriedMonth() { return queriedMonth; }
    public YesterdayPrice getYesterdayPrice() { return yesterdayPrice; }
    public MonthlyHighLow getMonthlyHighLow() { return monthlyHighLow; }
    public MovingAverages getMovingAverages() { return movingAverages; }
}
