package com.example.travel.model;
import com.example.travel.exception.PaymentFailed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
public class UserTest {
    private Customer alice;

    @BeforeEach
    void setUp() {
        alice = new Customer(1, "Alice", 100.0);
    }
// Discounts rates
    @Test
    void GiveCustomerNoDiscount(){
        assertEquals(0.0, alice.getDiscountRate(), 0.001);
    }

    @Test
    void GiveAgentTenPercentDiscount(){
        Agent bob = new Agent(2, "Bob", 100.0);

        assertEquals(0.10, bob.getDiscountRate(), 0.001);
    }
    @Test
    void shouldReturnDifferentDiscountThroughTheSameUserType(){
List<User> users = new ArrayList<>();
users.add(new Customer(1,"Alice", 100.0));
users.add(new Agent(2,"Bob", 100.0));

assertEquals(0.0, users.get(0).getDiscountRate(), 0.001);
assertEquals(0.10, users.get(1).getDiscountRate(), 0.001);
    }
    // ---------- Withdraw ----------

    @Test
    void shouldReduceBalanceWhenWithdrawing() {
        alice.withdraw(40.0);

        assertEquals(60.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldAllowWithdrawingTheExactBalance() {
        alice.withdraw(100.0);

        assertEquals(0.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenWithdrawingMoreThanTheBalance() {
        assertThrows(PaymentFailed.class, () -> alice.withdraw(100.01));

        // the balance must not change when the payment is refused
        assertEquals(100.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenWithdrawingFromAnEmptyAccount() {
        Customer broke = new Customer(3, "Pete", 0.0);

        assertThrows(PaymentFailed.class, () -> broke.withdraw(1.0));
    }

    @Test
    void shouldThrowWhenWithdrawingZeroOrNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> alice.withdraw(0));
        assertThrows(IllegalArgumentException.class, () -> alice.withdraw(-10.0));

        assertEquals(100.0, alice.getBalance(), 0.001);
    }

    // ---------- Deposit ----------

    @Test
    void shouldIncreaseBalanceWhenDepositing() {
        alice.deposit(50.0);

        assertEquals(150.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenDepositingZeroOrNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> alice.deposit(-10.0));

        assertEquals(100.0, alice.getBalance(), 0.001);
    }

    // ---------- Creating a user ----------

    @Test
    void shouldAllowCreatingAUserWithZeroBalance() {
        Customer broke = new Customer(3, "Pete", 0.0);

        assertEquals(0.0, broke.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenStartingBalanceIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(1, "Alice", -1.0));
    }

    @Test
    void shouldThrowWhenNameIsEmptyOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(1, "", 100.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(1, "   ", 100.0));
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Agent(1, null, 100.0));
    }

    @Test
    void shouldThrowWhenIdIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Customer(0, "Alice", 100.0));
        assertThrows(IllegalArgumentException.class,
                () -> new Agent(-5, "Bob", 100.0));
    }
    @Test
    void shouldKeepCorrectBalanceAfterSeveralOperations(){
        alice.deposit(50.0);
        alice.withdraw(100.0);

//        100+50-100
        assertEquals(50.0, alice.getBalance(), 0.001);
    }
    @Test
    void shouldApplyTheSameBalanceRulesToAgents() {
        Agent bob = new Agent(2, "Bob", 100.0);

        bob.withdraw(100.0);

        assertEquals(0.0, bob.getBalance(), 0.001);
        assertThrows(PaymentFailed.class, () -> bob.withdraw(1.0));
    }
}