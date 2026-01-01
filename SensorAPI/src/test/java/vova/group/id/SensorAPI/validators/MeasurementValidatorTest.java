package vova.group.id.SensorAPI.validators;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.utils.H2DatabaseInitTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MeasurementValidatorTest extends H2DatabaseInitTest {
    @Autowired
    private MeasurementValidator validator;

    @Test
    public void supportsTest() {
        assertTrue(validator.supports(Measurement.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void testValidateWithValidMeasurement() {
        // given
        Measurement measurement = new Measurement();
        Sensor sensor = new Sensor();
        sensor.setName("SecondSensor");
        measurement.setSensor(sensor);
        Errors errors = new BeanPropertyBindingResult(measurement, "measurement");
        // when
        validator.validate(measurement, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidateWithNullSensor() {
        // given
        Measurement measurement = new Measurement();
        Errors errors = new BeanPropertyBindingResult(measurement, "measurement");
        // when
        validator.validate(measurement, errors);
        // then
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.hasFieldErrors("sensor"));
        assertEquals("Sensor does not exist", Objects.requireNonNull(errors.getFieldError("sensor")).getDefaultMessage());
    }

    @Test
    public void testValidateWithNotExistingSensor() {
        // given
        Measurement measurement = new Measurement();
        Sensor sensor = new Sensor();
        sensor.setName("notExistingSensor");
        Errors errors = new BeanPropertyBindingResult(measurement, "measurement");
        // when
        validator.validate(measurement, errors);
        // then
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.hasFieldErrors("sensor"));
        assertEquals("Sensor does not exist", Objects.requireNonNull(errors.getFieldError("sensor")).getDefaultMessage());
    }
}
