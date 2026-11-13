package com.example.travel.service;

import com.example.travel.exception.NoSeatsAvailable;
import com.example.travel.exception.PaymentFailed;
import com.example.travel.model.Booking;
import com.example.travel.model.BookingStatus;
import com.example.travel.model.Customer;
import com.example.travel.model.Location;
import com.example.travel.model.ModeOfTransport;
import com.example.travel.model.SeatClass;
import com.example.travel.model.TransportProvider;
import com.example.travel.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingServiceTest {

    private BookingService service;
    private Trip trip;
    private Customer alice;
    private Customer bob;

    @BeforeEach
    void setUp() {
        service = new BookingService();

        trip = new Trip(1,
                new ModeOfTransport(1, "Plane"),
                new TransportProvider(1, "Air France"),
                new Location("Charles de Gaulle", "Paris", "France"),
                new Location("Heathrow", "London", "United Kingdom"),
                LocalDateTime.of(2026, 12, 1, 8, 30),
                LocalDateTime.of(2026, 12, 1, 9, 45),
                120.0, 5, 10, 5);

        alice = new Customer(1, "Alice", 1000.0);
        bob = new Customer(2, "Bob", 1000.0);
    }

    @Test
    void shouldCreateUnpaidBookingsWithIncreasingNumbers() {
        Booking first = service.book(alice, trip, 1, SeatClass.ECONOMY);
        Booking second = service.book(alice, trip, 1, SeatClass.ECONOMY);

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertEquals(BookingStatus.PENDING_PAYMENT, first.getStatus());
    }

    @Test
    void shouldConfirmBookingWhenBookingAndPaying() {
        Booking booking = service.bookAndPay(alice, trip, 2, SeatClass.ECONOMY);

        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(760.0, alice.getBalance(), 0.001);
        assertEquals(1, service.getBookingsOf(alice).size());
    }

    @Test
    void shouldNotKeepBookingWhenPaymentFails() {
        Customer pete = new Customer(3, "Pete", 50.0);

        assertThrows(PaymentFailed.class,
                () -> service.bookAndPay(pete, trip, 1, SeatClass.ECONOMY));

        assertEquals(0, service.getBookingsOf(pete).size());
        assertEquals(50.0, pete.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldNotKeepBookingWhenNotEnoughSeats() {
        assertThrows(NoSeatsAvailable.class,
                () -> service.bookAndPay(alice, trip, 6, SeatClass.ECONOMY));

        assertEquals(0, service.getBookingsOf(alice).size());
        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldUpgradeABooking() {
        Booking booking = service.bookAndPay(alice, trip, 2, SeatClass.ECONOMY);

        service.upgrade(alice, booking.getId(), SeatClass.BUSINESS);

        assertEquals(SeatClass.BUSINESS, booking.getSeatClass());
        assertEquals(400.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCancelABookingAndRefund() {
        Booking booking = service.bookAndPay(alice, trip, 2, SeatClass.ECONOMY);

        service.cancel(alice, booking.getId());

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldListOnlyTheBookingsOfTheGivenUser() {
        service.bookAndPay(alice, trip, 1, SeatClass.ECONOMY);
        service.bookAndPay(bob, trip, 1, SeatClass.ECONOMY);
        service.bookAndPay(alice, trip, 1, SeatClass.BUSINESS);

        assertEquals(2, service.getBookingsOf(alice).size());
        assertEquals(1, service.getBookingsOf(bob).size());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoBookings() {
        assertEquals(0, service.getBookingsOf(alice).size());
    }

    @Test
    void shouldThrowWhenBookingDoesNotExist() {
        assertThrows(IllegalArgumentException.class,
                () -> service.upgrade(alice, 99, SeatClass.BUSINESS));
    }

    @Test
    void shouldNotAllowActingOnAnotherUsersBooking() {
        Booking aliceBooking = service.bookAndPay(alice, trip, 1, SeatClass.ECONOMY);

        assertThrows(IllegalArgumentException.class,
                () -> service.cancel(bob, aliceBooking.getId()));

        // Alice's booking must be untouched
        assertEquals(BookingStatus.CONFIRMED, aliceBooking.getStatus());
    }
}