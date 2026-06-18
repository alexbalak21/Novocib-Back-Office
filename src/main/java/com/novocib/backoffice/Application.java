package com.novocib.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.novocib.backoffice.auth.config.AuthModuleConfig;

@SpringBootApplication
@Import({
  AuthModuleConfig.class,
  StocksModuleConfig.class,
  InvoicesModuleConfig.class,
  OrdersModuleConfig.class,
  TimeTrackingModuleConfig.class
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
