package com.example.travel.model;

import com.example.travel.exception.InvalidUpgrade;
import com.example.travel.exception.NoSeatsAvailable;

public class Booking {

    private int id;
    private User user;
    private Trip trip;
    private int numberOfTickets;
    private SeatClass seatClass;
    private BookingStatus status;
    private double amountPaid;

    public Booking(int id, User user, Trip trip, int numberOfTickets, SeatClass seatClass) {
        if (id <= 0) {
            throw new IllegalArgumentException("Booking id must be positive");
        }
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }
        if (trip == null) {
            throw new IllegalArgumentException("Trip is required");
        }
        if (numberOfTickets < 1) {
            throw new IllegalArgumentException("At least one ticket is required");
        }
        if (seatClass == null) {
            throw new IllegalArgumentException("Seat class is required");
        }

        this.id = id;
        this.user = user;
        this.trip = trip;
        this.numberOfTickets = numberOfTickets;
        this.seatClass = seatClass;
        this.status = BookingStatus.PENDING_PAYMENT;
        this.amountPaid = 0;
    }

    // Price of this booking if it were in the given seat class
    private double calculatePrice(SeatClass chosenClass) {
        double fullPrice = trip.getPrice() * chosenClass.getPriceMultiplier() * numberOfTickets;
        return fullPrice - fullPrice * user.getDiscountRate();
    }

    public double getTotalPrice() {
        return calculatePrice(seatClass);
    }

    public void pay() {
        if (status != BookingStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Only a booking waiting for payment can be paid");
        }
        if (trip.getAvailableSeats(seatClass) < numberOfTickets) {
            throw new NoSeatsAvailable(
                    "Not enough " + seatClass + " seats left on trip " + trip.getId());
        }

        double price = calculatePrice(seatClass);
        user.withdraw(price);

        for (int i = 0; i < numberOfTickets; i++) {
            trip.reserveSeat(seatClass);
        }

        amountPaid = price;
        status = BookingStatus.CONFIRMED;
    }

    public void upgrade(SeatClass newClass) {
        if (status != BookingStatus.CONFIRMED) {
            throw new InvalidUpgrade("Only a paid booking can be upgraded");
        }
        if (newClass == null) {
            throw new IllegalArgumentException("New seat class is required");
        }
        if (newClass.getPriceMultiplier() <= seatClass.getPriceMultiplier()) {
            throw new InvalidUpgrade(
                    "Cannot go from " + seatClass + " to " + newClass + ", it is not an upgrade");
        }
        if (trip.getAvailableSeats(newClass) < numberOfTickets) {
            throw new NoSeatsAvailable(
                    "Not enough " + newClass + " seats left on trip " + trip.getId());
        }

        double difference = calculatePrice(newClass) - amountPaid;
        user.withdraw(difference);

        for (int i = 0; i < numberOfTickets; i++) {
            trip.reserveSeat(newClass);
            trip.releaseSeat(seatClass);
        }

        seatClass = newClass;
        amountPaid = amountPaid + difference;
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Booking is already cancelled");
        }

        if (status == BookingStatus.CONFIRMED) {
            for (int i = 0; i < numberOfTickets; i++) {
                trip.releaseSeat(seatClass);
            }
            user.deposit(amountPaid);
            amountPaid = 0;
        }

        status = BookingStatus.CANCELLED;
    }

    public int getId() { return id; }
    public User getUser() { return user; }
    public Trip getTrip() { return trip; }
    public int getNumberOfTickets() { return numberOfTickets; }
    public SeatClass getSeatClass() { return seatClass; }
    public BookingStatus getStatus() { return status; }
    public double getAmountPaid() { return amountPaid; }
}