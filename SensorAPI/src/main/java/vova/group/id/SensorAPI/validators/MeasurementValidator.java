package vova.group.id.SensorAPI.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.services.SensorsService;

@Component
public class MeasurementValidator implements Validator {
    private final SensorsService sensorsService;

    @Autowired
    public MeasurementValidator(SensorsService sensorsService) {
        this.sensorsService = sensorsService;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Measurement.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Measurement measurement = (Measurement) o;
        // Check for sensor availability
        if (measurement.getSensor() == null || !sensorsService.existByName(measurement.getSensor().getName()))
            errors.rejectValue("sensor", "Sensor does not exist");
    }
}
