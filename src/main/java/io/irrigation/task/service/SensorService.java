package io.irrigation.task.service;

import io.irrigation.task.model.dto.sensordto.SensorDTO;
import io.irrigation.task.model.dto.sensordto.UpdateSensorDTO;
import io.irrigation.task.model.entity.Sensor;
import io.irrigation.task.repos.SensorRepository;
import io.irrigation.task.exception.NotFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class SensorService {

	private final SensorRepository SensorRepository;

	// Retrieve all Sensors sorted by creation date
	public List<SensorDTO> findAll() {
		final List<Sensor> Sensors = SensorRepository.findAll(Sort.by("dateCreated"));

		// Map each Sensor entity to its corresponding DTO and collect them into a list
		return Sensors.stream().map(Sensor -> mapToDTO(Sensor, new SensorDTO())).collect(Collectors.toList());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Retrieve a Sensor by its ID
	public SensorDTO get(final UUID id) {
		return SensorRepository.findById(id).map(Sensor -> mapToDTO(Sensor, new SensorDTO()))
				.orElseThrow(NotFoundException::new);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Create a new Sensor (used when creating land)
	public Sensor create(final SensorDTO SensorDTO) {
		final Sensor Sensor = new Sensor();
		mapToEntity(SensorDTO, Sensor);
		return SensorRepository.save(Sensor);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Update a Sensor's water dispense property using update API
	public SensorDTO update(final UUID id, final @Valid UpdateSensorDTO sensorDTO) {
		final Sensor Sensor = SensorRepository.findById(id).orElseThrow(NotFoundException::new);
		mapToEntity(sensorDTO, Sensor);
		SensorRepository.save(Sensor);
		return this.get(id);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Update a Sensor's properties (used inside code)
	public SensorDTO update(final UUID id, final @Valid SensorDTO sensorDTO) {
		final Sensor Sensor = SensorRepository.findById(id).orElseThrow(NotFoundException::new);
		mapToEntity(sensorDTO, Sensor);
		SensorRepository.save(Sensor);
		return this.get(id);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Delete a sensor (used when deleting land)
	public void delete(final UUID id) {
		SensorRepository.deleteById(id);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a sensor entity to its corresponding DTO
	private SensorDTO mapToDTO(final Sensor Sensor, final SensorDTO SensorDTO) {
		SensorDTO.setId(Sensor.getId());
		SensorDTO.setWaterDispensePerSecond(Sensor.getWaterDispensePerSecond());
		SensorDTO.setStatus(Sensor.getStatus());
		return SensorDTO;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a SensorDTO to a Sensor entity
	private Sensor mapToEntity(final SensorDTO SensorDTO, final Sensor Sensor) {
		Sensor.setId(SensorDTO.getId());
		Sensor.setWaterDispensePerSecond(SensorDTO.getWaterDispensePerSecond());
		Sensor.setStatus(SensorDTO.getStatus());
		return Sensor;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a UpdateSensorDTO to a Sensor entity
	private Sensor mapToEntity(final UpdateSensorDTO SensorDTO, final Sensor Sensor) {
		Sensor.setWaterDispensePerSecond(SensorDTO.getWaterDispensePerSecond());
		return Sensor;
	}

}
