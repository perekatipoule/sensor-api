package vova.group.id.SensorAPI.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.services.SensorsService;

@Component
public class SensorValidator implements Validator {

    private final SensorsService sensorsService;

    @Autowired
    public SensorValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Sensor.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Sensor sensor = (Sensor) o;
        // Check for sensor duplicates
        if (sensorsService.existByName(sensor.getName()))
            errors.rejectValue("name", "Sensor with this name already exists");
    }
}
