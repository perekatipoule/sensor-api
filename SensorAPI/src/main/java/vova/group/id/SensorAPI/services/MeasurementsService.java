package vova.group.id.SensorAPI.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.repositories.MeasurementsRepository;
import vova.group.id.SensorAPI.repositories.SensorsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MeasurementsService {

    private final MeasurementsRepository measurementsRepository;
    private final SensorsRepository sensorsRepository;
    private final SensorsService sensorsService;

    @Autowired
    public MeasurementsService(MeasurementsRepository measurementsRepository, SensorsRepository sensorsRepository,
                               SensorsService sensorsService) {
        this.measurementsRepository = measurementsRepository;
        this.sensorsRepository = sensorsRepository;
        this.sensorsService = sensorsService;
    }

    public List<Measurement> findAll() {
        return measurementsRepository.findAll();
    }


    @Transactional
    public void save(Measurement measurement) {

        String sensorName = measurement.getSensor().getName();
        Sensor sensorFromDB = sensorsService.findByName(sensorName);
        measurement.setSensor(sensorFromDB);

        enrichMeasurement(measurement);

        measurementsRepository.save(measurement);
    }

    private void enrichMeasurement(Measurement measurement) {
        measurement.setCreatedAt(LocalDateTime.now());
    }

}
