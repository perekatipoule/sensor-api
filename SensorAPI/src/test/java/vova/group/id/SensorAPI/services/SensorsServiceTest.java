package vova.group.id.SensorAPI.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.repositories.SensorsRepository;

import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class SensorsServiceTest {
    @MockBean
    private SensorsRepository sensorsRepository;

    @Autowired
    private SensorsService sensorsService;

    private Sensor testSensor;

    @BeforeEach
    public void setUp() {
        testSensor = new Sensor();
        testSensor.setName("testSensor");
    }

    @Test
    public void testSave() {
        // when
        sensorsService.save(testSensor);
        // then
        Date now = new Date();
        long createdAtMillis = testSensor.getCreatedAt()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        long diff = Math.abs(now.getTime() - createdAtMillis);
        assertTrue(diff < 1000, "created at should be set to current time");
        verify(sensorsRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    public void testFindByName() {
        // given
        when(sensorsRepository.findByName(anyString())).thenReturn(Optional.of(testSensor));
        // when
        Sensor receivedSensor = sensorsService.findByName(anyString());
        // then
        assertEquals(testSensor, receivedSensor);
        verify(sensorsRepository).findByName(anyString());
    }

    @Test
    public void testExistByName() {
        // given
        when(sensorsRepository.existsByName(anyString())).thenReturn(true);
        // when
        boolean receivedExist = sensorsService.existByName(anyString());
        // then
        assertTrue(receivedExist);
        verify(sensorsRepository).existsByName(anyString());
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(sensorsRepository);
    }
}
