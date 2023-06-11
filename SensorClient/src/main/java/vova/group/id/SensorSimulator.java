package vova.group.id;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SensorSimulator {
    public static void main(String[] args) {

        // register sensor to server
        String sensorName = "sensorName";
        registerSensor(sensorName);

        // add 1000 random measurement to server in the temperature range from -100 to 100 degrees
        for (int i = 0; i < 1; i++) {
            addMeasurement(generateRandTemp(-100, 100), generateRandomRaining(), sensorName);
        }

    }


    public static void registerSensor(String sensorName) {

        String url = "http://localhost:8080/sensors/registration";

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("name", sensorName);

        makePostRequest(jsonData, url);
    }

    public static void addMeasurement(double temperature, boolean raining, String sensorName) {

        String url = "http://localhost:8080/measurements/add";

        Map<String, Object> jsonData = new HashMap<>();
        jsonData.put("temperature", String.valueOf(temperature));
        jsonData.put("raining", String.valueOf(raining));
        jsonData.put("sensor", Map.of("name", sensorName));

        makePostRequest(jsonData, url);
    }


    private static void makePostRequest(Map<String, Object> jsonData, String url) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> request = new HttpEntity<>(jsonData, headers);

        try{
            String response = restTemplate.postForObject(url, request, String.class);
            System.out.println(response + ": Request sent successfully");
        } catch (HttpClientErrorException e) {
            System.out.println("Request error");
            System.out.println(e.getMessage());
        }
    }

    private static boolean generateRandomRaining() {
        Random random = new Random();
        return random.nextBoolean();
    }

    private static double generateRandTemp(double minTemp, double maxTemp) {
        Random random = new Random();
        double randomValue = minTemp + (maxTemp - minTemp) * random.nextDouble();

        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String roundedValueString = decimalFormat.format(randomValue);
        double roundedValue = Double.parseDouble(roundedValueString);

        return roundedValue;
    }


}