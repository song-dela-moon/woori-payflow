package com.card.payment.clearingsettlement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ClearingSettlementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClearingSettlementServiceApplication.class, args);
	}

}
