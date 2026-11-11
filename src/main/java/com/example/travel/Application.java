package com.example.travel;
import com.example.travel.model.Agent;
import com.example.travel.model.Customer;
import com.example.travel.model.User;

import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        List<User> users = new ArrayList<>();
        users.add(new Customer(1,"Ken", 23.2));
        users.add(new Agent(2,"Ken", 600.2));

        for (User user:users){
            System.out.println(user.getName() + "discount" + user.getDiscountRate());
        }
    }

}
