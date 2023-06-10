package io.irrigation.task.repos;

import io.irrigation.task.model.entity.CropRecommendation;
import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CropRecommendationRepository extends JpaRepository<CropRecommendation, UUID> {
	// List all slots with status and date query
	CropRecommendation findOneByCropTypeAndLandType(CropType cropType, LandType landType);

}
