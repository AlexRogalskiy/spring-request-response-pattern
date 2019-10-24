package com.api.sample.restful.producer.service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.api.sample.restful.models.Request;
import com.api.sample.restful.models.Response;
import com.api.sample.restful.producer.integration.QueueGateway;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Service
@Scope(SCOPE_SINGLETON)
@RequiredArgsConstructor
public class RequestAndResponseGateway {

	private final QueueGateway gateway;
	private final ObjectMapper mapper;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	public List<String> getMessages(int num) {
		List<Future<byte[]>> responses = new ArrayList<>();
		for (int index = 0; index < num; index++) {
			responses.add(threadPool.submit(submitRequest(index)));
		}

		return responses.stream().map(this::getResponse).collect(Collectors.toList());
	}

	private Callable<byte[]> submitRequest(int index) {
		return () -> {
			Request request = Request.builder().origin(1).messageIndex(index).build();
			return gateway.handle(request);
		};
	}

	private String getResponse(Future<byte[]> future) {
		try {
			return mapper.readValue(future.get(), Response.class).getMessage();
		} catch (IOException | InterruptedException | ExecutionException e) {
			throw new RuntimeException("Error getting message", e);
		}
	}

}
