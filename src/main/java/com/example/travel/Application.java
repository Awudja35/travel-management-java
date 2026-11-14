package com.example.travel;

import com.example.travel.exception.DataLoad;
import com.example.travel.model.SeatClass;
import com.example.travel.model.Trip;
import com.example.travel.model.User;
import com.example.travel.service.TripSearchService;
import com.example.travel.utils.DataLoader;
import com.example.travel.utils.TripCsvLoader;
import com.example.travel.utils.UserCsvLoader;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        try {
            // 1. LOAD the data from the files
            DataLoader<User> userLoader = new UserCsvLoader();
            DataLoader<Trip> tripLoader = new TripCsvLoader();
            List<User> users = userLoader.load(Path.of("src/main/resources/users.csv"));
            List<Trip> trips = tripLoader.load(Path.of("src/main/resources/trips.csv"));
            System.out.println("Loaded " + users.size() + " users and " + trips.size() + " trips.");

            TripSearchService searchService = new TripSearchService(trips);
            Scanner scanner = new Scanner(System.in);

            // 2. CHOOSE who is using the application
            User currentUser = chooseUser(scanner, users);
            System.out.println("Welcome " + currentUser.getName()
                    + "! Balance: " + currentUser.getBalance() + " EUR");

            // 3. THE REPL
            while (true) {

                // READ
                System.out.println();
                System.out.println("--- MENU ---");
                System.out.println("1) Search trips");
                System.out.println("0) Exit");
                System.out.print("> ");
                int choice = readNumber(scanner);

                // EVALUATE and PRINT
                if (choice == 1) {
                    searchTrips(scanner, searchService);
                } else if (choice == 0) {
                    System.out.println("Goodbye " + currentUser.getName() + "!");
                    break;
                } else {
                    throw new IllegalArgumentException("Choose 0 or 1");
                }
                // LOOP: Java goes back to the top of the while
            }

        } catch (DataLoad e) {
            System.out.println("Could not load data: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Wrong input: " + e.getMessage() + ". The program stops.");
        }
    }

    private static User chooseUser(Scanner scanner, List<User> users) {
        System.out.println();
        System.out.println("Who are you?");
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ") " + user.getName()
                    + " (balance " + user.getBalance() + " EUR)");
        }
        System.out.print("Choose a number: ");
        int choice = readNumber(scanner);

        if (choice < 1 || choice > users.size()) {
            throw new IllegalArgumentException("There is no user number " + choice);
        }
        return users.get(choice - 1);
    }

    private static void searchTrips(Scanner scanner, TripSearchService searchService) {
        System.out.print("From (city): ");
        String from = scanner.nextLine().trim();
        System.out.print("To (city): ");
        String to = scanner.nextLine().trim();
        System.out.print("Date (yyyy-mm-dd, or press Enter for any date): ");
        LocalDate date = readOptionalDate(scanner);

        List<Trip> found = searchService.search(from, to, date);

        if (found.isEmpty()) {
            System.out.println("No trips found.");
            return;
        }

        System.out.println("Trips found:");
        for (int i = 0; i < found.size(); i++) {
            printTrip(i + 1, found.get(i));
        }
    }

    private static void printTrip(int number, Trip trip) {
        System.out.println("[" + number + "] " + trip.getModeOfTransport().getName()
                + ", " + trip.getProvider().getName() + ": "
                + trip.getDepartureLocation().getCity() + " -> "
                + trip.getArrivalLocation().getCity());
        System.out.println("    Departs " + trip.getDeparture().toString().replace("T", " ")
                + ", takes " + trip.travelDuration().toMinutes() + " min"
                + ", price from " + trip.getPrice() + " EUR");
        System.out.println("    Seats left: Economy " + trip.getAvailableSeats(SeatClass.ECONOMY)
                + " | Business " + trip.getAvailableSeats(SeatClass.BUSINESS)
                + " | First " + trip.getAvailableSeats(SeatClass.FIRST));
    }

    // reads a whole number, or stops the program with a clear message
    private static int readNumber(Scanner scanner) {
        String text = scanner.nextLine().trim();
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + text + "' is not a number");
        }
    }

    // reads a date, or returns null when the user just presses Enter
    private static LocalDate readOptionalDate(Scanner scanner) {
        String text = scanner.nextLine().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Use the format yyyy-mm-dd, for example 2026-12-01");
        }
    }
}