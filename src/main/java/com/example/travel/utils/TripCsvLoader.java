package com.example.travel.utils;

import com.example.travel.exception.DataLoad;
import com.example.travel.model.Location;
import com.example.travel.model.ModeOfTransport;
import com.example.travel.model.TransportProvider;
import com.example.travel.model.Trip;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class TripCsvLoader implements DataLoader<Trip> {

    @Override
    public List<Trip> load(Path file) {
        List<String> lines = CsvFileReader.readLines(file);
        List<Trip> trips = new ArrayList<>();

        // i starts at 1 to skip the header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                trips.add(parseTrip(line, i + 1));
            } catch (IllegalArgumentException | DateTimeParseException e) {
                throw new DataLoad("Invalid trip on line " + (i + 1) + ": " + e.getMessage());
            }
        }

        return trips;
    }

    private Trip parseTrip(String line, int lineNumber) {
        String[] p = CsvFileReader.splitLine(line);
        if (p.length != 17) {
            throw new DataLoad("Line " + lineNumber + " must have 17 values but has " + p.length);
        }

        int id = Integer.parseInt(p[0]);
        ModeOfTransport mode = new ModeOfTransport(Integer.parseInt(p[1]), p[2]);
        TransportProvider provider = new TransportProvider(Integer.parseInt(p[3]), p[4]);
        Location from = new Location(p[5], p[6], p[7]);
        Location to = new Location(p[8], p[9], p[10]);
        LocalDateTime departure = LocalDateTime.parse(p[11]);
        LocalDateTime arrival = LocalDateTime.parse(p[12]);
        double price = Double.parseDouble(p[13]);
        int economySeats = Integer.parseInt(p[14]);
        int businessSeats = Integer.parseInt(p[15]);
        int firstSeats = Integer.parseInt(p[16]);

        return new Trip(id, mode, provider, from, to, departure, arrival,
                price, economySeats, businessSeats, firstSeats);
    }

}