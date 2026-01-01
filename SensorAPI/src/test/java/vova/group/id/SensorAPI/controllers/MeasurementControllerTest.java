package vova.group.id.SensorAPI.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vova.group.id.SensorAPI.dto.MeasurementDTO;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.services.MeasurementsService;
import vova.group.id.SensorAPI.services.SensorsService;
import vova.group.id.SensorAPI.validators.MeasurementValidatorTest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class MeasurementControllerTest {

    @MockBean
    private MeasurementsService measurementsService;

    @MockBean
    private SensorsService sensorService;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeasurementValidatorTest measurementValidator;

    private MockMvc mockMvc;
    private Measurement testMeasurement;
    private MeasurementDTO testMeasurementDTO;
    private final Random random = new Random();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        Sensor sensor = new Sensor();
        sensor.setName("Test sensor");

        testMeasurement = new Measurement();
        testMeasurement.setId(random.nextInt(100));
        testMeasurement.setTemperature(random.nextDouble());
        testMeasurement.setRaining(true);
        testMeasurement.setSensor(sensor);
        testMeasurement.setCreatedAt(LocalDateTime.now());

        testMeasurementDTO = modelMapper.map(testMeasurement, MeasurementDTO.class);
    }

    @Test
    public void testGetMeasurements() throws Exception {
        when(measurementsService.findAll()).thenReturn(Collections.singletonList(testMeasurement));

        mockMvc.perform(MockMvcRequestBuilders.get("/measurements"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").doesNotExist(),
                        jsonPath("$[0].temperature", is(testMeasurementDTO.getTemperature())),
                        jsonPath("$[0].raining", is(testMeasurementDTO.getRaining())),
                        jsonPath("$[0].sensor.name", is(testMeasurementDTO.getSensor().getName())),
                        jsonPath("$[0].created_at").doesNotExist()
                );

        verify(measurementsService, times(1)).findAll();
    }

    @Test
    public void testGetRainyDaysWithOneRainyDay() throws Exception {
        when(measurementsService.findAll()).thenReturn(Collections.singletonList(testMeasurement));
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements/rainyDaysCount"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", is(1))
                );

        verify(measurementsService, times(1)).findAll();
    }

    @Test
    public void testGetRainyDaysWithNoRainyDays() throws Exception {
        testMeasurement.setRaining(false);
        when(measurementsService.findAll()).thenReturn(Collections.singletonList(testMeasurement));
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements/rainyDaysCount"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", is(0))
                );

        verify(measurementsService, times(1)).findAll();
    }

    @Test
    public void testAddWithEmptyMeasurement() throws Exception {
        //empty measurement
        MeasurementDTO measurementDTO = new MeasurementDTO();
        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                containsString("Temperature value should not be empty")),
                        jsonPath("$.message").value(
                                containsString("Raining value should not be empty")),
                        jsonPath("$.message").value(
                                containsString("Sensor should not be empty")),
                        jsonPath("$.message").value(
                                containsString("Sensor does not exist"))
                );
    }

    @Test
    public void testAddWithTemperatureLessThan100() throws Exception {
        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setTemperature(-110.0);
        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                containsString("Temperature must be greater than or equal to -100"))
                );
    }

    @Test
    public void testAddWithTemperatureGreaterThan100() throws Exception {
        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setTemperature(110.0);
        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                containsString("Temperature must be less than or equal to 100"))
                );
    }

    @Test
    public void testAddWithValidMeasurement() throws Exception {
        when(sensorService.existByName(anyString())).thenReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(measurementsService, times(1)).save(any(Measurement.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(measurementsService);
    }
}
