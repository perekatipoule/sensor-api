package vova.group.id.SensorAPI.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vova.group.id.SensorAPI.dto.SensorDTO;
import vova.group.id.SensorAPI.models.Sensor;
import vova.group.id.SensorAPI.repositories.SensorsRepository;
import vova.group.id.SensorAPI.utils.H2DatabaseInitTest;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FromEndToEndSensorsTest extends H2DatabaseInitTest {
    private final MockMvc mockMvc;

    @Autowired
    private SensorsRepository sensorsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public FromEndToEndSensorsTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testRegisterWithValidSensor() throws Exception {
        List<Sensor> receivedSensorsInitial = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsInitial.size());
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("test name");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        List<Sensor> receivedSensorsAfterSensorRegistration = sensorsRepository.findAll();
        assertEquals(3, receivedSensorsAfterSensorRegistration.size());
    }

    @Test
    public void testRegisterWithNotUniqueSensor() throws Exception {
        List<Sensor> receivedSensorsInitial = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsInitial.size());
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("SecondSensor");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor with this name already exists")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Sensor> receivedSensorsAfterSensorRegistration = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsAfterSensorRegistration.size());
    }

    @Test
    public void testRegisterWithSensorNameLessThan3Characters() throws Exception {
        List<Sensor> receivedSensorsInitial = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsInitial.size());
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("Se");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor name should be between 3 and 30 characters")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Sensor> receivedSensorsAfterSensorRegistration = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsAfterSensorRegistration.size());
    }


    @Test
    public void testRegisterWithSensorNameMoreThan30Characters() throws Exception {
        List<Sensor> receivedSensorsInitial = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsInitial.size());
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("sensor".repeat(6));

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor name should be between 3 and 30 characters")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Sensor> receivedSensorsAfterSensorRegistration = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsAfterSensorRegistration.size());
    }

    @Test
    public void testRegisterWithEmptySensorName() throws Exception {
        List<Sensor> receivedSensorsInitial = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsInitial.size());
        SensorDTO sensorDTO = new SensorDTO();

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sensorDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor name should not be empty")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Sensor> receivedSensorsAfterSensorRegistration = sensorsRepository.findAll();
        assertEquals(2, receivedSensorsAfterSensorRegistration.size());
    }
}
