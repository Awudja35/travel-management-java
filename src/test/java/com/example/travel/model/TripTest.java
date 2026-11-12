package com.example.travel.model;

import com.example.travel.exception.NoSeatsAvailable;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripTest {

    private final ModeOfTransport plane = new ModeOfTransport(1, "Plane");
    private final TransportProvider airFrance = new TransportProvider(1, "Air France");
    private final Location paris = new Location("Charles de Gaulle", "Paris", "France");
    private final Location london = new Location("Heathrow", "London", "United Kingdom");
    private final LocalDateTime departure = LocalDateTime.of(2026, 12, 1, 8, 30);
    private final LocalDateTime arrival = LocalDateTime.of(2026, 12, 1, 9, 45);

    private Trip createTrip(int economySeats, int businessSeats, int firstSeats) {
        return new Trip(1, plane, airFrance, paris, london,
                departure, arrival, 120.0, economySeats, businessSeats, firstSeats);
    }

    // ---------- Reserving seats ----------

    @Test
    void shouldDecreaseSeatsWhenReserving() {
        Trip trip = createTrip(2, 10, 5);

        trip.reserveSeat(SeatClass.ECONOMY);

        assertEquals(1, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldAllowReservingTheLastSeat() {
        Trip trip = createTrip(1, 10, 5);

        trip.reserveSeat(SeatClass.ECONOMY);

        assertEquals(0, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenNoSeatsLeft() {
        Trip trip = createTrip(1, 10, 5);
        trip.reserveSeat(SeatClass.ECONOMY);

        assertThrows(NoSeatsAvailable.class,
                () -> trip.reserveSeat(SeatClass.ECONOMY));

        // a refused reservation must not push the count below zero
        assertEquals(0, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldOnlyChangeTheClassThatWasReserved() {
        Trip trip = createTrip(5, 10, 5);

        trip.reserveSeat(SeatClass.BUSINESS);

        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(9, trip.getAvailableSeats(SeatClass.BUSINESS));
        assertEquals(5, trip.getAvailableSeats(SeatClass.FIRST));
    }

    @Test
    void shouldThrowWhenTheClassHasNoSeatsAtAll() {
        // like a bus with no First class
        Trip trip = createTrip(40, 0, 0);

        assertThrows(NoSeatsAvailable.class,
                () -> trip.reserveSeat(SeatClass.FIRST));
    }

    // ---------- Releasing seats ----------

    @Test
    void shouldGiveTheSeatBackWhenReleasing() {
        Trip trip = createTrip(5, 10, 5);
        trip.reserveSeat(SeatClass.ECONOMY);

        trip.releaseSeat(SeatClass.ECONOMY);

        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    // ---------- Duration ----------

    @Test
    void shouldCalculateTravelDuration() {
        Trip trip = createTrip(1, 10, 5);

        assertEquals(75, trip.travelDuration().toMinutes());
    }

    @Test
    void shouldCalculateDurationAcrossMidnight() {
        // an edge case: leaves at 23:00 and arrives at 01:30 the next day
        Trip nightTrip = new Trip(1, plane, airFrance, paris, london,
                LocalDateTime.of(2026, 12, 1, 23, 0),
                LocalDateTime.of(2026, 12, 2, 1, 30),
                120.0, 5, 10, 5);

        assertEquals(150, nightTrip.travelDuration().toMinutes());
    }

    // ---------- Creating a trip ----------

    @Test
    void shouldThrowWhenPriceIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, london,
                        departure, arrival, 0.0, 5, 10, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, london,
                        departure, arrival, -50.0, 5, 10, 5));
    }

    @Test
    void shouldThrowWhenArrivalIsBeforeDeparture() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, london,
                        arrival, departure, 120.0, 5, 10, 5));
    }

    @Test
    void shouldThrowWhenArrivalEqualsDeparture() {
        // another edge case: a trip that takes no time is not valid either
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, london,
                        departure, departure, 120.0, 5, 10, 5));
    }

    @Test
    void shouldThrowWhenDepartureAndArrivalLocationsAreTheSame() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, paris,
                        departure, arrival, 120.0, 5, 10, 5));
    }

    @Test
    void shouldThrowWhenSeatCountIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> createTrip(-1, 10, 5));
        assertThrows(IllegalArgumentException.class, () -> createTrip(5, -1, 5));
        assertThrows(IllegalArgumentException.class, () -> createTrip(5, 10, -1));
    }

    @Test
    void shouldAllowAClassWithZeroSeats() {
        Trip trip = createTrip(0, 0, 0);

        assertEquals(0, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenARequiredPartIsMissing() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, null, airFrance, paris, london,
                        departure, arrival, 120.0, 5, 10, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, null, paris, london,
                        departure, arrival, 120.0, 5, 10, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, null, london,
                        departure, arrival, 120.0, 5, 10, 5));
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(1, plane, airFrance, paris, null,
                        departure, arrival, 120.0, 5, 10, 5));
    }

    @Test
    void shouldThrowWhenIdIsNotPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Trip(0, plane, airFrance, paris, london,
                        departure, arrival, 120.0, 5, 10, 5));
    }
}