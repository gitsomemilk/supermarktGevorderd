package model;

import java.util.Objects;

/**
 * Supermarket Customer check-out
 *
 * @author Max de Bood
 */

public class Product implements Comparable<Product> {
    private String code;
    private String description;
    private double price;

    public Product() {
    }

    public Product(String code, String description, double price) {
        this.code = code;
        this.description = description;
        this.price = price;
    }

    @Override
    public int compareTo(Product other) {
        int descriptionComparison = this.getDescription().compareTo(other.getDescription());
        if (descriptionComparison != 0) {
            return descriptionComparison;
        }
        return this.getCode().compareTo(other.getCode());
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
    public int hashCode() {
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
