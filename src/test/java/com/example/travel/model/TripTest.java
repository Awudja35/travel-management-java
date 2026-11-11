package com.example.travel.model;
import com.example.travel.exception.NoSeatsAvailable;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class TripTest {
    private Trip createTrip(int economySeats) {
        return new Trip(1,
                new ModeOfTransport(1, "Plane"),
                new TransportProvider(1, "Air France"),
                new Location("Charles de Gaulle", "Paris", "France"),
                new Location("Heathrow", "London", "United Kingdom"),
                LocalDateTime.of(2026, 12, 1, 8, 30),
                LocalDateTime.of(2026, 12, 1, 9, 45),
                120.0, economySeats, 10, 5);
    }

    @Test

    void shouldDecreaseSeatsWhenReserving() {
//        GIVEN
        Trip trip = createTrip(2);

//WHEN
        trip.reserveSeat(SeatClass.ECONOMY);


//THEN
        assertEquals(1, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenNoSeatsLeft() {
//        GIVEN
        Trip trip = createTrip(1);

//        WHEN
        trip.reserveSeat(SeatClass.ECONOMY);

// THEN
        assertThrows(NoSeatsAvailable.class,
                () -> trip.reserveSeat(SeatClass.ECONOMY));
    }

    @Test
    void shouldCalculateTravelDuration() {
//        GIVEN
        Trip trip = createTrip(1);



        assertEquals(75, trip.travelDuration().toMinutes());
    }
}
