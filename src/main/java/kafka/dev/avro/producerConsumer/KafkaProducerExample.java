package kafka.dev.avro.producerConsumer;

import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import kafka.dev.avro.specific.Customer;

// After sending Avro data to kafka, we can use this consumer:
// $> kafka-avro-console-consumer --bootstrap-server 127.0.0.1:9092 --topic customer-avro --from-beginning --property schema.registry.url=http://127.0.0.1:8081

public class KafkaProducerExample {
	public static void main(String[] args) {
		Properties properties = new Properties();

		// Regular producer configuration.
		properties.setProperty("bootstrap.servers", "127.0.0.1:9092");
		properties.setProperty("acks", "all");
		properties.setProperty("retries", "10");

		// Avro configuration.
		properties.setProperty("key.serializer", StringSerializer.class.getName());
		properties.setProperty("value.serializer", KafkaAvroSerializer.class.getName());
		properties.setProperty("schema.registry.url", "http://127.0.0.1:8081");

		Producer<String, Customer> kafkaProducer = new KafkaProducer<String, Customer>(properties);
		String topic = "customer-avro";

		Customer customer = Customer.newBuilder().setFirstName("John").setLastName("Doe").setAge(26).setHeight(185.5f)
				.setWeight(85.6f).setAutomatedEmail(false).build();

		ProducerRecord<String, Customer> producerRecord = new ProducerRecord<String, Customer>(topic, customer);

		kafkaProducer.send(producerRecord, new Callback() {

			@Override
			public void onCompletion(RecordMetadata metadata, Exception exception) {
				if (exception == null) {
					System.out.println("Success");
					System.out.println(metadata.toString());
				} else {
					exception.printStackTrace();
				}
			}
		});

		kafkaProducer.flush();
		kafkaProducer.close();
	}
}