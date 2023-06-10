package io.irrigation.task.model.dto;

import java.util.Date;
import java.util.UUID;

import io.irrigation.task.model.enums.SlotStatus;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotDTO {

	@Getter
	private UUID id;

	@NotNull
	@Getter
	private Date irrigationStartDate;

	@NotNull
	@Getter
	private SlotStatus status;

	@Getter
	private int retries;

	@Getter
	private int maxRetries;

	@NotNull
	@Getter
	private UUID land;

	@NotNull
	@Getter
	private UUID sensor;

}
