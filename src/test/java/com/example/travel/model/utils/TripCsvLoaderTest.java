package com.example.travel.model.utils;

import com.example.travel.exception.DataLoad;
import com.example.travel.model.SeatClass;
import com.example.travel.model.Trip;
import com.example.travel.utils.TripCsvLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripCsvLoaderTest {

    private static final String HEADER =
            "id,modeId,modeName,providerId,providerName,fromName,fromCity,fromCountry,"
                    + "toName,toCity,toCountry,departure,arrival,price,"
                    + "economySeats,businessSeats,firstSeats\n";

    private static final String PLANE_ROW =
            "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                    + "Heathrow,London,United Kingdom,"
                    + "2026-12-01T08:30,2026-12-01T09:45,120.0,5,10,5\n";

    private static final String TRAIN_ROW =
            "2,2,Train,2,SNCF,Gare du Nord,Paris,France,"
                    + "Gare Lille Europe,Lille,France,"
                    + "2026-12-02T09:00,2026-12-02T10:00,35.0,100,30,10\n";

    private static final String BUS_ROW =
            "3,3,Bus,3,FlixBus,Bercy Seine,Paris,France,"
                    + "Gare Routiere,Brussels,Belgium,"
                    + "2026-12-03T07:15,2026-12-03T11:30,18.0,40,0,0\n";

    @TempDir
    Path tempDir;

    private final TripCsvLoader loader = new TripCsvLoader();

    private Path writeFile(String content) throws IOException {
        Path file = tempDir.resolve("trips.csv");
        Files.writeString(file, content);
        return file;
    }

    // ---------- Normal cases ----------

    @Test
    void shouldLoadATripFromFile() throws IOException {
        Path file = writeFile(HEADER + PLANE_ROW);

        List<Trip> trips = loader.load(file);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(1, trip.getId());
        assertEquals("Plane", trip.getModeOfTransport().getName());
        assertEquals("Air France", trip.getProvider().getName());
        assertEquals("Paris", trip.getDepartureLocation().getCity());
        assertEquals("London", trip.getArrivalLocation().getCity());
        assertEquals(120.0, trip.getPrice(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(75, trip.travelDuration().toMinutes());
    }

    @Test
    void shouldLoadSeveralTrips() throws IOException {
        Path file = writeFile(HEADER + PLANE_ROW + TRAIN_ROW);

        List<Trip> trips = loader.load(file);

        assertEquals(2, trips.size());
        assertEquals("Train", trips.get(1).getModeOfTransport().getName());
    }

    // ---------- Edge cases ----------

    @Test
    void shouldLoadATripWithZeroSeatsInAClass() throws IOException {
        // a bus with no Business and no First class
        Path file = writeFile(HEADER + BUS_ROW);

        List<Trip> trips = loader.load(file);

        assertEquals(40, trips.get(0).getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(0, trips.get(0).getAvailableSeats(SeatClass.BUSINESS));
        assertEquals(0, trips.get(0).getAvailableSeats(SeatClass.FIRST));
    }

    @Test
    void shouldReturnEmptyListWhenFileHasOnlyHeader() throws IOException {
        Path file = writeFile(HEADER);

        List<Trip> trips = loader.load(file);

        assertEquals(0, trips.size());
    }

    // ---------- Invalid cases ----------

    @Test
    void shouldThrowWhenFileDoesNotExist() {
        Path missing = tempDir.resolve("does-not-exist.csv");

        assertThrows(DataLoad.class, () -> loader.load(missing));
    }

    @Test
    void shouldThrowWhenDateFormatIsWrong() throws IOException {
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "01/12/2026,2026-12-01T09:45,120.0,5,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenPriceIsNotANumber() throws IOException {
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T08:30,2026-12-01T09:45,abc,5,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenArrivalIsBeforeDeparture() throws IOException {
        // the trip rules protect the data file too
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T09:45,2026-12-01T08:30,120.0,5,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenDepartureAndArrivalLocationsAreTheSame() throws IOException {
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Heathrow,London,United Kingdom,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T08:30,2026-12-01T09:45,120.0,5,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenALineHasTooFewValues() throws IOException {
        // the last value (firstSeats) is missing, so the line has 16 values instead of 17
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T08:30,2026-12-01T09:45,120.0,5,10\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }
    @Test
    void shouldThrowWhenSeatCountIsNegative() throws IOException {
        // economySeats is -1
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T08:30,2026-12-01T09:45,120.0,-1,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }

    @Test
    void shouldThrowWhenPriceIsZero() throws IOException {
        Path file = writeFile(HEADER
                + "1,1,Plane,1,Air France,Charles de Gaulle,Paris,France,"
                + "Heathrow,London,United Kingdom,"
                + "2026-12-01T08:30,2026-12-01T09:45,0,5,10,5\n");

        assertThrows(DataLoad.class, () -> loader.load(file));
    }
}