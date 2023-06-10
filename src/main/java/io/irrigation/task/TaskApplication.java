package io.irrigation.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class TaskApplication {

	public static void main(final String[] args) {
		SpringApplication.run(TaskApplication.class, args);
	}

}
