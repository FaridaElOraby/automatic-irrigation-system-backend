package io.irrigation.task.repos;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.irrigation.task.model.entity.Slot;
import io.irrigation.task.model.enums.SlotStatus;

@Repository
public interface SlotRepository extends JpaRepository<Slot, UUID> {

	// List all slots with status and date query
	List<Slot> findAllByStatusAndIrrigationStartDateLessThanEqual(SlotStatus status, java.util.Date date);

	// Delete all slots with status and date query
	void deleteAllByStatusAndIrrigationStartDateLessThanEqual(SlotStatus status, java.util.Date date);

	// Delete all slots with status and landID query
	void deleteAllByLandIdAndStatus(UUID uuid, SlotStatus status);

	// Delete all slots with landID
	void deleteAllByLandId(UUID uuid);

	// List all slots with specific landId
	List<Slot> findByLandId(UUID uuid);

}
