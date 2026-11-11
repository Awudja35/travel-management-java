package com.example.travel.model;

public class Location {
    private String name;
    private String city;
    private String country;

    public Location(String name, String city, String country) {
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("Location name cannot be null or blank");
        }
        if(city == null || city.isBlank()){
            throw new IllegalArgumentException("Location city cannot be null or blank");
        }
        if(country == null || country.isBlank()){
            throw new IllegalArgumentException("Location country cannot be null or blank");
        }
        this.name = name;
        this.city = city;
        this.country = country;
    }
    public String getName() {return name;}
    public String getCity() {return city;}
    public String getCountry() {return country;}
}
