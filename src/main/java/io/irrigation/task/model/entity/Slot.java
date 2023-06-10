package io.irrigation.task.model.entity;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.irrigation.task.model.enums.SensorStatus;
import io.irrigation.task.model.enums.SlotStatus;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Slot {

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(generator = "UUID")
	@Getter
	private UUID id;

	@Getter
	@Column(nullable = false)
	private Date irrigationStartDate;

	@Getter
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private SlotStatus status;

	@Getter
	@Column
	private int retries;

	@Getter
	@Column
	private int maxRetries;

	@Getter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "land_id", nullable = false)
	private Land land;

	@Getter
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sensor_id", nullable = false)
	private Sensor sensor;

	@Getter
	@CreatedDate
	@Column(nullable = false, updatable = false)
	private OffsetDateTime dateCreated;

	@Getter
	@LastModifiedDate
	@Column(nullable = false)
	private OffsetDateTime lastUpdated;

}
