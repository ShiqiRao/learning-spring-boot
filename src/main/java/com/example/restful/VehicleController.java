package com.example.restful;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.beans.FeatureDescriptor;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleRepository vehicleRepository;

    public VehicleController(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Vehicle>> vehicleList() {
        return new ResponseEntity<>(vehicleRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> selectOne(@PathVariable Long id) {
        return vehicleRepository.findById(id).map(v -> new ResponseEntity<>(v, HttpStatus.OK))
                .orElse(new ResponseEntity<>(null, HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/")
    public ResponseEntity<Vehicle> createOne(@RequestBody Vehicle vehicle) {
        return new ResponseEntity<>(vehicleRepository.save(vehicle), HttpStatus.OK);
    }

    @PutMapping("/")
    public ResponseEntity<Vehicle> replaceOne(@RequestBody Vehicle vehicle) {
        Optional<Vehicle> oldOne = vehicleRepository.findById(vehicle.getId());
        if (!oldOne.isPresent()) {
            new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(vehicleRepository.save(vehicle), HttpStatus.OK);
    }

    @PatchMapping("/")
    public ResponseEntity<Vehicle> modifyOne(@RequestBody Vehicle vehicle) {
        Optional<Vehicle> findById = vehicleRepository.findById(vehicle.getId());
        Vehicle oldOne;
        if (!findById.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } else {
            oldOne = findById.get();
        }
        Vehicle newOne = new Vehicle();
        List<String> nullProperties = getNullProperties(newOne);
        BeanUtils.copyProperties(newOne, oldOne, nullProperties.toArray(new String[0]));
        return new ResponseEntity<>(vehicleRepository.save(oldOne), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Vehicle> deleteOne(@PathVariable Long id) {
        vehicleRepository.deleteById(id);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    private List<String> getNullProperties(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> Objects.isNull(wrappedSource.getPropertyValue(propertyName)))
                .collect(Collectors.toList());
    }

}
