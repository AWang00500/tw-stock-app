package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Spring Boot 應用程式入口；位於 org.example 根套件，可掃描底下各職責套件。 */
@SpringBootApplication
public class TwStockTickerFetcherApplication {
    /** 啟動 Spring Boot 伺服器與 REST API。 */
    public static void main(String[] args) {
        SpringApplication.run(TwStockTickerFetcherApplication.class, args);
    }
}
