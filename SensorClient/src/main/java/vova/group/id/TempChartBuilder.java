package vova.group.id;

import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.springframework.web.client.RestTemplate;
import vova.group.id.dto.MeasurementDTO;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TempChartBuilder {

    public static void main(String[] args) {
        List<Double> temperatures = getTemperatureFromServer();
        drawChart(temperatures);
    }

    public static List<Double> getTemperatureFromServer() {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8080/measurements";

        MeasurementDTO[] measurements = restTemplate.getForObject(url, MeasurementDTO[].class);

        if (measurements == null)
            return Collections.emptyList();

        return Arrays.stream(measurements)
                .map(MeasurementDTO::getTemperature)
                .collect(Collectors.toList());
    }

    private static void drawChart(List<Double> temperatures) {
        double[] xData = IntStream.range(0, temperatures.size()).asDoubleStream().toArray();
        double[] yData = temperatures.stream().mapToDouble(x -> x).toArray();

        XYChart chart = QuickChart.getChart("Temperature chart", "measurements", "temperatures", "temperature",
                xData, yData);

        new SwingWrapper(chart).displayChart();
    }

}
