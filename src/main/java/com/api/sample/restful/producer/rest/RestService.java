package com.api.sample.restful.producer.rest;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.sample.restful.producer.service.RequestAndResponseGateway;
import com.api.sample.restful.producer.service.RequestAndResponseProducer;

import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RestService {
	private final RequestAndResponseProducer queueProducer;
	private final RequestAndResponseGateway integrationProducer;

	@GetMapping("/queue/messages/{num}")
	public List<String> getByNameUsingQueues(@NotNull @PathVariable("num") Integer num) throws InterruptedException {
		return queueProducer.getMessages(num);
	}

	@GetMapping("/gateway/messages/{num}")
	public List<String> getByNameUsingGateway(@NotNull @PathVariable("num") Integer num) {
		return integrationProducer.getMessages(num);
	}
}
