package vova.group.id.SensorAPI.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import vova.group.id.SensorAPI.models.Sensor;

@Getter
@Setter
public class MeasurementDTO {

    @NotNull(message = "Temperature value should not be empty")
    @Min(value = -100, message = "Temperature must be greater than or equal to -100")
    @Max(value = 100, message = "Temperature must be less than or equal to 100")
    private Double temperature;

    @NotNull(message = "Raining value should not be empty")
    private Boolean raining;

    @NotNull(message = "Sensor should not be empty")
    private SensorDTO sensor;
}
