package com.api.sample.restful.producer.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Payload;

import com.api.sample.restful.models.Request;

@MessagingGateway
public interface QueueGateway {
	@Gateway(requestChannel = IntegrationFlowDefinitions.HANDLER_FLOW, replyChannel = GatewayChannels.REPLY)
	byte[] handle(@Payload Request payload);
}