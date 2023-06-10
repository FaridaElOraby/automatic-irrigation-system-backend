package io.irrigation.task.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.time.Duration;
import java.time.Instant;

@Controller
public class HomeController {
	private final Instant startTime = Instant.now();

	// Simple get API
	@GetMapping("/")
	@ResponseBody
	public String index() {
		return "Hello World! This is the irrigation system by Farida ElOraby";
	}

	// Health Check API that returns server status and upTime in seconds
	@GetMapping("/health")
	@ResponseBody
	public HealthCheckResponse healthCheck() {
		return new HealthCheckResponse("UP", Duration.between(startTime, Instant.now()).getSeconds());
	}

	@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
	// Health Check Response Class for the Health Check API
	private static class HealthCheckResponse {
		private final String status;
		private final long uptimeInSeconds;

		public HealthCheckResponse(String status, long l) {
			this.status = status;
			this.uptimeInSeconds = l;
		}
	}

}
