package by;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;

public class Main {

	private static final Random RANDOM = new Random();
	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final String QUEUE_NAME = "rabbitQueue";

	private static final List<String> adjectives =
			Arrays.asList("hot", "cold", "funny", "scary", "sad", "happy", "tasty", "nasty", "lazy", "white", "black", "fat");
	private static final List<String> nouns =
			Arrays.asList("apple", "orange", "bear", "shark", "whale", "car", "toy", "boy", "house", "spy", "TV", "wheel");
	private static final List<String> verbs =
			Arrays.asList("eats", "loves", "throws", "consumes", "breaks", "drops", "hits", "hugs", "teaches", "watches");

	public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
		// Setting up connection
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(System.getenv("RABBIT_HOST"));
		factory.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();
		try {
			channel.queueDeclare(QUEUE_NAME, false, false, false, null);
			// Sending messages in infinite loop
			boolean executionInProgress = true;
			while (executionInProgress) {
				String message = getMessage();
				channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
				LOGGER.info(MessageFormat.format("Message sent: \"{0}\", timestamp: {1}", message, new Date()));
				Thread.sleep(3000);
			}
		} finally {
			channel.close();
			connection.close();
		}
	}

	private static String getMessage() {
		String message = MessageFormat.format(
				"{0} {1} {2} {3} {4}.",
				adjectives.get(RANDOM.nextInt(adjectives.size())),
				nouns.get(RANDOM.nextInt(nouns.size())),
				verbs.get(RANDOM.nextInt(verbs.size())),
				adjectives.get(RANDOM.nextInt(adjectives.size())),
				nouns.get(RANDOM.nextInt(nouns.size())));
		message = message.substring(0, 1).toUpperCase() + message.substring(1);
		return message;
	}
}
