package io.irrigation.task.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import io.irrigation.task.exception.NotFoundException;
import io.irrigation.task.model.dto.SlotDTO;
import io.irrigation.task.model.dto.sensordto.SensorDTO;
import io.irrigation.task.model.entity.Land;
import io.irrigation.task.model.entity.Sensor;
import io.irrigation.task.model.entity.Slot;
import io.irrigation.task.model.enums.SensorStatus;
import io.irrigation.task.model.enums.SlotStatus;
import io.irrigation.task.repos.LandRepository;
import io.irrigation.task.repos.SensorRepository;
import io.irrigation.task.repos.SlotRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class SlotService {

	// Inject the required dependencies via constructor injection
	private final SlotRepository slotRepository;
	private final LandRepository landRepository;
	private final SensorRepository sensorRepository;

	private final SensorService sensorService;

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// Retrieve a Land by its ID
	public SlotDTO get(final UUID id) {
		return slotRepository.findById(id).map(slot -> mapToDTO(slot, new SlotDTO()))
				.orElseThrow(NotFoundException::new);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Create a new Slot
	public UUID create(final SlotDTO slotDTO) {
		final Slot slot = new Slot();
		mapToEntity(slotDTO, slot);
		return slotRepository.save(slot).getId();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a Slot entity to its corresponding DTO
	private SlotDTO mapToDTO(final Slot slot, final SlotDTO slotDTO) {
		slotDTO.setId(slot.getId());
		slotDTO.setIrrigationStartDate(slot.getIrrigationStartDate());
		slotDTO.setStatus(slot.getStatus());
		slotDTO.setRetries(slot.getRetries());
		slotDTO.setMaxRetries(slot.getMaxRetries());
		slotDTO.setSensor(slot.getSensor().getId());
		slotDTO.setLand(slot.getLand().getId());

		return slotDTO;
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Map the properties of a SlotDTO to a slot entity
	private Slot mapToEntity(final SlotDTO slotDTO, final Slot slot) {
		slot.setId(slotDTO.getId());
		slot.setIrrigationStartDate(slotDTO.getIrrigationStartDate());
		slot.setStatus(slotDTO.getStatus());
		slot.setRetries(slotDTO.getRetries());
		slot.setMaxRetries(slotDTO.getMaxRetries());
		UUID landId = slotDTO.getLand();
		Land land = landRepository.getOne(landId);
		slot.setLand(land);
		UUID sensorId = slotDTO.getSensor();
		Sensor sensor = sensorRepository.getOne(sensorId);
		slot.setSensor(sensor);
		return slot;
	}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Update a Slot's properties
	public void update(final UUID id, final @Valid SlotDTO slotDTO) {
		this.get(id);
		Slot slot = new Slot();
		mapToEntity(slotDTO, slot);
		slotRepository.save(slot);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Retrieve all Pending Slots
	public List<SlotDTO> findAllPendingSlots() {
		final List<Slot> slots = slotRepository
				.findAllByStatusAndIrrigationStartDateLessThanEqual(SlotStatus.STATUS_PENDING, new Date());
		return slots.stream().map(slot -> mapToDTO(slot, new SlotDTO())).collect(Collectors.toList());
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Schedule next slot after current slots finished running with status failed or
	// done
	public void createNextSlot(SlotDTO slot) {

		try {
			// Create new slot
			final Slot nextSlot = new Slot();

			// Find land by landId
			Land land = landRepository.findById(slot.getLand()).orElseThrow(NotFoundException::new);

			// Calculate next irrigation time. It will be after x seconds depending on
			// irrigation rate
			int x = land.getIrrigationRateInSeconds();
			nextSlot.setIrrigationStartDate(new Date(slot.getIrrigationStartDate().getTime() + (x * 1000)));

			// Set Slot properties
			nextSlot.setLand(land);
			Sensor sensor = sensorRepository.findById(slot.getSensor()).orElseThrow(NotFoundException::new);
			nextSlot.setSensor(sensor);
			nextSlot.setStatus(SlotStatus.STATUS_PENDING);
			nextSlot.setMaxRetries(slot.getMaxRetries());
			nextSlot.setRetries(0);

			// Save slot to database
			slotRepository.save(nextSlot);

		} catch (Exception e) {
			// ignore as this means that the land has been deleted
		}

	}

	// This function executes the irrigation process for the land slot
	public void execute(SlotDTO slotDTO) {
		// Set the slot status to running and set the irrigation start date
		slotDTO.setStatus(SlotStatus.STATUS_RUNNING);
		slotDTO.setIrrigationStartDate(new Date());
		this.update(slotDTO.getId(), slotDTO);

		// Run the irrigation process until it's successful or the maximum retries have
		// been reached
		while (slotDTO.getRetries() < slotDTO.getMaxRetries() && slotDTO.getStatus() != SlotStatus.STATUS_DONE) {
			slotDTO.setRetries(slotDTO.getRetries() + 1);
			try {
				// Check if the sensor is available
				SensorDTO sensor = sensorService.get(slotDTO.getSensor());

				if (sensor.getStatus() != SensorStatus.STATUS_AVAILABLE)
					throw new Exception("Sensor Busy");

				// Set the sensor status to watering and wait for the irrigation process to
				// complete
				// Sleep time in seconds is equal to the amount of water needed for irrigation
				// divided by the sensor's water dispense per second

				sensor.setStatus(SensorStatus.STATUS_WATERING);
				sensorService.update(slotDTO.getSensor(), sensor);

				System.out.println("Slot " + slotDTO.getId() + " is running");
				final Land land = landRepository.findById(slotDTO.getLand()).orElseThrow(NotFoundException::new);

				Thread.sleep(1000 * (land.getWaterAmount() / sensor.getWaterDispensePerSecond()));

				sensor.setStatus(SensorStatus.STATUS_AVAILABLE);
				sensorService.update(slotDTO.getSensor(), sensor);

				slotDTO.setStatus(SlotStatus.STATUS_DONE);
				this.update(slotDTO.getId(), slotDTO);

			} catch (Exception e) {
				// If an exception occurs, set the slot status to failed
				System.out.println("ERROR");
				System.out.println(e);
				slotDTO.setStatus(SlotStatus.STATUS_FAILED);
				this.update(slotDTO.getId(), slotDTO);
			}
		}

		if (slotDTO.getStatus() != SlotStatus.STATUS_DONE) {
			System.out.println("ALERT: " + "Slot " + slotDTO.getId() + " has failed");
		}
	}

	// List all slots for a specific land (may return multiple records with status
	// done or failed but only one with status pending)
	public List<SlotDTO> findByLandId(UUID id) {
		final List<Slot> slots = slotRepository.findByLandId(id);
		return slots.stream().map(slot -> mapToDTO(slot, new SlotDTO())).collect(Collectors.toList());
	}

}
