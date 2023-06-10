package io.irrigation.task.model.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;
import lombok.Getter;
import lombok.Setter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class CropRecommendation {

	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(generator = "UUID")
	private UUID id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private CropType cropType;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private LandType landType;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private OffsetDateTime dateCreated;

	@LastModifiedDate
	@Column(nullable = false)
	private OffsetDateTime lastUpdated;

	@Getter
	private int irrigationRateInSeconds;

	@Getter
	private int waterAmount;

}
