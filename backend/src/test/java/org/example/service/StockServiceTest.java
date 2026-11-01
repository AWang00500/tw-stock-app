package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.client.TwseStockClient;
import org.example.dto.StockData;
import org.example.exception.MonthlyDataUnavailableException;
import org.example.exception.StockNotFoundException;
import org.example.exception.TwseApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
/** 使用模擬 TWSE JSON 回應，驗證月份回退與錯誤處理。 */
class StockServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private TwseStockClient stockClient;

    private StockService stockService;

    @BeforeEach
    void setUp() {
        stockService = new StockService(stockClient);
    }

    @Test
    void usesCurrentMonthWhenItHasDataAndKeepsExistingCalculations() throws Exception {
        when(stockClient.fetch("2330", "20261101")).thenReturn(okResponse("115/11/02"));

        StockData result = stockService.search("2330", YearMonth.of(2026, 11));

        assertEquals("2026-11", result.getQueriedMonth());
        assertEquals(105.0, result.getYesterdayPrice().getYesterdayClose());
        assertEquals(110.0, result.getMonthlyHighLow().getHighest());
        assertEquals(90.0, result.getMonthlyHighLow().getLowest());
        assertNull(result.getMovingAverages().getMa5());
        assertNull(result.getMovingAverages().getMa10());
        verify(stockClient, times(1)).fetch("2330", "20261101");
    }

    @Test
    void fallsBackToPreviousMonthAndReportsTheMonthUsed() throws Exception {
        when(stockClient.fetch("2330", "20261101")).thenReturn(noDataResponse());
        when(stockClient.fetch("2330", "20261001")).thenReturn(okResponse("115/10/30"));

        StockData result = stockService.search("2330", YearMonth.of(2026, 11));

        assertEquals("2026-10", result.getQueriedMonth());
        verify(stockClient, times(1)).fetch("2330", "20261101");
        verify(stockClient, times(1)).fetch("2330", "20261001");
    }

    @Test
    void fallsBackAcrossYearBoundary() throws Exception {
        when(stockClient.fetch("2330", "20270101")).thenReturn(noDataResponse());
        when(stockClient.fetch("2330", "20261201")).thenReturn(noDataResponse());
        when(stockClient.fetch("2330", "20261101")).thenReturn(okResponse("115/11/30"));

        StockData result = stockService.search("2330", YearMonth.of(2027, 1));

        assertEquals("2026-11", result.getQueriedMonth());
        verify(stockClient, times(1)).fetch("2330", "20270101");
        verify(stockClient, times(1)).fetch("2330", "20261201");
        verify(stockClient, times(1)).fetch("2330", "20261101");
    }

    @Test
    void stopsAfterThreeMonthsWhenAllHaveNoData() throws Exception {
        when(stockClient.fetch("2330", "20261101")).thenReturn(noDataResponse());
        when(stockClient.fetch("2330", "20261001")).thenReturn(noDataResponse());
        when(stockClient.fetch("2330", "20260901")).thenReturn(noDataResponse());

        assertThrows(MonthlyDataUnavailableException.class,
                () -> stockService.search("2330", YearMonth.of(2026, 11)));

        verify(stockClient, times(1)).fetch("2330", "20261101");
        verify(stockClient, times(1)).fetch("2330", "20261001");
        verify(stockClient, times(1)).fetch("2330", "20260901");
    }

    @Test
    void reportsUnknownCodeOnlyWhenTwseExplicitlySaysSo() throws Exception {
        when(stockClient.fetch("9999", "20261101"))
                .thenReturn(objectMapper.readTree("{\"stat\":\"Stock code does not exist\"}"));

        assertThrows(StockNotFoundException.class,
                () -> stockService.search("9999", YearMonth.of(2026, 11)));

        verify(stockClient, times(1)).fetch("9999", "20261101");
    }

    @Test
    void propagatesTwseFailureWithoutTreatingItAsMissingMonthData() throws Exception {
        when(stockClient.fetch("2330", "20261101"))
                .thenThrow(new TwseApiException("TWSE unavailable"));

        assertThrows(TwseApiException.class,
                () -> stockService.search("2330", YearMonth.of(2026, 11)));

        verify(stockClient, times(1)).fetch("2330", "20261101");
    }

    private JsonNode noDataResponse() throws Exception {
        return objectMapper.readTree("{\"stat\":\"\\u5f88\\u62b1\\u6b49\\uff0c\\u6c92\\u6709\\u7b26\\u5408\\u689d\\u4ef6\\u7684\\u8cc7\\u6599!\",\"total\":0}");
    }

    private JsonNode okResponse(String rocDate) throws Exception {
        String json = "{\"stat\":\"OK\",\"title\":\"115年11月 2330 台積電\",\"data\":[[\""
                + rocDate + "\",\"100\",\"1000\",\"100.00\",\"110.00\",\"90.00\",\"105.00\",\"+5.00\"]]}";
        return objectMapper.readTree(json);
    }
}
