package com.example.travel.utils;
import com.example.travel.exception.DataLoad;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public class CsvFileReader {
    public static List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            throw new DataLoad("Cannot read file " + file + ": " + e.getMessage());
        }
    }

    public static String[] splitLine(String line) {
        String[] parts = line.split(",");
        for (int i = 0; i < parts.length; i++) {
            parts[i] = parts[i].trim();
        }
        return parts;
    }
}
