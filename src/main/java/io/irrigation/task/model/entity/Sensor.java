package io.irrigation.task.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.irrigation.task.model.enums.SensorStatus;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Sensor {

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(generator = "UUID")
	private UUID id;

	@Getter
	@Column(nullable = false)
	private int waterDispensePerSecond;

	@Getter
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SensorStatus status;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private OffsetDateTime dateCreated;

	@LastModifiedDate
	@Column(nullable = false)
	private OffsetDateTime lastUpdated;

}
