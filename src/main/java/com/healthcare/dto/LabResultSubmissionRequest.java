package com.healthcare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabResultSubmissionRequest {
	// Either patientId or patientName must be provided
	private String patientId;

	private String patientName;

	@NotBlank(message = "Doctor ID is required")
	private String doctorId;

	@NotBlank(message = "Test result is required")
	private String result;

	// Optional date string in format dd/MM/yyyy or ISO; if absent, current time will be used
	private String date;
}


