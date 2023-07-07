package model; /**
 * Supermarket Customer check-out and Cashier simulation
 * @author  hbo-ict@hva.nl
 */


import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Customer implements Comparable<Customer> {
    private LocalTime queuedAt;      // time of arrival at cashier
    private String zipCode;          // zip-code of the customer
    private Map<Product, Integer> itemsCart = new HashMap<>();;     // items purchased by customer
    private int actualWaitingTime;   // actual waiting time in seconds before check-out
    private int actualCheckOutTime;  // actual check-out time at cashier in seconds

    public Customer() {
    }

    public Customer(LocalTime queuedAt, String zipCode) {
        this.queuedAt = queuedAt;
        this.zipCode = zipCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o ) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer =(Customer) o;
        return Objects.equals(queuedAt,customer.queuedAt) && Objects.equals(zipCode, customer.zipCode);
    }

    @Override
    public int hashCode(){
        return Objects.hash(queuedAt, zipCode);
    }

    // Override compareTo() method voor de TreeSet and TreeMap
    @Override
    public int compareTo(Customer other){
        return  this.queuedAt.compareTo(other.queuedAt);
    }

    /**
     * calculate the total number of items purchased by this customer
     * @return
     */
    public int getNumberOfItems() {
        int numItems = 0;
        for (int quantity:itemsCart.values()) numItems += quantity;
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
        result.append("\nzipCode: " + zipCode);
        result.append("\nPurchases:" );
        for (Product product : itemsCart.keySet()) {
            result.append("\n\t" + product + ": " + itemsCart.get(product));
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
