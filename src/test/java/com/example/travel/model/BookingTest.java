package com.example.travel.model;

import com.example.travel.exception.InvalidUpgrade;
import com.example.travel.exception.NoSeatsAvailable;
import com.example.travel.exception.PaymentFailed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BookingTest {

    private Trip trip;
    private Customer alice;
    private Agent agent;

    private Trip createTrip(int economySeats, int businessSeats, int firstSeats) {
        return new Trip(1,
                new ModeOfTransport(1, "Plane"),
                new TransportProvider(1, "Air France"),
                new Location("Charles de Gaulle", "Paris", "France"),
                new Location("Heathrow", "London", "United Kingdom"),
                LocalDateTime.of(2026, 12, 1, 8, 30),
                LocalDateTime.of(2026, 12, 1, 9, 45),
                120.0, economySeats, businessSeats, firstSeats);
    }

    @BeforeEach
    void setUp() {
        trip = createTrip(5, 10, 5);

        alice = new Customer(1, "Alice", 1000.0);
        agent = new Agent(2, "Bob", 1000.0);
    }

    // ---------- Paying ----------

    @Test
    void shouldConfirmBookingAndWithdrawWhenPaying() {
//        AAA - arrange, act, assert
//        Given, when, then

//        GIVEN
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);

//        WHEN
        booking.pay();

//        THEN
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(760.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge120WhenBookingEconomy() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);

        booking.pay();

        assertEquals(120.0, booking.getAmountPaid(), 0.001);
        assertEquals(880.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge300WhenBookingBusiness() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.BUSINESS);

        booking.pay();

        assertEquals(300.0, booking.getAmountPaid(), 0.001);
        assertEquals(700.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldCharge480WhenBookingFirst() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.FIRST);

        booking.pay();

        assertEquals(480.0, booking.getAmountPaid(), 0.001);
        assertEquals(520.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldGiveAgentTenPercentDiscount() {
        Booking booking = new Booking(1, agent, trip, 1, SeatClass.FIRST);

        booking.pay();

        assertEquals(432.0, booking.getAmountPaid(), 0.001);
    }

    @Test
    void shouldReduceAvailableSeatsWhenPaying() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);

        booking.pay();

        assertEquals(3, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenBalanceIsTooLow() {
        Customer poor = new Customer(3, "Pete", 50.0);
        Booking booking = new Booking(1, poor, trip, 1, SeatClass.ECONOMY);

        assertThrows(PaymentFailed.class, () -> booking.pay());

        // nothing should have changed
        assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
        assertEquals(50.0, poor.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenNotEnoughSeats() {
        // the trip only has 5 Economy seats
        Booking booking = new Booking(1, alice, trip, 6, SeatClass.ECONOMY);

        assertThrows(NoSeatsAvailable.class, () -> booking.pay());

        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    // ---------- Upgrading ----------

    @Test
    void shouldChargeOnlyTheDifferenceWhenUpgrading() {
        // GIVEN: 2 Economy tickets, paid (240)
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);
        booking.pay();

        // WHEN: upgrade to Business (now worth 600, so the difference is 360)
        booking.upgrade(SeatClass.BUSINESS);

        // THEN
        assertEquals(SeatClass.BUSINESS, booking.getSeatClass());
        assertEquals(600.0, booking.getAmountPaid(), 0.001);
        assertEquals(400.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldMoveSeatsFromOldClassToNewClassWhenUpgrading() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);
        booking.pay();

        booking.upgrade(SeatClass.BUSINESS);

        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(8, trip.getAvailableSeats(SeatClass.BUSINESS));
    }

    @Test
    void shouldThrowWhenUpgradingAnUnpaidBooking() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);

        assertThrows(InvalidUpgrade.class,
                () -> booking.upgrade(SeatClass.BUSINESS));

        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenUpgradingToTheSameClass() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.BUSINESS);
        booking.pay();

        assertThrows(InvalidUpgrade.class,
                () -> booking.upgrade(SeatClass.BUSINESS));
    }

    @Test
    void shouldThrowWhenDowngrading() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.BUSINESS);
        booking.pay();

        assertThrows(InvalidUpgrade.class,
                () -> booking.upgrade(SeatClass.ECONOMY));

        assertEquals(SeatClass.BUSINESS, booking.getSeatClass());
    }

    @Test
    void shouldChangeNothingWhenUpgradeIsTooExpensive() {
        // Pete has 300: he can pay 240 for Economy, but not the 360 difference
        Customer pete = new Customer(3, "Pete", 300.0);
        Booking booking = new Booking(1, pete, trip, 2, SeatClass.ECONOMY);
        booking.pay();

        assertThrows(PaymentFailed.class,
                () -> booking.upgrade(SeatClass.BUSINESS));

        assertEquals(SeatClass.ECONOMY, booking.getSeatClass());
        assertEquals(60.0, pete.getBalance(), 0.001);
        assertEquals(3, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(10, trip.getAvailableSeats(SeatClass.BUSINESS));
    }

    @Test
    void shouldThrowWhenNewClassHasNotEnoughSeats() {
        // this trip has only 1 Business seat, but the booking has 2 tickets
        Trip smallTrip = createTrip(5, 1, 5);
        Booking booking = new Booking(1, alice, smallTrip, 2, SeatClass.ECONOMY);
        booking.pay();

        assertThrows(NoSeatsAvailable.class,
                () -> booking.upgrade(SeatClass.BUSINESS));

        assertEquals(SeatClass.ECONOMY, booking.getSeatClass());
        assertEquals(760.0, alice.getBalance(), 0.001);
    }

    // ---------- Cancelling ----------

    @Test
    void shouldRefundAndFreeSeatsWhenCancellingAPaidBooking() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);
        booking.pay();

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(1000.0, alice.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(0.0, booking.getAmountPaid(), 0.001);
    }

    @Test
    void shouldNotChangeBalanceWhenCancellingAnUnpaidBooking() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertEquals(1000.0, alice.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenCancellingTwice() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);
        booking.pay();
        booking.cancel();

        assertThrows(IllegalStateException.class, () -> booking.cancel());

        assertEquals(1000.0, alice.getBalance(), 0.001);
    }

    @Test
    void shouldThrowWhenUpgradingACancelledBooking() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);
        booking.pay();
        booking.cancel();

        assertThrows(InvalidUpgrade.class,
                () -> booking.upgrade(SeatClass.BUSINESS));
    }

    @Test
    void shouldRefundTheUpgradedPriceWhenCancellingAfterAnUpgrade() {
        Booking booking = new Booking(1, alice, trip, 2, SeatClass.ECONOMY);
        booking.pay();
        booking.upgrade(SeatClass.BUSINESS);

        booking.cancel();

        assertEquals(1000.0, alice.getBalance(), 0.001);
        assertEquals(5, trip.getAvailableSeats(SeatClass.ECONOMY));
        assertEquals(10, trip.getAvailableSeats(SeatClass.BUSINESS));
    }
    @Test
    void shouldThrowWhenPayingTwice() {
        Booking booking = new Booking(1, alice, trip, 1, SeatClass.ECONOMY);
        booking.pay();

        assertThrows(IllegalStateException.class, () -> booking.pay());

//        second payment should not take money or seat again
        assertEquals(880.0, alice.getBalance(), 0.001);
        assertEquals(4, trip.getAvailableSeats(SeatClass.ECONOMY));

    }
@Test
    void shouldAllowSpendingTheExactBalance(){
//        an edge case: the balance equals the price so it ends at exactly at 0

    Customer eve = new Customer(4, "Eve", 120.0);
    Booking booking = new Booking(1, eve, trip, 1, SeatClass.ECONOMY);

    booking.pay();

    assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
    assertEquals(0.0, eve.getBalance(), 0.001);
}

@Test
void shouldAllowBookingAllRemainingSeats() {
    // another edge case: taking the very last seats is allowed
    Booking booking = new Booking(1, alice, trip, 5, SeatClass.ECONOMY);

    booking.pay();

    assertEquals(0, trip.getAvailableSeats(SeatClass.ECONOMY));
    assertEquals(400.0, alice.getBalance(), 0.001);
}
@Test
void shouldCalculateTotalPriceBeforePaying() {
    Booking booking = new Booking(1, alice, trip, 2, SeatClass.BUSINESS);

    assertEquals(600.0, booking.getTotalPrice(), 0.001);

    // asking for the price must not pay anything
    assertEquals(BookingStatus.PENDING_PAYMENT, booking.getStatus());
    assertEquals(1000.0, alice.getBalance(), 0.001);
}
@Test
void shouldThrowWhenTicketsIsZero() {
    assertThrows(IllegalArgumentException.class,
            () -> new Booking(1, alice, trip, 0, SeatClass.ECONOMY));
}

    @Test
    void shouldThrowWhenUserIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Booking(1, null, trip, 1, SeatClass.ECONOMY));
    }

    @Test
    void shouldThrowWhenTripIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> new Booking(1, alice, null, 1, SeatClass.ECONOMY));
    }
}