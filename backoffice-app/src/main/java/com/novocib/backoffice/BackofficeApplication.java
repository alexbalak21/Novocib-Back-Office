package com.novocib.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.novocib.stocks.StocksModuleConfig;
import com.novocib.timetracking.TimeTrackingModuleConfig;

@SpringBootApplication
@Import({
    StocksModuleConfig.class,
    TimeTrackingModuleConfig.class
})
public class BackofficeApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackofficeApplication.class, args);
    }
}