package tests;

import model.Product;
import model.Supermarket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import utilities.SupermarketBuilder;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class ProductTest {

    @BeforeEach
    void setup() {
    }

    @Test
    void t011_productsWithIdenticalCodesAreDuplicates() {
        Product p1 = new Product("ABC", "XXX", 1.0);
        Product p2 = new Product("ABC", "YYY", 2.0);
        Product p3 = new Product("DEF", "XXX", 1.0);
        assertEquals(p1, p2,
                "Products with identical code should be equal");
        assertEquals(p1.hashCode(), p2.hashCode(),
                "Products with identical code should have identical hash code");
        assertNotEquals(p1, p3,
                "Products with different code should be different");
    }

    @Test
    void t012_productsCanBeSorted() {
        Product p1 = new Product("ABC", "XXX", 1.0);
        Product p2 = new Product("ABC", "YYY", 2.0);
        Product p3 = new Product("DEF", "XXX", 1.0);
        assertTrue(p1 instanceof Comparable);
        assertThat(p1, is(lessThan((Comparable)p3)));
        assertThat(p3, is(greaterThan((Comparable)p1)));
        assertThat(p1, is(equalTo((Comparable)p2)));
    }

    @Test
    void t013_prductsCanBeLoadedIntoASet() {
        Supermarket supermarket = new SupermarketBuilder("src/main/resources/jambi1Json.txt").initializeSupermarket().addProducts().addCustomers().create();
        assertEquals(5, supermarket.getProducts().size(),
                "A duplicate product code should not have been added to the set");
    }
}