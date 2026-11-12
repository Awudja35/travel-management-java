package com.example.travel;
import com.example.travel.model.Agent;
import com.example.travel.model.Customer;
import com.example.travel.model.User;
import com.example.travel.utils.CsvFileReader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<String> lines = CsvFileReader.readLines(Path.of("src/main/resources/trips.csv"));
        System.out.println("Lines read: " + lines.size());

        String[] parts = CsvFileReader.splitLine(lines.get(1));
        System.out.println("Values on line 2: " + parts.length);
        System.out.println("From city: " + parts[6]);

        }
    }


