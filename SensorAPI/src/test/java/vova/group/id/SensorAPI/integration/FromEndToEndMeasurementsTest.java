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
import vova.group.id.SensorAPI.dto.MeasurementDTO;
import vova.group.id.SensorAPI.dto.SensorDTO;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.services.MeasurementsService;
import vova.group.id.SensorAPI.utils.H2DatabaseInitTest;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FromEndToEndMeasurementsTest extends H2DatabaseInitTest {
    private final MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MeasurementsService measurementsService;

    @Autowired
    public FromEndToEndMeasurementsTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void getMeasurementsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements"))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$", hasSize(2)),
                        MockMvcResultMatchers.jsonPath("$[0].id").doesNotExist(),
                        MockMvcResultMatchers.jsonPath("$[0].temperature", is(21.5)),
                        MockMvcResultMatchers.jsonPath("$[0].raining", is(false)),
                        MockMvcResultMatchers.jsonPath("$[0].sensor.name", is("FirstSensor")),
                        MockMvcResultMatchers.jsonPath("$[0].created_at").doesNotExist(),
                        MockMvcResultMatchers.jsonPath("$[1].id").doesNotExist(),
                        MockMvcResultMatchers.jsonPath("$[1].temperature", is(15.2)),
                        MockMvcResultMatchers.jsonPath("$[1].raining", is(true)),
                        MockMvcResultMatchers.jsonPath("$[1].sensor.name", is("SecondSensor")),
                        MockMvcResultMatchers.jsonPath("$[1].created_at").doesNotExist());
    }

    @Test
    public void getRainyDaysTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements/rainyDaysCount"))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$", is(1)));
    }

    @Test
    public void addValidMeasurementTest() throws Exception {
        List<Measurement> receivedMeasurementsInitial = measurementsService.findAll();
        assertEquals(2, receivedMeasurementsInitial.size());

        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setTemperature(30.0);
        measurementDTO.setRaining(false);
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("FirstSensor");
        measurementDTO.setSensor(sensorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isOk(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

        List<Measurement> receivedMeasurementsAfterAdding = measurementsService.findAll();
        assertEquals(3, receivedMeasurementsAfterAdding.size());
    }

    @Test
    public void addMeasurementWithNotExistingSensorTest() throws Exception {
        List<Measurement> receivedMeasurementsInitial = measurementsService.findAll();
        assertEquals(2, receivedMeasurementsInitial.size());

        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setTemperature(30.0);
        measurementDTO.setRaining(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor does not exist")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Measurement> receivedMeasurementsAfterAdding = measurementsService.findAll();
        assertEquals(2, receivedMeasurementsAfterAdding.size());
    }

    @Test
    public void addMeasurementWithUnknownSensorTest() throws Exception {
        List<Measurement> receivedMeasurementsInitial = measurementsService.findAll();
        assertEquals(2, receivedMeasurementsInitial.size());

        MeasurementDTO measurementDTO = new MeasurementDTO();
        measurementDTO.setTemperature(30.0);
        measurementDTO.setRaining(false);
        SensorDTO sensorDTO = new SensorDTO();
        sensorDTO.setName("UnknownSensor");
        measurementDTO.setSensor(sensorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(measurementDTO)))
                .andExpectAll(
                        status().isBadRequest(),
                        MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON),
                        MockMvcResultMatchers.jsonPath("$.message").value(
                                containsString("Sensor does not exist")),
                        MockMvcResultMatchers.jsonPath("$.timestamp", instanceOf(Long.class))
                );

        List<Measurement> receivedMeasurementsAfterAdding = measurementsService.findAll();
        assertEquals(2, receivedMeasurementsAfterAdding.size());
    }
}
