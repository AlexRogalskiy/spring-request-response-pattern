package com.api.sample.restful.producer;

import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;

import com.api.sample.restful.producer.integration.GatewayChannels;

@SpringBootApplication
@EnableBinding(GatewayChannels.class)
@EnableIntegration
@IntegrationComponentScan
public class ProducerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(ProducerApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "65056"));
		app.run(args);
	}
}
