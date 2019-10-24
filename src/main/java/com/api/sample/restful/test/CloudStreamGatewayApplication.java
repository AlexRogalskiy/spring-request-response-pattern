package com.api.sample.restful.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding({ Processor.class, CloudStreamGatewayApplication.GatewayChannels.class })
@SpringBootApplication
public class CloudStreamGatewayApplication {

	interface GatewayChannels {

		String REQUEST = "request";

		@Output(REQUEST)
		MessageChannel request();

		String REPLY = "reply";

		@Input(REPLY)
		SubscribableChannel reply();
	}

	private static final String ENRICH = "enrich";

	@MessagingGateway
	public interface StreamGateway {

		@Gateway(requestChannel = ENRICH, replyChannel = GatewayChannels.REPLY)
		String process(String payload);

	}

	@Bean
	public IntegrationFlow headerEnricherFlow() {
		return IntegrationFlows.from(ENRICH).enrichHeaders(HeaderEnricherSpec::headerChannelsToString)
				.channel(GatewayChannels.REQUEST).get();
	}

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public Message<?> process(Message<String> request) {
		return MessageBuilder.withPayload(request.getPayload().toUpperCase()).copyHeaders(request.getHeaders()).build();
	}

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = SpringApplication.run(CloudStreamGatewayApplication.class,
				args);

		StreamGateway gateway = applicationContext.getBean(StreamGateway.class);

		String result = gateway.process("foo");

		System.out.println(result);
	}

}
