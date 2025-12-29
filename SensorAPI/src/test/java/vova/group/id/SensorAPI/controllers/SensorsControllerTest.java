package vova.group.id.SensorAPI.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.WebApplicationContext;
import vova.group.id.SensorAPI.dto.SensorDTO;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.services.SensorsService;
import vova.group.id.SensorAPI.validators.SensorValidator;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
public class SensorsControllerTest {
    @MockBean
    private SensorsService sensorsService;

    @MockBean
    SensorValidator sensorValidator;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;
    private SensorDTO testSensorDTO;

    @BeforeEach
    public void setUp() {
        testSensorDTO = new SensorDTO();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRegisterWithEmptySensor() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                org.hamcrest.Matchers.containsString("Sensor name should not be empty")
                        ));

        verify(sensorValidator, times(1)).validate(any(Sensor.class), any(BindingResult.class));
    }

    @Test
    public void testRegisterWithNotValidSensor() throws Exception {
        // test with name less than 3 characters
        testSensorDTO.setName("se");
        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                org.hamcrest.Matchers.containsString("Sensor name should be between 3 and 30 characters")
                        )
                );

        // test with name more than 30 characters
        testSensorDTO.setName("sensor".repeat(6));
        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message").exists(),
                        jsonPath("$.message").value(
                                org.hamcrest.Matchers.containsString("Sensor name should be between 3 and 30 characters")
                        )
                );

        verify(sensorValidator, times(2)).validate(any(Sensor.class), any(BindingResult.class));
    }

    @Test
    public void testRegisterWithValidSensor() throws Exception {
        testSensorDTO.setName("sensor");
        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(sensorValidator, times(1)).validate(any(Sensor.class), any(BindingResult.class));
        verify(sensorsService, times(1)).save(any(Sensor.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(sensorsService);
    }
}
