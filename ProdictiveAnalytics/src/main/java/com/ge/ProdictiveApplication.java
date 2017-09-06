package com.ge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.ge.config.ApplicationInitializer;


@SpringBootApplication
@EnableScheduling
public class ProdictiveApplication {
	

	public static void main(String[] args) {
		new SpringApplicationBuilder(ProdictiveApplication.class)
	.initializers(new ApplicationInitializer())
	.run(args);}
}
