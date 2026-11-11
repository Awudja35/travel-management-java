package com.example.travel.model;
import com.example.travel.exception.NoSeatsAvailable;
import com.sun.jdi.connect.Transport;

import java.time.Duration;
import java.time.LocalDateTime;
public class Trip {
    private int id;
    private ModeOfTransport modeOfTransport;
    private TransportProvider transport;
    private LocalDateTime departure;
    private LocalDateTime arrival;
    private Location departureLocation;
    private Location arrivalLocation;
    private double price;
    private int economySeats;
    private int businessSeats;
    private int firstSeats;


    public Trip(int id, ModeOfTransport modeOfTransport, TransportProvider provider,
                Location departureLocation, Location arrivalLocation,
                LocalDateTime departure, LocalDateTime arrival,
                double price, int economySeats, int businessSeats, int firstSeats) {

        if (id <= 0) {
            throw new IllegalArgumentException("Trip id must be positive");
        }
        if (modeOfTransport == null) {
            throw new IllegalArgumentException("Mode of transport is required");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Transport provider is required");
        }
        if (departureLocation == null || arrivalLocation == null) {
            throw new IllegalArgumentException("Departure and arrival locations are required");
        }
        if (departureLocation.getName().equals(arrivalLocation.getName())) {
            throw new IllegalArgumentException("Departure and arrival locations must be different");
        }
        if (departure == null || arrival == null) {
            throw new IllegalArgumentException("Departure and arrival times are required");
        }
        if (!arrival.isAfter(departure)) {
            throw new IllegalArgumentException("Arrival must be after departure");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be positive");
        }
        if (economySeats < 0 || businessSeats < 0 || firstSeats < 0) {
            throw new IllegalArgumentException("Seat counts cannot be negative");
        }

        this.id = id;
        this.modeOfTransport = modeOfTransport;
        this.transport = provider;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.economySeats = economySeats;
        this.businessSeats = businessSeats;
        this.firstSeats = firstSeats;
    }

    public Duration travelDuration() {
        return Duration.between(departure, arrival);
    }

    public int getAvailableSeats(SeatClass seatClass) {
        if (seatClass == SeatClass.ECONOMY) {
            return economySeats;
        } else if (seatClass == SeatClass.BUSINESS) {
            return businessSeats;
        } else {
            return firstSeats;
        }
    }

    public void reserveSeat(SeatClass seatClass) {
        if (getAvailableSeats(seatClass) == 0) {
            throw new NoSeatsAvailable(
                    "No " + seatClass + " seats left on trip " + id);
        }

        if (seatClass == SeatClass.ECONOMY) {
            economySeats--;
        } else if (seatClass == SeatClass.BUSINESS) {
            businessSeats--;
        } else {
            firstSeats--;
        }
    }

    public void releaseSeat(SeatClass seatClass) {
        if (seatClass == SeatClass.ECONOMY) {
            economySeats++;
        } else if (seatClass == SeatClass.BUSINESS) {
            businessSeats++;
        } else {
            firstSeats++;
        }
    }

    public int getId() { return id; }
    public ModeOfTransport getModeOfTransport() { return modeOfTransport; }
    public TransportProvider getProvider() { return transport; }
    public Location getDepartureLocation() { return departureLocation; }
    public Location getArrivalLocation() { return arrivalLocation; }
    public LocalDateTime getDeparture() { return departure; }
    public LocalDateTime getArrival() { return arrival; }
    public double getPrice() { return price; }
}
