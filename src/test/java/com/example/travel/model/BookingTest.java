package com.example.travel.model;

import com.example.travel.exception.NoSeatsAvailable;
import com.example.travel.exception.PaymentFailed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingTest {

    private Trip trip;
    private Customer alice;
    private Agent agent;

    @BeforeEach
    void setUp() {
        trip = new Trip(1,
                new ModeOfTransport(1, "Plane"),
                new TransportProvider(1, "Air France"),
                new Location("Charles de Gaulle", "Paris", "France"),
                new Location("Heathrow", "London", "United Kingdom"),
                LocalDateTime.of(2026, 12, 1, 8, 30),
                LocalDateTime.of(2026, 12, 1, 9, 45),
                120.0, 5, 10, 5);

        alice = new Customer(1, "Alice", 1000.0);
        agent = new Agent(2, "Bob", 1000.0);
    }

    // Your test, now using the shared trip and alice from setUp()
    @Test
    void shouldConfirmBookingAndWithdrawWhenPaying() {
//        AAA - arrange, act, assert
//        Given, when, then

//        GIVEN
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);

//        WHEN
        booking.pay();

//        THEN
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(760.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge120WhenBookingEconomy() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);

        booking.pay();

        assertEquals(120.0, booking.getAmountPaid(), 0.001);
        assertEquals(880.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge300WhenBookingBusiness() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.BUSINESS);

        booking.pay();

        assertEquals(300.0, booking.getAmountPaid(), 0.001);
        assertEquals(700.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge480WhenBookingFirst() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.FIRST);

        booking.pay();

        assertEquals(480.0, booking.getAmountPaid(), 0.001);
        assertEquals(520.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldGiveAgentTenPercentDiscount() {
        Booking booking = new Booking(1, agent, trip, 1, SeatClass.FIRST);

        booking.pay();

        assertEquals(432.0, booking.getAmountPaid(), 0.001);
    }

    @Test
    void shouldReduceAvailableSeatsWhenPaying() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);

        booking.pay();

        assertEquals(3, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenBalanceIsTooLow() {
        Customer poor = new Customer(3, "Pete", 50.0);
        Booking booking = new Booking(1, poor, trip, 1, SeatClass.ECONOMY);

        assertThrows(PaymentFailed.class, () -> booking.pay());

        // nothing should have changed
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertEquals(50.0, poor.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenNotEnoughSeats() {
        // the trip only has 5 Economy seats
        Booking booking = new Booking(1, alice, trip, 6, SeatClass.ECONOMY);

        assertThrows(NoSeatsAvailable.class, () -> booking.pay());

        assertEquals(1000.0, alice.getBalance(), 0.001);
    }
}