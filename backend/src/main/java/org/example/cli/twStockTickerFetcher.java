package org.example.cli;

import org.example.client.TwseStockClient;
import org.example.model.MonthlyHighLow;
import org.example.model.MovingAverages;
import org.example.model.YesterdayPrice;
import org.example.dto.StockData;
import org.example.exception.InvalidStockCodeException;
import org.example.exception.MonthlyDataUnavailableException;
import org.example.exception.StockNotFoundException;
import org.example.exception.TwseApiException;
import org.example.service.StockService;

import java.io.PrintStream;
import java.util.Scanner;

/** 命令列查詢介面，與 REST API 共用 StockService 處理股票查詢。 */
public class twStockTickerFetcher {
    /** 反覆接收股票代號，使用共用服務查詢並顯示結果。 */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StockService stockService = new StockService(new TwseStockClient());
        new twStockTickerFetcher().run(scanner, System.out, stockService);
    }

/** 使用可注入的輸入、輸出與共用服務執行 CLI，也供自動化測試呼叫。 */
    void run(Scanner scanner, PrintStream output, StockService stockService) {
        while (true) {
            output.print("Enter a stock code (for example, 2330): ");
            String input = scanner.nextLine();
            try {
                StockData result = stockService.search(input);
                YesterdayPrice yesterday = result.getYesterdayPrice();
                MonthlyHighLow range = result.getMonthlyHighLow();
                MovingAverages averages = result.getMovingAverages();
                output.println("--- Stock search result ---");
                output.println(result.getTitle());
                output.println("Queried month: " + result.getQueriedMonth());
                output.println("Date: " + yesterday.getYesterdayDate());
                output.println("Open: " + yesterday.getYesterdayOpen());
                output.println("Close: " + yesterday.getYesterdayClose());
                output.println("Spread: " + yesterday.getPriceSpread());
                output.println("Monthly high: " + range.getHighest());
                output.println("Monthly low: " + range.getLowest());
                output.println("MA5: " + averages.getMa5());
                output.println("MA10: " + averages.getMa10());
                return;
            } catch (InvalidStockCodeException e) {
                output.println(e.getMessage());
            } catch (StockNotFoundException | MonthlyDataUnavailableException | TwseApiException e) {
                output.println("Search failed: " + e.getMessage());
            }
        }
    }
}
