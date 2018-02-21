package eu.toop.tooppackagetracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ToopPackageTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ToopPackageTrackerApplication.class, args);
	}
}
