package io.irrigation.task.service;

import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Service;

import io.irrigation.task.model.entity.CropRecommendation;

import io.irrigation.task.model.dto.SlotDTO;
import io.irrigation.task.model.dto.landdto.CreateLandDTO;
import io.irrigation.task.model.dto.landdto.LandConfigurationDTO;
import io.irrigation.task.model.dto.landdto.LandDTO;
import io.irrigation.task.repos.CropRecommendationRepository;

import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;

@EnableAsync
@Service
public class SimulationRunnerService {

	// Inject the required dependencies via constructor injection
	@Autowired
	private SlotService slotService;

	@Autowired
	private LandService landService;

	@Autowired
	private CropRecommendationRepository cropRecommendationRepository;

	// Run Slots Scheduled function that runs every 20 seconds and simulated
	// watering the land
	@Async
	@Scheduled(fixedRate = 20 * 1000)
	public void runSlots() throws InterruptedException {
		System.out.println("slot runner started");

		// Retrieved all pending slots that have start time less than or equal current
		// time
		List<SlotDTO> slots = slotService.findAllPendingSlots();

		for (SlotDTO slot : slots) {
			// executes the slot (simulates watering)
			slotService.execute(slot);

			// schedules next slot after current slot is finished either with status done or
			// failed
			slotService.createNextSlot(slot);
		}
		System.out.println("slot runner stopped");
	}

	@PostConstruct
	public void seeder() {
		System.out.println("SEED DATA");

		// Seed Crop Recommendation Data for all enum values of crop types and land
		// types and set irrigation rate and water amount to a random number
		seedCropRecommendations();

		// Add a land and configure it based on crop recommendations
		addOneLandAndConfigureIt();
	}

	public void seedCropRecommendations() {
		// Get All Crop Recommendations from database
		List<CropRecommendation> allCropRecommendations = cropRecommendationRepository.findAll();

		// If Empty then seed data
		if (allCropRecommendations.isEmpty()) {
			// Loop on all crop type emums
			for (CropType cropType : CropType.values()) {
				// Loop on all land type enums for each crop type
				for (LandType landType : LandType.values()) {
					CropRecommendation cropRecommendation = new CropRecommendation();
					// Set Crop Type
					cropRecommendation.setCropType(cropType);

					// Set land type
					cropRecommendation.setLandType(landType);

					cropRecommendation.setIrrigationRateInSeconds(getRandomNumber(40, 80));
					cropRecommendation.setWaterAmount(getRandomNumber(10, 50));

					// Save in Database
					System.out.println(cropRecommendation);
					cropRecommendationRepository.save(cropRecommendation);
				}

			}
		}
	}

	public void addOneLandAndConfigureIt() {
		// Get All Lands from database
		List<LandDTO> allLands = landService.findAll();

		// If Empty then seed data
		if (allLands.isEmpty()) {

			CreateLandDTO landDTO = new CreateLandDTO();

			// Add Land Properties
			landDTO.setCropType(CropType.CROP_POTATO);
			landDTO.setLandType(LandType.LAND_AGRICULTURAL);
			landDTO.setName("Seeded Land");
			landDTO.setSensorWaterDispensePerSecond(20);

			// Create Land DTO
			UUID landId = landService.create(landDTO);

			// Configure Land without giving it any configuration properties so it is
			// configured based on crop recommendation
			LandConfigurationDTO configLandDTO = new LandConfigurationDTO();
			landService.configureLand(landId, configLandDTO);
		}
	}

	public int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}
}
