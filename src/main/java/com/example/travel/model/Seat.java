package com.example.travel.model;

public class Seat {

    private String number;
    private SeatClass seatClass;
    private boolean booked;

    public Seat(String number, SeatClass seatClass) {
        if (number == null || number.isBlank()) {
            throw new IllegalArgumentException("Seat number cannot be empty");
        }
        if (seatClass == null) {
            throw new IllegalArgumentException("Seat class is required");
        }

        this.number = number;
        this.seatClass = seatClass;
        this.booked = false;
    }

    public void book() {
        if (booked) {
            throw new IllegalStateException("Seat " + number + " is already booked");
        }
        booked = true;
    }

    public void release() {
        booked = false;
    }

    public String getNumber() { return number; }
    public SeatClass getSeatClass() { return seatClass; }
    public boolean isBooked() { return booked; }
}