package com.example.travel;
import com.example.travel.model.Trip;


import com.example.travel.utils.DataLoader;
import com.example.travel.utils.TripCsvLoader;


import java.nio.file.Path;
import java.util.List;

public class Application {
     static void main(String[] args) {
         DataLoader<Trip> tripLoader = new TripCsvLoader();
         List<Trip> trips = tripLoader.load(Path.of("src/main/resources/trips.csv"));

         for (Trip trip : trips) {
             System.out.println(trip.getModeOfTransport().getName() + ": "
                     + trip.getDepartureLocation().getCity() + " -> "
                     + trip.getArrivalLocation().getCity()
                     + ", " + trip.getPrice() + " EUR, "
                     + trip.travelDuration().toMinutes() + " min");
        }
        }
    }


