package io.irrigation.task.model.dto.landdto;

import javax.validation.constraints.Size;

import io.irrigation.task.model.enums.CropType;
import io.irrigation.task.model.enums.LandType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLandDTO {

	@Size(max = 255)
	private String name;

	private CropType cropType;

	private LandType landType;

}
