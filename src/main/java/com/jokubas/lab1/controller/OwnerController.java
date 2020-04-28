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
import com.jokubas.lab1.exception.ResourceNotFoundException;


@RestController
@RequestMapping("/api/v1")
public class OwnerController {
    final String uri = "http://contacts:5000/contacts/";
    RestTemplate restTemplate = new RestTemplate();

    /*
    public boolean initialData(){
        Owner owner = new Owner();
        boolean running = false;
            final String uri = "http://contacts:5000/contacts/";
            RestTemplate restTemplate = new RestTemplate();
            try{
                if(restTemplate.getForObject(uri, Owner.class) == null) return running;
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
                    return running;
            }catch(Exception ex){
                return running;
            }
    }
    */


    @GetMapping("/owners")
    public Owner[] getAllOwners() throws JsonParseException, JsonMappingException, IOException {
        try{
            return restTemplate.getForObject(uri, Owner[].class);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }
    }



    @GetMapping("/owners/{id}")
    public ResponseEntity<Owner> getOwnerById(@PathVariable(value = "id") Long id) throws ResourceNotFoundException,
            JsonParseException, JsonMappingException, IOException {
        final String uriWithId = uri + id;
        RestTemplate restTemplate = new RestTemplate();
        try{
            return ResponseEntity.ok().body(restTemplate.getForObject(uriWithId, Owner.class));
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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Owner> entity = new HttpEntity<>(saveOwner, headers);
            return ResponseEntity.created(location).body(restTemplate.postForObject(uri, entity, String.class));
        } catch (DataAccessException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }
    
    @PutMapping("/owners/{id}")
    public ResponseEntity<Owner> updateOwner(@PathVariable(value = "id") Long id, @Valid @RequestBody Owner ownerDetails)
            throws ResourceNotFoundException {
        final String uriWithId = uri + id;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<Owner> entity = new HttpEntity<>(ownerDetails, headers);
            restTemplate.put(uriWithId, entity, Owner.class);
            return ResponseEntity.status(HttpStatus.OK).body(ownerDetails);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong data structure", ex);
        }
    }

    @DeleteMapping("/owners/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public Map<String, Boolean> deleteOwner(@PathVariable(value = "id") Long id) throws ResourceNotFoundException {
        try{
            final String uriWithId = uri + id;
            restTemplate.delete(uriWithId, 10);
            Map<String, Boolean> response = new HashMap<>();
            response.put("deleted", Boolean.TRUE);
            return response;
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Owner not found", e);
        }
    }

    
}