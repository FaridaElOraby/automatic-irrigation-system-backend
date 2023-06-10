package io.irrigation.task.model.dto.landdto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLandDTO {

	@NotNull
	@Size(max = 255)
	private String name;

	@NotNull
	private CropType cropType;

	@NotNull
	private LandType landType;

	@NotNull
	@Getter
	private int sensorWaterDispensePerSecond;

}
