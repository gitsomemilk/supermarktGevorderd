package model;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
/**
 * Supermarket Customer check-out and Cashier simulation
 *
 * @author Max de Bood
 */
public class Customer implements Comparable<Customer> {
    private LocalTime queuedAt;
    private String zipCode;
    private Map<Product, Integer> itemsCart = new HashMap<>();


    public Customer() {
    }

    public Customer(LocalTime queuedAt, String zipCode) {
        this.queuedAt = queuedAt;
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(queuedAt, customer.queuedAt) && Objects.equals(zipCode, customer.zipCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(queuedAt, zipCode);
    }

    @Override
    public int compareTo(Customer other) {
        return this.queuedAt.compareTo(other.queuedAt);
    }

    /**
     * calculate the total number of items purchased by this customer
     * @return Het totale aantal items in het winkelwagentje.
     */
    public int getNumberOfItems() {
        int numItems = 0;
        for (int quantity : itemsCart.values()) numItems += quantity;
        return numItems;
    }


    public void addToCart(Product product, int number) {
        if (itemsCart.containsKey(product)) {
            int huidigeQuantity = itemsCart.get(product);
            itemsCart.put(product, huidigeQuantity + number);
        } else {
            itemsCart.put(product, number);
        }
    }

    public double calculateTotalBill() {
        return itemsCart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public String toString() {
        StringBuilder result = new StringBuilder("queuedAt: " + queuedAt);
        result.append("\nzipCode: ").append(zipCode);
        result.append("\nPurchases:");
        for (Product product : itemsCart.keySet()) {
            result.append("\n\t").append(product).append(": ").append(itemsCart.get(product));
        }
        result.append("\n");
        return result.toString();
    }


    public LocalTime getQueuedAt() {
        return queuedAt;
    }

    public String getZipCode() {
        return zipCode;
    }

    public Map<Product, Integer> getItemsCart() {
        return itemsCart;
    }


}
