package com.jokubas.lab1.model;

public class CarAndOwner {
    private long id;
    private String manufacturer;
    private String model;
    private int year;
    private String surname;
    private String name;
    private String number;
    private String email;

    public void setId(int id){
        this.id = id;
    }
    public void setManufacturer(String m){
        this.manufacturer = m;
    }
    public void setModel(String m){
        this.model = m;
    }
    public void setYear(int y){
        this.year = y;
    }
    public void setSurname(String s){
        this.surname = s;
    }
    public void setName(String n){
        this.name = n;
    }
    public void setNumber(String n){
        this.number = n;
    }
    public void setEmail(String e){
        this.email = e;
    }

    public long getId(){
        return this.id;
    }

    public String getSurname(){
        return surname;
    }

    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public String getEmail(){
        return email;
    }

    //car
    public String getManufacturer() {
        return manufacturer;
    }

    public String getModel() {
        return model;
    }
    
    public int getYear() {
        return year;
    }


}
