package com.example.travel.service;

import com.example.travel.exception.NoSeatsAvailable;
import com.example.travel.exception.PaymentFailed;
import com.example.travel.model.Booking;
import com.example.travel.model.SeatClass;
import com.example.travel.model.Trip;
import com.example.travel.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BookingService {

    private Map<Integer, Booking> bookings = new LinkedHashMap<>();
    private int nextId = 1;

    public Booking book(User user, Trip trip, int tickets, SeatClass seatClass) {
        Booking booking = new Booking(nextId, user, trip, tickets, seatClass);
        bookings.put(nextId, booking);
        nextId++;
        return booking;
    }

    public Booking bookAndPay(User user, Trip trip, int tickets, SeatClass seatClass) {
        Booking booking = book(user, trip, tickets, seatClass);

        try {
            booking.pay();
        } catch (PaymentFailed | NoSeatsAvailable e) {
            // a failed payment must not leave a booking behind
            bookings.remove(booking.getId());
            throw e;
        }

        return booking;
    }

    public void upgrade(User user, int bookingId, SeatClass newClass) {
        Booking booking = findBooking(user, bookingId);
        booking.upgrade(newClass);
    }

    public void cancel(User user, int bookingId) {
        Booking booking = findBooking(user, bookingId);
        booking.cancel();
    }

    public List<Booking> getBookingsOf(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getUser().getId() == user.getId()) {
                result.add(booking);
            }
        }
        return result;
    }

    public Booking findBooking(User user, int bookingId) {
        if (user == null) {
            throw new IllegalArgumentException("User is required");
        }

        Booking booking = bookings.get(bookingId);
        if (booking == null || booking.getUser().getId() != user.getId()) {
            throw new IllegalArgumentException("Booking " + bookingId + " not found");
        }
        return booking;
    }
}