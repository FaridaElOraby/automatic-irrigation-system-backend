package io.irrigation.task.model.dto.sensordto;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSensorDTO {
	@NotNull
	@Getter
	private int waterDispensePerSecond;

}
