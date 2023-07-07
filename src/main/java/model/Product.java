package model;

import java.util.Objects;

/**
 * Supermarket Customer check-out
 * @author  Max de Bood
 */

public class Product implements Comparable<Product> {
    private String code;            // a unique product code; identical codes designate identical products
    private String description;     // the product description, useful for reporting
    private double price;           // the product's price

    public Product() {
    }

    public Product(String code, String description, double price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    @Override
    public int compareTo(Product other) {
        return this.getDescription().compareTo(other.getDescription());
    }
    // Overrides of equals() and hashCode() methods for HashSet and HashMap
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(code, product.code);
    }

    @Override
    public int hashCode(){
        return Objects.hash(code);
    }


    public String toString() {
        return description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }


}
