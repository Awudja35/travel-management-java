package com.example.travel.model;

public class Customer extends User{
    public Customer(int id, String name, double balance) {
        super(id, name, balance);
    }

    @Override
    public double  getDiscountRate() {
        return 0.0;
    }
}
