package com.jokubas.lab1.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jokubas.lab1.exception.ResourceNotFoundException;

@RestController
@RequestMapping("/api/v1")
public class CarController {
    public boolean running = false;
    @Autowired
    private CarRepository carRepository;
    //private OwnerRelay ownerRelay;
    

    /*
    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        running = ownerRelay.initialData();
    }
    */

    @EventListener(ApplicationReadyEvent.class)
    public void startup() {
        //initialData();
    }

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
    //----------------------------------------------------------------------------------------------------------------------------------

    @GetMapping("/ownedcars")
    public Object getAllOwnedCars() throws JsonParseException, JsonMappingException, IOException {
        List<Car> cars = carRepository.findAll();
        List<CarAndOwner> ownedCars = new ArrayList<CarAndOwner>();
        running = pingHost("contacts", 5000, 500);
        try{
                if(running){
                    List<Owner> owners = getOwnerList();
                    for(Car car : cars){
                        CarAndOwner cao = new CarAndOwner();
                            for(Owner owner : owners){
                                if(owner.getId() == car.getOwnerId()){
                                    BeanUtils.copyProperties(owner, cao);  
                                    BeanUtils.copyProperties(car, cao);
                                    cao.setId((int)car.getId());
                                    ownedCars.add(cao);
                                }   
                            }
                    }
                    return ownedCars;
                }else{
                    /*
                    for(Car car : cars){
                        CarAndOwner cao = new CarAndOwner();
                        BeanUtils.copyProperties(car, cao);
                        cao.setId((int)car.getId());
                        ownedCars.add(cao);
                    }
                    */
                    return cars;
                }
        }catch(Exception ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", ex);
        }
    }
    
    // get
    @GetMapping("/ownedcars/{id}")
    public ResponseEntity<Object> getOwnedCarById(@PathVariable(value = "id") Long carId) throws ResourceNotFoundException {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + carId));
        running = pingHost("contacts", 5000, 500);
        try{
            CarAndOwner cao = new CarAndOwner();
            if(running){
                Owner owner = getOwnerObjectById(car.getOwnerId());
                BeanUtils.copyProperties(owner, cao);
                BeanUtils.copyProperties(car, cao);
                cao.setId((int)car.getId());
                return ResponseEntity.ok().body(cao);
            }else{
                return ResponseEntity.ok().body(car);
            }
            
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }
    }


    
    // create
    @PostMapping("/ownedcars")
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity<Object> createOwnedCar(@Valid @RequestBody CarAndOwner carandowner)
            throws ResourceNotFoundException {
        running = pingHost("contacts", 5000, 500);
        try {
            Car car = new Car();
            BeanUtils.copyProperties(carandowner, car);
            Car saveCar = carRepository.save(car);
            car = carRepository.findById(saveCar.getId()).orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: "));
            car.setOwnerId((int)saveCar.getId()*10);
            saveCar = carRepository.save(car);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(saveCar.getId()).toUri();

            if(running){
                Owner owner = new Owner();
                BeanUtils.copyProperties(carandowner, owner);
                owner.setId(saveCar.getOwnerId());
                carandowner.setId(((int)saveCar.getId()));
                final Owner saveOwner = owner;
                postObject(saveOwner);
                return ResponseEntity.created(location).body(carandowner);
            }else{
                return ResponseEntity.created(location).body(car);
            }
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

        // create array
        @PostMapping("/ownedcarsArray")
        @ResponseStatus(value = HttpStatus.CREATED)
        public ResponseEntity<Object> createOwnedCarArray(@Valid @RequestBody List<CarAndOwner> carandowner)
                throws ResourceNotFoundException {
            running = pingHost("contacts", 5000, 500);
            Car saveCar = new Car();
            try {
                for(CarAndOwner cao : carandowner){
                    Car car = new Car();
                    BeanUtils.copyProperties(cao, car);
                    saveCar = carRepository.save(car);
                    car = carRepository.findById(saveCar.getId()).orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: "));
                    car.setOwnerId((int)saveCar.getId()*10);
                    saveCar = carRepository.save(car);
                    if(running){
                        Owner owner = new Owner();
                        BeanUtils.copyProperties(cao, owner);
                        owner.setId(saveCar.getOwnerId());
                        cao.setId(((int)saveCar.getId()));
                        final Owner saveOwner = owner;
                        postObject(saveOwner);
                    }
                }
                
                URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                        .buildAndExpand(saveCar.getId()).toUri();
                if(running){
                    return ResponseEntity.created(location).body(carandowner);
                }else{
                    return ResponseEntity.created(location).body(saveCar);
                }
            } catch (DataAccessException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
            }
        }


    
    // update
    @PutMapping("/ownedcars/{id}")
    public ResponseEntity<Object> updateOwnedCar(@PathVariable(value = "id") Long id, @Valid @RequestBody CarAndOwner ownedCarDetails)
            throws ResourceNotFoundException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + id));
        running = pingHost("contacts", 5000, 500);
        try {
            car.setManufacturer(ownedCarDetails.getManufacturer());
            car.setModel(ownedCarDetails.getModel());
            car.setYear(ownedCarDetails.getYear());
            final Car finalCar = carRepository.save(car);
            ownedCarDetails.setId((int)car.getId());

            if(running){
            Owner owner = getOwnerObjectById(car.getOwnerId());
            
            owner.setName(ownedCarDetails.getName());
            owner.setSurname(ownedCarDetails.getSurname());
            owner.setEmail(ownedCarDetails.getEmail());
            owner.setNumber(ownedCarDetails.getNumber());
            owner.setId(0);

            putObjrct(owner, car.getOwnerId());

            return ResponseEntity.status(HttpStatus.OK).body(ownedCarDetails);
            }else{
                return ResponseEntity.status(HttpStatus.OK).body(finalCar);
            }
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    // delete
    @DeleteMapping("/ownedcars/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteOwnedCar(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found for this id :: " + id));
        carRepository.delete(car);
        running = pingHost("contacts", 5000, 500);
        try{
            if(running)
                deleteOwnerById(car.getOwnerId());
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }
    }


    void initialData(){
        Owner owner = new Owner();
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            try{
                    running = true;
                    owner.setId(10);
                    owner.setName("Edwin");
                    owner.setSurname("Bird");
                    owner.setNumber("606-434-2825");
                    owner.setEmail("EdwinRBird@armyspy.com");
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                    HttpEntity<Owner> entity = new HttpEntity<>(owner, headers);
                    restTemplate.postForObject(uri, entity, String.class);
                    owner.setId(20);
                    owner.setName("Sarah");
                    owner.setSurname("Patterson");
                    owner.setNumber("321-512-3924");
                    owner.setEmail("SarahHPatterson@rhyta.com");
                    entity = new HttpEntity<>(owner, headers);
                    restTemplate.postForObject(uri, entity, String.class);
                    owner.setId(30);
                    owner.setName("Alisha");
                    owner.setSurname("Hayner");
                    owner.setNumber("678-237-3632");
                    owner.setEmail("AlishaJHayner@armyspy.com");
                    entity = new HttpEntity<>(owner, headers);
                    restTemplate.postForObject(uri, entity, String.class);
                    owner.setId(40);
                    owner.setName("Amanda");
                    owner.setSurname("Moorhead");
                    owner.setNumber("989-397-1216");
                    owner.setEmail("AmandaCMoorhead@teleworm.us");
                    entity = new HttpEntity<>(owner, headers);
                    restTemplate.postForObject(uri, entity, String.class);
            }catch(Exception ex){
                return;
            }
    }



    final String uri = "http://contacts:5000/contacts/";
    RestTemplate restTemplate = new RestTemplate();

    public List<Owner> getOwnerList() throws JsonParseException, JsonMappingException, IOException {
        List<Owner> owners;
        String response = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        owners = objectMapper.readValue(response, new TypeReference<List<Owner>>(){});
        return owners;
    }

    public Owner getOwnerObjectById(int id){
        final String uriWithId = uri + id;
        RestTemplate restTemplate = new RestTemplate();
        Owner owner = restTemplate.getForObject(uriWithId, Owner.class);
        return owner;
    }

    public void postObject(Owner owner){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Owner> entity = new HttpEntity<>(owner, headers); 
        restTemplate.postForObject(uri, entity, String.class);
    }

    public void putObjrct(Owner owner, int id){
        final String uriWithId = uri + id;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Owner> entity = new HttpEntity<>(owner, headers);
        restTemplate.put(uriWithId, entity, Owner.class);
    }

    public void deleteOwnerById(int id){
        final String uriWithId = uri + id;
        restTemplate.delete(uriWithId);
    }

    public static boolean pingHost(String host, int port, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeout);
            return true;
        } catch (IOException e) {
            return false; // Either timeout or unreachable or failed DNS lookup.
        }
    }
}
