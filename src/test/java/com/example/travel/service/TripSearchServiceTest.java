package com.example.travel.service;

import com.example.travel.model.Location;
import com.example.travel.model.ModeOfTransport;
import com.example.travel.model.TransportProvider;
import com.example.travel.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripSearchServiceTest {

    private TripSearchService service;

    // builds a valid trip in December 2026; only the id, the cities and the day change
    private Trip createTrip(int id, String fromCity, String toCity, int day) {
        return new Trip(id,
                new ModeOfTransport(1, "Plane"),
                new TransportProvider(1, "Air France"),
                new Location(fromCity + " station", fromCity, "France"),
                new Location(toCity + " station", toCity, "France"),
                LocalDateTime.of(2026, 12, day, 8, 30),
                LocalDateTime.of(2026, 12, day, 10, 30),
                100.0, 5, 5, 5);
    }

    @BeforeEach
    void setUp() {
        List<Trip> trips = new ArrayList<>();
        trips.add(createTrip(1, "Paris", "London", 1));
        trips.add(createTrip(2, "Paris", "London", 2));
        trips.add(createTrip(3, "Paris", "Lille", 1));
        trips.add(createTrip(4, "London", "Paris", 5));

        service = new TripSearchService(trips);
    }

    // ---------- Normal cases ----------

    @Test
    void shouldFindTripsByOriginAndDestination() {
        List<Trip> results = service.search("Paris", "London", null);

        assertEquals(2, results.size());
    }

    @Test
    void shouldFilterByDateWhenDateIsGiven() {
        List<Trip> results = service.search("Paris", "London", LocalDate.of(2026, 12, 1));

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getId());
    }

    @Test
    void shouldNotMixUpDirections() {
        List<Trip> results = service.search("London", "Paris", null);

        assertEquals(1, results.size());
        assertEquals(4, results.get(0).getId());
    }

    // ---------- Edge cases ----------

    @Test
    void shouldIgnoreUpperAndLowerCase() {
        List<Trip> results = service.search("paris", "LONDON", null);

        assertEquals(2, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTripMatches() {
        List<Trip> results = service.search("Paris", "Berlin", null);

        assertEquals(0, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoTripOnThatDate() {
        List<Trip> results = service.search("Paris", "London", LocalDate.of(2026, 12, 25));

        assertEquals(0, results.size());
    }

    @Test
    void shouldReturnEmptyListWhenDateMatchesAnotherRoute() {
        // Paris to Lille only runs on December 1
        List<Trip> results = service.search("Paris", "Lille", LocalDate.of(2026, 12, 2));

        assertEquals(0, results.size());
    }

    // ---------- Invalid cases ----------

    @Test
    void shouldThrowWhenOriginIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> service.search("   ", "London", null));
    }

    @Test
    void shouldThrowWhenDestinationIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> service.search("Paris", null, null));
    }

    @Test
    void shouldThrowWhenTripListIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new TripSearchService(null));
    }
}