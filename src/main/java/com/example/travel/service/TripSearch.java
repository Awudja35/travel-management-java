package com.example.travel.service;

import com.example.travel.model.Trip;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripSearch {

    private List<Trip> trips;

    public TripSearch(List<Trip> trips) {
        if (trips == null) {
            throw new IllegalArgumentException("Trip list is required");
        }
        this.trips = trips;
    }

    public List<Trip> search(String originCity, String destinationCity, LocalDate date) {
        if (originCity == null || originCity.isBlank()) {
            throw new IllegalArgumentException("Origin city is required");
        }
        if (destinationCity == null || destinationCity.isBlank()) {
            throw new IllegalArgumentException("Destination city is required");
        }

        List<Trip> results = new ArrayList<>();

        for (Trip trip : trips) {
            String tripOrigin = trip.getDepartureLocation().getCity();
            String tripDestination = trip.getArrivalLocation().getCity();

            if (tripOrigin.equalsIgnoreCase(originCity)
                    && tripDestination.equalsIgnoreCase(destinationCity)) {

                if (date == null) {
                    results.add(trip);
                } else if (trip.getDeparture().toLocalDate().equals(date)) {
                    results.add(trip);
                }
            }
        }

        return results;
    }
}