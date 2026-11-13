package com.example.travel;
import com.example.travel.model.Trip;


import com.example.travel.utils.DataLoader;
import com.example.travel.utils.TripCsvLoader;


import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class Application {
    static void main(String[] args) {
        while(true) {
            System.out.println("Enter your character: ");
            Scanner scanner = new Scanner(System.in);
            String alph = scanner.nextLine();
            if(alph.equals("a")) {
                System.out.println("Your name starts with a");
            }
            else if(alph.equals("b")) {
                System.out.println("Your title is b");

            }
            else if(alph.equals("c")) {
                System.out.println("Your transport is c");

            }
            else if(alph.equals("d")) {
                System.out.println("You're in seat D");

            }
            else if (alph.equals ("q")) {
                System.out.println("wrong input it should be between a and e");
                break;
            }
            }
        }
    }


