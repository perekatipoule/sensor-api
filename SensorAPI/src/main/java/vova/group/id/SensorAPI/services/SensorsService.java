package vova.group.id.SensorAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.repositories.SensorsRepository;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class SensorsService {

    private final SensorsRepository sensorsRepository;

    @Autowired
    public SensorsService(SensorsRepository sensorsRepository) {
        this.sensorsRepository = sensorsRepository;
    }

    @Transactional
    public void save(Sensor sensor) {
        enrichSensor(sensor);
        sensorsRepository.save(sensor);
    }

    public Sensor findByName(String sensorName) {
        return sensorsRepository.findByName(sensorName).orElse(null);
    }

    public Boolean existByName(String name) {
        return sensorsRepository.existsByName(name);
    }


    private void enrichSensor(Sensor sensor) {
        sensor.setCreatedAt(LocalDateTime.now());
    }
}
