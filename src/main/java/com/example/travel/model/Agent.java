package com.example.travel.model;

public class Agent extends User{
    public Agent(int id, String name, double balance) {
        super(id, name, balance);
    }

    @Override
    public double getDiscountRate() {
        return 0.10;
    }
}
