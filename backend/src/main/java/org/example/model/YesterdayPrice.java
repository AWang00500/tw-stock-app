package org.example.model;

import java.time.LocalDate;

/** 儲存最新一筆交易的日期、開收盤與價差。 */
public class YesterdayPrice {
    // 昨日日期、昨日開盤價、昨日收盤價、昨日價差
    final LocalDate yesterdayDate;
    final double yesterdayOpen;
    final double yesterdayClose;
    final double priceSpread;

    public YesterdayPrice(LocalDate yesterdayDate, double yesterdayOpen, double yesterdayClose, double priceSpread) {
        this.yesterdayDate = yesterdayDate;
        this.yesterdayOpen = yesterdayOpen;
        this.yesterdayClose = yesterdayClose;
        this.priceSpread = priceSpread;
    }

    public LocalDate getYesterdayDate() { return yesterdayDate; }
    public double getYesterdayOpen() { return yesterdayOpen; }
    public double getYesterdayClose() { return yesterdayClose; }
    public double getPriceSpread() { return priceSpread; }
}
