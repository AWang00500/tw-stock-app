package org.example.model;

/** 儲存 MA5 與 MA10；當有效日數不足時，個別均線可為 null。 */
public class MovingAverages {
    //五日均線、十日均線
    final Double ma5;
    final Double ma10;

    public MovingAverages(double ma5, double ma10) {
        this.ma5 = ma5;
        this.ma10 = ma10;
    }

    public MovingAverages(Double ma5, Double ma10) {
        this.ma5 = ma5;
        this.ma10 = ma10;
    }

    public Double getMa5() { return ma5; }
    public Double getMa10() { return ma10; }
}
