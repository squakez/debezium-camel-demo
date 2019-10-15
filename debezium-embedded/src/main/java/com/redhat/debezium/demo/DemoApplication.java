package com.redhat.debezium.demo;

import com.redhat.debezium.demo.debezium.DebeziumConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableAutoConfiguration
@EnableJpaRepositories
public class DemoApplication {

	public static void main(String[] args) {
		DebeziumConfiguration debezium = new DebeziumConfiguration();
		debezium.init();
		SpringApplication.run(DemoApplication.class, args);
	}

}
