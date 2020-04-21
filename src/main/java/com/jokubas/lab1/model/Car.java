package com.jokubas.lab1.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "cars")

public class Car {
    private long id;
    private String manufacturer;
    private String model;
    private int year;
    private int ownerId;

    public Car(){

    }

    public Car(String manufacturer, String model, int year, int ownerId){
        this.manufacturer = manufacturer;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "manufacturer", nullable = false)
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @Column(name = "model", nullable = false)
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    @Column(name = "year", nullable = false)
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    @Column(name = "owner", nullable = false)
    public int getOwnerId(){
        return ownerId;
    }
    public void setOwnerId(int id){
        this.ownerId = id;
    }

}
