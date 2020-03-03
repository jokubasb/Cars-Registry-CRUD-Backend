package com.jokubas.lab1.controller;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jokubas.lab1.model.Car;
import com.jokubas.lab1.repository.CarRepository;
import com.jokubas.lab1.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class CarController {
    @Autowired
    private CarRepository carRepository;

    @GetMapping("/cars")
    public List <Car> getAllCars(){
        return carRepository.findAll();
    }

    //get
    @GetMapping("/cars/{id}")
    public ResponseEntity <Car> getCarById(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException{
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        return ResponseEntity.ok().body(car);
    }

    //create
    @PostMapping("/cars")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity <Car> createCar(@Valid @RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carRepository.save(car));
    }

    //update
    @PutMapping("/cars/{id}")
    public ResponseEntity <Car> updateCar(@PathVariable(value = "id") Long carId,
                                          @Valid @RequestBody Car carDetails) throws ResourceNotFoundException{
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new ResourceNotFoundException("Car not found for this id :: " + carId));

        car.setManufacturer(carDetails.getManufacturer());
        car.setModel(carDetails.getModel());
        car.setYear(carDetails.getYear());
        final Car updatedCar = carRepository.save(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(updatedCar);
    }

    //delete
    @DeleteMapping("/cars/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map <String, Boolean> deleteCar(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException{
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new ResourceNotFoundException("Car not found for this id :: " + carId));

        carRepository.delete(car);
        Map <String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}
