package org.example.controller;

import org.example.dto.ApiResponse;
import org.example.dto.StockData;
import org.example.service.StockService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 股票查詢 API 控制器，接收 HTTP 查詢並回傳統一格式的 JSON。 */
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    /** 依股票代號查詢資料，查詢和驗證由共用 Service 處理。 */
    @GetMapping("/{stockCode}")
    public ApiResponse<StockData> getStock(@PathVariable("stockCode") String stockCode) {
        return ApiResponse.success(stockService.search(stockCode));
    }
}
