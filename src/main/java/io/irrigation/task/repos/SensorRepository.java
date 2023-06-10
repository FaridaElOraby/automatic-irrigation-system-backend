package io.irrigation.task.repos;

import io.irrigation.task.model.entity.Sensor;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, UUID> {

}
