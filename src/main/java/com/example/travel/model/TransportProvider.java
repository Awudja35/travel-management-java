package com.example.travel.model;

public class TransportProvider {
    private int id;
    private String name;


    public TransportProvider(int id, String name){
        if(id <= 0){
            throw new IllegalArgumentException("Transport provider id cannot be null or blank");
        }
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Transport Provider name cannot be null or blank");
        }


        this.id = id;
        this.name = name;
    }
    public int getID(){return id;};
    public String getName(){return name;}
}
