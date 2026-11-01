package org.example.model;

/** 儲存查詢月份的最高價與最低價。 */
public class MonthlyHighLow {
    // 月最高價、月最低價
    final double highest;
    final double lowest;

    public MonthlyHighLow(double highest, double lowest) {
        this.highest = highest;
        this.lowest= lowest;
    }

    public double getHighest() { return highest; }
    public double getLowest() { return lowest; }


}
