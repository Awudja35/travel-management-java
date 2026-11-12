package com.example.travel.model;

import com.example.travel.exception.PaymentFailed;

public abstract class User {
    private int id;
    private String name;
    private double balance;

    public User(int id, String name, double balance){
        if(id <= 0){
            throw new IllegalArgumentException("User id cannot be null or blank");
        }
        if(name == null || name.isBlank()){
            throw new IllegalArgumentException("User name cannot be null or blank");
        }
        if(balance < 0){
            throw new IllegalArgumentException("User balance cannot be zero or negative");
        }

        this.id = id;
        this.name = name;
        this.balance = balance;
    }
    public abstract double getDiscountRate();
    public void deposit(double amount){
        if(amount <= 0){
            throw new IllegalArgumentException("Deposit must be positive");
        }
      balance += amount;
    }
    public void withdraw(double amount){
       if(amount <= 0){
           throw new IllegalArgumentException("Withdrawal amount must be positive");
       }

       if(balance < amount){
           throw new PaymentFailed(name + "does not have enough balance: " + balance + ", needed " + amount);
       }
    balance -= amount;
    }
    public int getId() {return id;}
    public String getName() {return name;}
    public double getBalance() {return balance;}
}
