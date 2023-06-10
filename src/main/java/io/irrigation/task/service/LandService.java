package io.irrigation.task.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import io.irrigation.task.exception.NotFoundException;

import io.irrigation.task.model.dto.SlotDTO;
import io.irrigation.task.model.dto.landdto.CreateLandDTO;
import io.irrigation.task.model.dto.landdto.LandConfigurationDTO;
import io.irrigation.task.model.dto.landdto.LandDTO;
import io.irrigation.task.model.dto.landdto.UpdateLandDTO;
import io.irrigation.task.model.dto.sensordto.SensorDTO;
import io.irrigation.task.model.entity.CropRecommendation;
import io.irrigation.task.model.entity.Land;
import io.irrigation.task.model.entity.Sensor;

import io.irrigation.task.model.enums.SensorStatus;
import io.irrigation.task.model.enums.SlotStatus;

import io.irrigation.task.repos.LandRepository;
import io.irrigation.task.repos.CropRecommendationRepository;
import io.irrigation.task.repos.SlotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class LandService {

	// Inject the required dependencies via constructor injection
	private final LandRepository landRepository;
	private final CropRecommendationRepository cropRecommendationRepository;
	private final SlotRepository slotRepository;

	private final SensorService SensorService;
	private final SlotService slotService;

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Retrieve all Lands sorted by creation date
	public List<LandDTO> findAll() {
		final List<Land> lands = landRepository.findAll(Sort.by("dateCreated"));

		// Map each Land entity to its corresponding DTO and collect them into a list
		return lands.stream().map(land -> mapToDTO(land, new LandDTO())).collect(Collectors.toList());
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Retrieve a Land by its ID
	public LandDTO get(final UUID id) {
		return landRepository.findById(id).map(land -> mapToDTO(land, new LandDTO()))
				.orElseThrow(NotFoundException::new);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Create a new Land and its associated Sensor
	public UUID create(final CreateLandDTO landDTO) {

		// Create a new Sensor with default values
		SensorDTO sensorDto = new SensorDTO();

		sensorDto.setStatus(SensorStatus.STATUS_AVAILABLE);
		sensorDto.setWaterDispensePerSecond(landDTO.getSensorWaterDispensePerSecond());

		Sensor sensor = SensorService.create(sensorDto);

		final Land land = new Land();

		mapToEntity(landDTO, land);

		// Associate the new Land with the new Sensor and save it to the database
		land.setSensor(sensor);

		// Save Land in DB and return its generated Id (UUID)
		return landRepository.save(land).getId();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Update a Land's properties using update API
	public void update(final UUID id, final UpdateLandDTO landDTO) {
		final Land land = landRepository.findById(id).orElseThrow(NotFoundException::new);
		mapToEntity(landDTO, land);
		landRepository.save(land);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Update a Land's properties and configurations (helper function used in code)
	public void update(final UUID id, final LandDTO landDTO) {
		final Land land = landRepository.findById(id).orElseThrow(NotFoundException::new);
		mapToEntity(landDTO, land);
		landRepository.save(land);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Delete a Land and its associated Slots and Sensor
	@Transactional
	public void delete(final UUID landId) {
		// Validate that land with landId exists
		final LandDTO landDto = this.get(landId);

		// Delete any associated Slots with status "STATUS_PENDING"
		slotRepository.deleteAllByLandId(landId);

		// Delete the Land from the database
		landRepository.deleteById(landId);

		// Delete the associated Sensors
		SensorService.delete(landDto.getSensor());

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Configure a Land's properties and create a new Slot with status
	// "STATUS_PENDING"
	@Transactional
	public LandDTO configureLand(final UUID landId, final LandConfigurationDTO landConfigDTO) {
		// get Land with this landId from DB
		final LandDTO landDto = this.get(landId);

		CropRecommendation cropRecommendation = cropRecommendationRepository
				.findOneByCropTypeAndLandType(landDto.getCropType(), landDto.getLandType());

		// Update the Land's configuration properties with the values from the DTO or
		// from the crop recommendations

		if (landConfigDTO.getIrrigationRateInSeconds() > 0) {
			landDto.setIrrigationRateInSeconds(landConfigDTO.getIrrigationRateInSeconds());
		} else {
			landDto.setIrrigationRateInSeconds(cropRecommendation.getIrrigationRateInSeconds());
		}

		if (landConfigDTO.getWaterAmount() > 0) {
			landDto.setWaterAmount(landConfigDTO.getWaterAmount());
		} else {
			landDto.setWaterAmount(cropRecommendation.getWaterAmount());
		}

		// Update the Land's max retries with the values from the DTO or the default
		// value 3
		if (landConfigDTO.getIrrigationRateInSeconds() > 0) {
			landDto.setMaxRetries(landConfigDTO.getMaxRetries());
		} else {
			landDto.setMaxRetries(3);
		}

		// Update the Land entity in the database with the new configuration properties
		this.update(landId, landDto);

		// Delete any Slots with status "STATUS_PENDING" associated with the Land form
		// previous configuration if exists
		slotRepository.deleteAllByLandIdAndStatus(landId, SlotStatus.STATUS_PENDING);

		// Create First Watering Slot for new configuration with pending status
		Date date = new Date();
		SlotDTO slotDTO = new SlotDTO();

		slotDTO.setIrrigationStartDate(date);
		slotDTO.setStatus(SlotStatus.STATUS_PENDING);
		slotDTO.setRetries(0);
		slotDTO.setMaxRetries(landDto.getMaxRetries());
		slotDTO.setLand(landId);
		slotDTO.setSensor(landDto.getSensor());

		slotService.create(slotDTO);

		// Return the updated Land DTO
		return this.get(landId);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a Land entity to its corresponding DTO
	private LandDTO mapToDTO(final Land land, final LandDTO landDTO) {
		landDTO.setId(land.getId());
		landDTO.setName(land.getName());
		landDTO.setCropType(land.getCropType());
		landDTO.setLandType(land.getLandType());
		landDTO.setIrrigationRateInSeconds(land.getIrrigationRateInSeconds());
		landDTO.setMaxRetries(land.getMaxRetries());
		landDTO.setWaterAmount(land.getWaterAmount());
		Sensor sensor = land.getSensor();
		landDTO.setSensor(sensor.getId());
		return landDTO;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a CreateLandDTO to a Land entity
	private Land mapToEntity(final CreateLandDTO landDTO, final Land land) {
		land.setName(landDTO.getName());
		land.setCropType(landDTO.getCropType());
		land.setLandType(landDTO.getLandType());
		return land;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of an UpdateLandDTO to a Land entity
	private Land mapToEntity(final UpdateLandDTO landDTO, final Land land) {
		if (landDTO.getName() != null) {
			land.setName(landDTO.getName());
		}

		if (landDTO.getCropType() != null) {
			land.setCropType(landDTO.getCropType());
		}

		if (landDTO.getLandType() != null) {
			land.setLandType(landDTO.getLandType());
		}
		return land;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a LandDTO to a Land entity
	private Land mapToEntity(final LandDTO landDTO, final Land land) {
		land.setName(landDTO.getName());
		land.setCropType(landDTO.getCropType());
		land.setLandType(landDTO.getLandType());
		land.setIrrigationRateInSeconds(landDTO.getIrrigationRateInSeconds());
		land.setMaxRetries(landDTO.getMaxRetries());
		land.setWaterAmount(landDTO.getWaterAmount());
		return land;
	}

}
