package io.irrigation.task.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldError {

	private String field;
	private String errorCode;

}
