package com.codeone.hubspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication
public class HubspotApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubspotApplication.class, args);
	}

}
