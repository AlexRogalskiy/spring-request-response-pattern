package com.api.sample.restful.producer.service;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.context.annotation.Scope;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import com.api.sample.restful.models.Request;
import com.api.sample.restful.producer.integration.GatewayChannels;

import lombok.RequiredArgsConstructor;

@Service
@Scope(SCOPE_SINGLETON)
@RequiredArgsConstructor
public class RequestAndResponseProducer {

	private final GatewayChannels queue;
	private ThreadLocal<CountDownLatch> barrier = new ThreadLocal<>();
	private ThreadLocal<List<String>> messages = new ThreadLocal<>();

	public List<String> getMessages(int num) throws InterruptedException {
		messages.set(new ArrayList<>());
		barrier.set(new CountDownLatch(num));

		for (int index = 0; index < num; index++) {
			Request request = Request.builder().origin(1).messageIndex(index).build();
			queue.request().send(MessageBuilder.withPayload(request).setHeader("approach", "queues").build());
			Thread.sleep(100);
		}

		barrier.get().await();

		return messages.get();
	}

}
