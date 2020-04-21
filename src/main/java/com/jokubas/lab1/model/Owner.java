package com.jokubas.lab1.model;

import java.io.Serializable;

public class Owner{
    private int id;
    private String surname;
    private String name;
    private String number;
    private String email;

    public Owner(){}

    public Owner(int id, String surname, String name, String number, String email){
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.number = number;
        this.email = email;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setSurname(String surname){
        this.surname = surname;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public void setEmail(String email){

        this.email = email;
    }

    public int getId(){
        return id;
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


}