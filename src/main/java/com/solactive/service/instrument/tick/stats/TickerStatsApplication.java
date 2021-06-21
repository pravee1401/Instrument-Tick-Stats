package com.solactive.service.instrument.tick.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class TickerStatsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickerStatsApplication.class, args);
	}

}
