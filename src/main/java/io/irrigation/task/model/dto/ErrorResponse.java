package io.irrigation.task.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {

	@Getter
	@Setter
	private Integer httpStatus;

	@Getter
	@Setter
	private String exception;

	@Getter
	@Setter
	private String message;

	@Getter
	@Setter
	private List<FieldError> fieldErrors;

}
