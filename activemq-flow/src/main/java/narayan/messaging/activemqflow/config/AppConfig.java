package narayan.messaging.activemqflow.config;

import javax.jms.ConnectionFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.LoggingHandler.Level;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.messaging.MessageChannel;

/**
 * 
 * @author narayana
 *
 */
@Configuration
@EnableIntegration
public class AppConfig {

	@Bean
	public MessageChannel outputChannel() {
		return MessageChannels.direct().get();
	}
	
	@Bean
	public MessageChannel errorChannel() {
		return MessageChannels.direct().get();
	}
	
	@Bean
	public IntegrationFlow dispatchFlow(ConnectionFactory connectionFactory, @Value("${destination}") String destination) {
		return IntegrationFlows
				.from(Jms
						.messageDrivenChannelAdapter(connectionFactory).destination(destination)
				 .outputChannel(outputChannel())
				 .errorChannel(errorChannel()))
				.get();
	}
	
	@Bean
	public IntegrationFlow processFlow() {
		return IntegrationFlows
				.from(outputChannel()).handle( message -> {
					System.out.println("HEADERS :: " + message.getHeaders());
					System.out.println("PAYLOAD :: " + message.getPayload());
				})
				.get();
	}
	
	@Bean
	public IntegrationFlow errorFlow() {
		return IntegrationFlows
				.from(errorChannel()).handle(new LoggingHandler(Level.ERROR))
				.get();
	}

	
}
