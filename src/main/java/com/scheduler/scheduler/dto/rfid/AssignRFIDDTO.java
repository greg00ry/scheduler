package com.scheduler.scheduler.dto.rfid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AssignRFIDDTO {
    @NotNull
    private Long id;
    @NotBlank
    @Size(min = 10, max = 10)
    @Pattern(regexp = "\\d{10}")
    private String rfid;
}
