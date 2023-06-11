package vova.group.id.SensorAPI.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import vova.group.id.SensorAPI.dto.MeasurementDTO;
import vova.group.id.SensorAPI.exceptions.MeasurementNotCreatedException;
import vova.group.id.SensorAPI.models.Measurement;
import vova.group.id.SensorAPI.services.MeasurementsService;
import vova.group.id.SensorAPI.util.*;
import vova.group.id.SensorAPI.validators.MeasurementValidator;

import java.util.List;
import java.util.stream.Collectors;

import static vova.group.id.SensorAPI.util.ErrorsUtil.formErrorsMessage;


@RestController
@RequestMapping("/measurements")
public class MeasurementsController {

    private final ModelMapper modelMapper;
    private final MeasurementsService measurementsService;
    private final MeasurementValidator measurementValidator;

    @Autowired
    public MeasurementsController(ModelMapper modelMapper, MeasurementsService measurementsService, MeasurementValidator measurementValidator) {
        this.modelMapper = modelMapper;
        this.measurementsService = measurementsService;
        this.measurementValidator = measurementValidator;
    }

    @GetMapping
    public List<MeasurementDTO> getMeasurements() {
        return measurementsService.findAll().stream()
                .map(this::convertToMeasurementDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/rainyDaysCount")
    public Long getRainyDays() {
        return measurementsService.findAll().stream().
                filter(Measurement::getRaining).
                count();
    }


    @PostMapping("/add")
    public ResponseEntity<HttpStatus> add(@RequestBody @Valid MeasurementDTO measurementDTO,
                                          BindingResult bindingResult) {

        Measurement measurement = convertToMeasurement(measurementDTO);
        measurementValidator.validate(measurement, bindingResult);

        if (bindingResult.hasErrors())
            throw new MeasurementNotCreatedException(formErrorsMessage(bindingResult));

        measurementsService.save(measurement);

        return ResponseEntity.ok(HttpStatus.OK);
    }



    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(MeasurementNotCreatedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        return modelMapper.map(measurementDTO, Measurement.class);
    }

    private MeasurementDTO convertToMeasurementDTO(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }
}
