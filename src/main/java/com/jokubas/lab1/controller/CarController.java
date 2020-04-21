package com.jokubas.lab1.controller;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.jokubas.lab1.model.Car;
import com.jokubas.lab1.model.CarAndOwner;
import com.jokubas.lab1.model.Owner;
import com.jokubas.lab1.repository.CarRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jokubas.lab1.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class CarController {
    @Autowired
    private CarRepository carRepository;
    

    @GetMapping("/cars")
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    // get
    @GetMapping("/cars/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        return ResponseEntity.ok().body(car);
    }

    // create
    @PostMapping("/cars")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Car> createCar(@Valid @RequestBody Car car) {
        try {
            final Car saveCar = carRepository.save(car);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveCar.getId()).toUri();
            return ResponseEntity.created(location).body(saveCar);
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }

    }

    // update
    @PutMapping("/cars/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable(value = "id") Long carId, @Valid @RequestBody Car carDetails)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        try {
            car.setManufacturer(carDetails.getManufacturer());
            car.setModel(carDetails.getModel());
            car.setYear(carDetails.getYear());
            final Car updatedCar = carRepository.save(car);
            return ResponseEntity.status(HttpStatus.OK).body(updatedCar);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    // delete
    @DeleteMapping("/cars/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteCar(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));

        carRepository.delete(car);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    @GetMapping("/owners")
    public Owner[] getAllOwners() throws JsonParseException, JsonMappingException, IOException {
        final String uri = "http://localhost:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, Owner[].class);
    }



    @GetMapping("/owners/{id}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        final String uri = "http://localhost:5000/contacts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return ResponseEntity.ok().body(restTemplate.getForObject(uri, Owner.class));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }

    }

    
    @PostMapping("/owners")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<String> createOwner(@Valid @RequestBody Owner owner) {
        try {
            final Owner saveOwner = owner;
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveOwner.getId()).toUri();
            final String uri = "http://localhost:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
             // create headers
            HttpHeaders headers = new HttpHeaders();
            // set `content-type` header
            headers.setContentType(MediaType.APPLICATION_JSON);
            // set `accept` header
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            // build the request
            HttpEntity<Owner> entity = new HttpEntity<>(saveOwner, headers);
            return ResponseEntity.created(location).body(restTemplate.postForObject(uri, entity, String.class));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    @PutMapping("/owners/{id}")
    public ResponseEntity<Owner> updateOwner(@PathVariable(value = "id") Long id, @Valid @RequestBody Owner ownerDetails)
            throws ResourceNotFoundException {
        final String uri = "http://localhost:5000/contacts/" + id;
        try {
            RestTemplate restTemplate = new RestTemplate();
             // create headers
             HttpHeaders headers = new HttpHeaders();
             // set `content-type` header
             headers.setContentType(MediaType.APPLICATION_JSON);
             // set `accept` header
             headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
             // build the request
             HttpEntity<Owner> entity = new HttpEntity<>(ownerDetails, headers);
             restTemplate.put(uri, entity, Owner.class);
            return ResponseEntity.status(HttpStatus.OK).body(ownerDetails);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    @DeleteMapping("/owners/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteOwner(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        final String uri = "http://localhost:5000/contacts/" + id;
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(uri, 10);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }

    //----------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/ownedcars")
    public List<CarAndOwner> getAllOwnedCars() throws JsonParseException, JsonMappingException, IOException {
        List<Car> cars = carRepository.findAll();
        final String uri = "http://localhost:5000/contacts";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        List<Owner> owners = objectMapper.readValue(response, new TypeReference<List<Owner>>(){});
        List<CarAndOwner> ownedCars = new ArrayList<CarAndOwner>();
        for(Car car : cars){
            for(Owner owner : owners){
                if(owner.getId() == car.getOwnerId()){
                    CarAndOwner cao = new CarAndOwner();
                    BeanUtils.copyProperties(car, cao);
                    BeanUtils.copyProperties(owner, cao);
                    cao.setId((int)car.getId());
                    ownedCars.add(cao);
                }
            }
        }
        return ownedCars;
    }
    
    // get
    @GetMapping("/ownedcars/{id}")
    public ResponseEntity<CarAndOwner> getOwnedCarById(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        final String uri = "http://localhost:5000/contacts/" + car.getOwnerId();
        RestTemplate restTemplate = new RestTemplate();
        try{
            Owner owner = restTemplate.getForObject(uri, Owner.class);
            CarAndOwner cao = new CarAndOwner();
            BeanUtils.copyProperties(car, cao);
            BeanUtils.copyProperties(owner, cao);
            cao.setId((int)car.getId());
            return ResponseEntity.ok().body(cao);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }
    }


    
    // create
    @PostMapping("/ownedcars")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<CarAndOwner> createOwnedCar(@Valid @RequestBody CarAndOwner carandowner)
            throws ResourceNotFoundException {
        try {
            Car car = new Car();
            BeanUtils.copyProperties(carandowner, car);
            //car.setOwnerId((int) car.getId());
            Car saveCar = carRepository.save(car);
            car = carRepository.findById(saveCar.getId()).orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: "));
            car.setOwnerId((int)saveCar.getId()*10);
            saveCar = carRepository.save(car);

            Owner owner = new Owner();
            BeanUtils.copyProperties(carandowner, owner);
            owner.setId(saveCar.getOwnerId());
            carandowner.setId(((int)saveCar.getId()));
            final Owner saveOwner = owner;

            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveCar.getId()).toUri();
            
            //post to contacts
            final String uri = "http://localhost:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Owner> entity = new HttpEntity<>(saveOwner, headers); 
            restTemplate.postForObject(uri, entity, String.class);

            return ResponseEntity.created(location).body(carandowner);
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }

    }

    /*TODO
    // update
    @PutMapping("/ownedcars/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable(value = "id") Long carId, @Valid @RequestBody Car carDetails)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        try {
            car.setManufacturer(carDetails.getManufacturer());
            car.setModel(carDetails.getModel());
            car.setYear(carDetails.getYear());
            final Car updatedCar = carRepository.save(car);
            return ResponseEntity.status(HttpStatus.OK).body(updatedCar);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    // delete
    @DeleteMapping("/ownedcars/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteCar(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));

        carRepository.delete(car);
        Map<String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
    */


}
