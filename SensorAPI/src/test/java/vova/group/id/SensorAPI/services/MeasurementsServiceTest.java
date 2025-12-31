package vova.group.id.SensorAPI.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.repositories.MeasurementsRepository;

import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class MeasurementsServiceTest {
    @MockBean
    private MeasurementsRepository measurementsRepository;

    @Autowired
    private MeasurementsService measurementsService;

    @MockBean
    private SensorsService sensorsService;

    @Test
    public void testFindAll() {
        // given
        List<Measurement> testMeasurements = Collections.singletonList(new Measurement());
        when(measurementsRepository.findAll()).thenReturn(testMeasurements);
        // when
        List<Measurement> receivedMeasurements = measurementsService.findAll();
        // then
        assertEquals(testMeasurements, receivedMeasurements);
        verify(measurementsRepository).findAll();
    }

    @Test
    public void testSave() {
        // given
        Measurement testMeasurement = new Measurement();
        Sensor measurementSensor = new Sensor();
        measurementSensor.setName("Test Sensor");
        testMeasurement.setSensor(measurementSensor);

        Sensor sensorFromDB = new Sensor();
        sensorFromDB.setName("Sensor From DB");

        when(sensorsService.findByName(anyString())).thenReturn(sensorFromDB);
        // when
        measurementsService.save(testMeasurement);
        // then
        assertEquals("Sensor From DB", testMeasurement.getSensor().getName());

        Date now = new Date();
        long createdAtMillis = testMeasurement.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long diff = Math.abs(now.getTime() - createdAtMillis);
        assertTrue(diff < 1000, "created at should be set to current time");

        verify(measurementsRepository).save(testMeasurement);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(measurementsRepository);
    }
}
