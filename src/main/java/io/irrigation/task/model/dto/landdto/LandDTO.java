package io.irrigation.task.model.dto.landdto;

import java.util.UUID;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandDTO {

	private UUID id;

	@NotNull
	@Size(max = 255)
	private String name;

	@NotNull
	private CropType cropType;

	@NotNull
	private LandType landType;

	@Getter
	private UUID sensor;

	@Getter
	private int irrigationRateInSeconds;

	@Getter
	private int waterAmount;

	@Getter
	private int maxRetries;

}
