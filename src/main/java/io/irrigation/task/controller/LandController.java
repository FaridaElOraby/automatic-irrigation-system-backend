package io.irrigation.task.controller;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.irrigation.task.model.dto.landdto.CreateLandDTO;
import io.irrigation.task.model.dto.landdto.LandConfigurationDTO;
import io.irrigation.task.model.dto.landdto.LandDTO;
import io.irrigation.task.model.dto.landdto.UpdateLandDTO;
import io.irrigation.task.model.dto.SlotDTO;

import io.irrigation.task.service.LandService;
import io.irrigation.task.service.SlotService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/lands", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class LandController {

	// Inject the required dependencies via constructor injection
	private final LandService landService;
	private final SlotService slotService;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Get API for listing Lands returns landId, name crop type and land type
	@Operation(summary = "Get all lands", description = "Get all land details which are landId, name, crop type, land type, assigned sensor Id and the configuration details which are irrigationRateInSeconds, waterAmount, maxRetries.")
	@GetMapping

	public ResponseEntity<List<LandDTO>> getAllLands() {
		return ResponseEntity.ok(landService.findAll());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Get API for getting land details and land configuration details
	// Land Details include landId, name, crop type, land type, assigned sensor Id
	// Land COnfiguration Details include irrigationRateInSeconds, waterAmount,
	// maxRetries
	@Operation(summary = "Get land details", description = "Get land details which are landId, name, crop type, land type, assigned sensor Id and the configuration details which are irrigationRateInSeconds, waterAmount, maxRetries.")
	@GetMapping("/{id}")

	public ResponseEntity<LandDTO> getLand(
			@Parameter(description = "The id of the land") @PathVariable(name = "id") final UUID id) {
		return ResponseEntity.ok(landService.get(id));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Post API for creating land and automatically created a sensor assigned to
	// this Land
	// Request Body includes name, crop type(enum), land type(enum), associated
	// sensor's Water Dispense Per Second
	// API response is the generated landId (UUID)
	@Operation(summary = "Create land", description = "Create land and automatically create a sensor assigned to this land. The sesnor's property \"water dispense per second\" is also specified in the request body. This land is still not configured so no scheduled watering slots are assigned to it.")
	@PostMapping
	@ApiResponse(responseCode = "201", description = "Land created successfully")

	public ResponseEntity<UUID> createLand(
			@Parameter(description = "The details of the land to create") @RequestBody @Valid final CreateLandDTO landDTO) {
		final UUID createdId = landService.create(landDTO);
		return new ResponseEntity<>(createdId, HttpStatus.CREATED);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Post API for Configuring Land
	// Request Body is the Land Configuration Details which includes
	// irrigationRateInSeconds, waterAmount, maxRetries
	@Operation(summary = "Configure land with irrigationRateInSeconds, waterAmount and maxRetries. Each land can only have one configuration. This API adds a new configuration to an unconfigured land or reconfigures and overrides the old configuration if exists.")
	@PostMapping("/{id}/configure")
	@ApiResponse(responseCode = "204", description = "Land configured successfully")

	public ResponseEntity<LandDTO> configureLand(
			@Parameter(description = "The id of the land to configure") @PathVariable(name = "id") final UUID landId,
			@Parameter(description = "The configuration details for the land") @RequestBody @Valid final LandConfigurationDTO landConfigDTO) {
		return ResponseEntity.ok(landService.configureLand(landId, landConfigDTO));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Get API for listing Scheduled Land Slots
	// Returns previous land slots with status DONE or FAILED
	// Returns one slot with status pending or running
	// Each Slot returned has a slotId, retries, maxRetries and Sensor Details
	@Operation(summary = "Get land slots", description = "Get land details which are landId, name, crop type, land type, assigned sensor Id and the configuration details which are irrigationRateInSeconds, waterAmount, maxRetries.et scheduled land watering slots with information on slotId, slot status, retries, maxRetries.")
	@GetMapping("/{id}/slots")

	public ResponseEntity<List<SlotDTO>> getLandSlots(
			@Parameter(description = "The id of the land") @PathVariable(name = "id") final UUID id) {
		return ResponseEntity.ok(slotService.findByLandId(id));
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Put API for updating land
	// Request Body includes optionalFields: name, crop type and land type
	@Operation(summary = "Update land", description = "Update land with optionalFields: name, crop type and land type")
	@PutMapping("/{id}")

	public ResponseEntity<Void> updateLand(
			@Parameter(description = "The id of the land to update") @PathVariable(name = "id") final UUID id,
			@Parameter(description = "The fields to update for the land") @RequestBody @Valid final UpdateLandDTO landDTO) {
		landService.update(id, landDTO);
		return ResponseEntity.ok().build();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Delete API for deleting land and automatically deleting a sensor assigned to
	// this land and any pending slots
	@Operation(summary = "Delete land", description = "Delete land and automatically delete a sensor assigned to this land and any of the land's watering slots")
	@DeleteMapping("/{id}")
	@ApiResponse(responseCode = "204", description = "Land deleted successfully")

	public ResponseEntity<Void> deleteLand(
			@Parameter(description = "The id of the land to delete") @PathVariable(name = "id") final UUID id) {
		landService.delete(id);
		return ResponseEntity.noContent().build();
	}

}
