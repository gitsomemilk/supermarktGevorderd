package tests;//import com.sun.org.apache.xerces.internal.util.PropertyState;

import model.Customer;
import model.Product;
import org.junit.jupiter.api.*;

import java.time.LocalTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class CustomerTest {

    private Product prod1 = new Product("A001", "Any-1", 1.0);
    private Product prod2 = new Product("A002", "Any-2", 2.0);
    private Product prod3 = new Product("A003", "Any-3", 3.0);
    private Customer customer0, customer1, customer2, customer3, customer9;

    @BeforeEach
    void setup() {
        this.customer0 = new Customer(LocalTime.NOON, "1000AA");
        this.customer1 = new Customer(LocalTime.NOON.plusSeconds(10), "1000AB");
        this.customer1.addToCart(this.prod1,1);
        this.customer2 = new Customer(LocalTime.NOON.plusSeconds(20), "1000AB");
        this.customer2.addToCart(this.prod2,2);
        this.customer3 = new Customer(LocalTime.NOON, "1100AA");
        this.customer9 = new Customer(LocalTime.NOON.plusSeconds(30), "1000AC");
        this.customer9.addToCart(this.prod1,5);
        this.customer9.addToCart(this.prod2,3);
        this.customer9.addToCart(this.prod3,1);
        // a number of products can be added to the cart
        this.customer9.addToCart(this.prod3,3);
    }

    @Test
    void t014_customersWithIdenticalQueueAtAreDuplicates() {
        assertEquals(customer0, customer3,
                "Customers with identical queuedAt should be equal");
        assertEquals(customer0.hashCode(), customer3.hashCode(),
                "Customers with identical queuedAt should have identical hash code");
        assertNotEquals(customer0, customer1,
                "Customers with different queuedAt should be different");
    }

    @Test
    void t015_customerHoldsMapOfItems() {
        Assertions.assertTrue(this.customer0.getItemsCart() instanceof Map);
    }

    @Test
    void t021_customerCalculatesNumberOfItems() {
        Assertions.assertEquals(0, this.customer0.getNumberOfItems());
        Assertions.assertEquals(1, this.customer1.getNumberOfItems());
        Assertions.assertEquals(2, this.customer2.getNumberOfItems());
        Assertions.assertEquals(12, this.customer9.getNumberOfItems());
    }

    @Test
    void t051_customerCalculatesTotalBill() {
        Assertions.assertEquals(0.0, this.customer0.calculateTotalBill());
        Assertions.assertEquals(1.0, this.customer1.calculateTotalBill());
        Assertions.assertEquals(4.0, this.customer2.calculateTotalBill());
        Assertions.assertEquals(23.0, this.customer9.calculateTotalBill());
    }
}