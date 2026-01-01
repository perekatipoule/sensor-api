package vova.group.id.SensorAPI.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
public class H2DatabaseInitTest {
    @Value("${sql.script.create.table.sensor}")
    protected String createTableSensor;

    @Value("${sql.script.create.table.measurement}")
    protected String createTableMeasurement;

    @Value("${sql.script.add.sensors}")
    protected String addSensors;

    @Value("${sql.script.add.measurements}")
    protected String addMeasurements;

    @Value("${sql.script.drop.table.sensor}")
    protected String dropTableSensor;

    @Value("${sql.script.drop.table.measurement}")
    protected String dropTableMeasurement;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute(createTableSensor);
        jdbcTemplate.execute(createTableMeasurement);
        jdbcTemplate.execute(addSensors);
        jdbcTemplate.execute(addMeasurements);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dropTableMeasurement);
        jdbcTemplate.execute(dropTableSensor);
    }
}
