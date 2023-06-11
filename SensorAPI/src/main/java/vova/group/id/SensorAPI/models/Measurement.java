package vova.group.id.SensorAPI.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "Measurement")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Measurement {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "temperature")
    @NotNull(message = "Temperature value should not be empty")
    @Min(value = -100, message = "Temperature must be greater than or equal to -100")
    @Max(value = 100, message = "Temperature must be less than or equal to 100")
    private Double temperature;

    @Column(name = "raining")
    @NotNull(message = "Raining value should not be empty")
    private Boolean raining;

    @Column(name = "measured_at")
    private LocalDateTime createdAt;

    @NotNull(message = "Sensor should not be empty")
    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "id")
    private Sensor sensor;


}
