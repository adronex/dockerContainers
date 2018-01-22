package by;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.rabbitmq.client.*;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static spark.Spark.*;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
	private static final ObjectMapper JSON = new ObjectMapper();
	private static final MysqlDataSource JDBC = new MysqlDataSource();
	private static final String QUEUE_NAME = "rabbitQueue";

	private static final List<String> receivedMessages = new ArrayList<>();

	public static void main (String[] args) throws IOException, TimeoutException {
		setupDatabaseConnection();
		setupRabbitMq();
		setupSpark();
	}

	private static void setupDatabaseConnection() {
		Flyway flyway = new Flyway();
		JDBC.setUser(System.getenv("MYSQL_USER"));
		JDBC.setPassword(System.getenv("MYSQL_PASSWORD"));
		JDBC.setServerName(System.getenv("MYSQL_HOST"));
		JDBC.setDatabaseName(System.getenv("MYSQL_DATABASE"));
		JDBC.setPort(Integer.parseInt(System.getenv("MYSQL_PORT")));
		flyway.setDataSource(JDBC);
		flyway.migrate();
	}

	private static void setupRabbitMq() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(System.getenv("RABBIT_HOST"));
		factory.setPort(Integer.parseInt(System.getenv("RABBIT_PORT")));
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		LOGGER.info("Waiting for messages. To exit press CTRL+C");

		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope,
									   AMQP.BasicProperties properties, byte[] body)
					throws IOException {
				String message = new String(body, "UTF-8");
				try {
					saveMessage(message);
				} catch (SQLException e) {
					LOGGER.error(e.getMessage(), e);
				}
				LOGGER.info(MessageFormat.format("Message received: \"{0}\", timestamp: {1}", message, new Date()));
			}
		};
		channel.basicConsume(QUEUE_NAME, true, consumer);
	}

	private static void setupSpark() {
		port(Integer.parseInt(System.getenv("SPARK_PORT")));
		options("/*", (request, response) -> {
			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");

			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}
			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

			if (accessControlRequestMethod != null) {
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}
			return "OK";
		});
		before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
		get("/messages", (request, response) -> getAllMessagesAsString());
	}

	private static String getAllMessagesAsString() throws JsonProcessingException {
		return JSON.writeValueAsString(receivedMessages);
	}

	private static void saveMessage(String message) throws SQLException {
		receivedMessages.add(message);
		PreparedStatement statement = JDBC.getConnection().prepareStatement("INSERT INTO messages(message, received) VALUES (?, ?)");
		statement.setString(1, message);
		statement.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
		statement.executeUpdate();
	}
}
