package org.example.cli;

import org.example.dto.StockData;
import org.example.exception.InvalidStockCodeException;
import org.example.exception.TwseApiException;
import org.example.model.MonthlyHighLow;
import org.example.model.MovingAverages;
import org.example.model.YesterdayPrice;
import org.example.service.StockService;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 以模擬的 StockService 驗證 CLI 輸入、顯示與錯誤後繼續輸入的行為。 */
class StockTickerCliTest {
    @Test
    void displaysStockDataReturnedBySharedService() {
        StockService stockService = mock(StockService.class);
        when(stockService.search("2330")).thenReturn(sampleStockData());
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);

        new twStockTickerFetcher().run(new Scanner("2330\n"), output, stockService);

        String text = capturedOutput.toString(StandardCharsets.UTF_8);
        assertTrue(text.contains("Example Company"));
        assertTrue(text.contains("Close: 105.0"));
        assertTrue(text.contains("Queried month: 2026-10"));
        verify(stockService, times(1)).search("2330");
    }

    @Test
    void reportsInvalidInputAndContinuesToTheNextEntry() {
        StockService stockService = mock(StockService.class);
        when(stockService.search("abc")).thenThrow(new InvalidStockCodeException("Digits only"));
        when(stockService.search("2330")).thenReturn(sampleStockData());
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);

        new twStockTickerFetcher().run(new Scanner("abc\n2330\n"), output, stockService);

        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("Digits only"));
        verify(stockService, times(1)).search("abc");
        verify(stockService, times(1)).search("2330");
    }

    @Test
    void reportsTwseFailureAndContinuesToTheNextEntry() {
        StockService stockService = mock(StockService.class);
        when(stockService.search("2330"))
                .thenThrow(new TwseApiException("TWSE unavailable"))
                .thenReturn(sampleStockData());
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream output = new PrintStream(capturedOutput, true, StandardCharsets.UTF_8);

        new twStockTickerFetcher().run(new Scanner("2330\n2330\n"), output, stockService);

        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("Search failed: TWSE unavailable"));
        verify(stockService, times(2)).search("2330");
    }

    private StockData sampleStockData() {
        return new StockData(
                "2330",
                "Example Company",
                "2026-10",
                new YesterdayPrice(LocalDate.of(2026, 10, 30), 100.0, 105.0, 5.0),
                new MonthlyHighLow(110.0, 90.0),
                new MovingAverages(102.0, 101.0));
    }
}
