package io.irrigation.task.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.irrigation.task.model.dto.sensordto.SensorDTO;
import io.irrigation.task.model.dto.sensordto.UpdateSensorDTO;

import io.irrigation.task.service.SensorService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/sensors", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SensorController {

	private final SensorService SensorService;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Get API for listing Sensors
	// Returns sensorId, status, and water dispense per second
	@Operation(summary = "Get all sensors", description = "Get all sensors with sensorId, status and water dispense per second")
	@GetMapping
	public ResponseEntity<List<SensorDTO>> getAllSensors() {
		return ResponseEntity.ok(SensorService.findAll());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Get API for getting Sensor details
	// Returns sensorId, status, and water dispense per second
	@Operation(summary = "Get sensor details", description = "Get sensor details whcih are sensorId, status, and water dispense per second")
	@GetMapping("/{id}")
	public ResponseEntity<SensorDTO> getSensor(
			@Parameter(description = "The id of the sensor") @PathVariable(name = "id") final UUID id) {
		return ResponseEntity.ok(SensorService.get(id));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Put API for updating Sensor
	// Request Body includes one required field water dispense per second
	// Returns updated sensor
	@Operation(summary = "Update sensor", description = "Update sensor with required field water dispense per second")
	@PutMapping("/{id}")
	public ResponseEntity<SensorDTO> updateSensor(
			@Parameter(description = "The id of the sensor to update") @PathVariable(name = "id") final UUID id,
			@Parameter(description = "The fields to update for the sensor") @RequestBody @Valid final UpdateSensorDTO sensorDTO) {
		return ResponseEntity.ok(SensorService.update(id, sensorDTO));
	}
}
