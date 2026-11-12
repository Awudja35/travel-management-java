package com.example.travel.utils;

import com.example.travel.exception.DataLoad;
import com.example.travel.model.Agent;
import com.example.travel.model.Customer;
import com.example.travel.model.User;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class UserCsvLoader implements DataLoader<User> {

    @Override
    public List<User> load(Path file) {
        List<String> lines = CsvFileReader.readLines(file);
        List<User> users = new ArrayList<>();

        // i starts at 1 to skip the header line
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }

            try {
                users.add(parseUser(line, i + 1));
            } catch (IllegalArgumentException e) {
                throw new DataLoad("Invalid user on line " + (i + 1) + ": " + e.getMessage());
            }
        }

        return users;
    }

    private User parseUser(String line, int lineNumber) {
        String[] parts = CsvFileReader.splitLine(line);
        if (parts.length != 4) {
            throw new DataLoad("Line " + lineNumber + " must have 4 values but has " + parts.length);
        }

        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        double balance = Double.parseDouble(parts[3]);

        if (type.equals("CUSTOMER")) {
            return new Customer(id, name, balance);
        } else if (type.equals("AGENT")) {
            return new Agent(id, name, balance);
        } else {
            throw new DataLoad("Unknown user type '" + type + "' on line " + lineNumber);
        }
    }
}