package com.example.travel.model;

public class ModeOfTransport {
    private int id;
    private String name;

    public ModeOfTransport(int id, String name){
        if(id <= 0){
            throw new IllegalArgumentException("Mode of Transport id cannot be null or blank");
        }
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Mode of Transport name cannot be null or blank");
        }
        this.id = id;
        this.name = name;
    }
    public int getId() {return id;}
    public String getName() {return name;}
}