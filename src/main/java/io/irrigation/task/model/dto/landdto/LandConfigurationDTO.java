package io.irrigation.task.model.dto.landdto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandConfigurationDTO {

	@Getter
	private int irrigationRateInSeconds;

	@Getter
	private int waterAmount;

	@Getter
	private int maxRetries;

}
