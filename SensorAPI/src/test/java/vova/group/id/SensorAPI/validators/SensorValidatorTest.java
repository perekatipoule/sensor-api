package vova.group.id.SensorAPI.validators;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.utils.H2DatabaseInitTest;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class SensorValidatorTest extends H2DatabaseInitTest {
    @Autowired
    private SensorValidator validator;

    @Test
    public void supportsTest() {
        assertTrue(validator.supports(Sensor.class));
        assertFalse(validator.supports(Object.class));
    }

    @Test
    public void testValidateWithValidSensor() {
        // given
        Sensor sensor = new Sensor();
        sensor.setName("test");
        Errors errors = new BeanPropertyBindingResult(sensor, "sensor");
        // when
        validator.validate(sensor, errors);
        // then
        assertFalse(errors.hasErrors());
    }

    @Test
    public void testValidateWithSensorNameExist() {
        // given
        Sensor sensor = new Sensor();
        sensor.setName("FirstSensor");
        Errors errors = new BeanPropertyBindingResult(sensor, "sensor");
        // when
        validator.validate(sensor, errors);
        // then
        assertTrue(errors.hasErrors());
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.hasFieldErrors("name"));
        assertEquals("Sensor with this name already exists", Objects.requireNonNull(errors.getFieldError("name")).getDefaultMessage());
    }
}
