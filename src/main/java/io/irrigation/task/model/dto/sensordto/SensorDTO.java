package io.irrigation.task.model.dto.sensordto;

import java.util.UUID;

import io.irrigation.task.model.enums.SensorStatus;

import javax.persistence.GeneratedValue;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorDTO {

	@GeneratedValue(generator = "UUID")
	@Getter
	private UUID id;

	@NotNull
	@Getter
	private SensorStatus status;

	@NotNull
	@Getter
	private int waterDispensePerSecond;

}
