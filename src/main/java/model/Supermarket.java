package model;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Supermarket Customer and purchase statistics
 *
 * @author Max de Bood
 */
public class Supermarket {

    private String name;                 // name of the case for reporting purposes
    private Set<Product> products;      // a set of products that is being sold in the supermarket
    private Set<Customer> customers;   // a set of customers that have visited the supermarket
    private LocalTime openTime;         // start time of the simulation
    private LocalTime closingTime;      // end time of the simulation
    private static final int INTERVAL_IN_MINUTES = 15; // to use for number of customers and revenues per 15 minute intervals

    public Supermarket() {
        initializeCollections();
    }

    public Supermarket(String name, LocalTime openTime, LocalTime closingTime) {
        this.name = name;
        this.setOpenTime(openTime);
        this.setClosingTime(closingTime);
        initializeCollections();
    }

    public void initializeCollections() {
        products = new HashSet<>();
        customers = new HashSet<>();

    }

    public int getTotalNumberOfItems() {
        int totalItems = 0;
        for (Customer customer : customers) {
            totalItems += customer.getNumberOfItems();
        }
        return totalItems;
    }

    private void printErrorMessage() {
        System.out.println("No products or customers have been set up...");
    }

    private boolean checkSetupErrorProductCustomers() {
        return this.customers == null || this.products == null ||
                this.customers.size() == 0 || this.products.size() == 0;
    }

    /**
     * report statistics of data of products
     */
    public void printProductStatistics() {
        if (checkSetupErrorProductCustomers()) {
            printErrorMessage();
            return;
        }
        printTopCustomerStatistics();
        printProductSummary();
        printProductZipCodes();
        printMostPopularProducts();
        printMostBoughtProductsPerZipCode();
    }


    /**
     * report statistics of the input data of customer
     */
    public void printCustomerStatistics() {
        if (checkSetupErrorProductCustomers()) {
            printErrorMessage();
            return;
        }
        System.out.println("\n>>>>> Customer Statistics of all purchases <<<<<");
        System.out.println();
        System.out.printf("Customer that has the highest bill of %.2f euro: \n", findHighestBill());
        System.out.println(findMostPayingCustomer());
        System.out.println();
        System.out.println(">>> Time intervals with number of customers\n");
        Map<LocalTime, Integer> customersPerQuarterhour = countCustomersPerInterval(INTERVAL_IN_MINUTES);
        for (Map.Entry<LocalTime, Integer> entry : customersPerQuarterhour.entrySet()) {
            LocalTime startTime = entry.getKey();
            int count = entry.getValue();
            LocalTime endTime = startTime.plusMinutes(INTERVAL_IN_MINUTES);
            System.out.printf("Between %s and %s the number of customers was %d\n", startTime, endTime, count);
        }

    }

    /**
     * report statistics of the input data of customer
     */
    public void printRevenueStatistics() {
        System.out.println("\n>>>>> Revenue Statistics of all purchases <<<<<");
        // TODO stap 5: calculate and show the total revenue and the average revenue
        System.out.printf("\nTotal revenue = %.2f\nAverage revenue per customer = %.2f\n", findTotalRevenue(), findAverageRevenue());
        System.out.println();
        System.out.print(">>> Revenues per zip-code:\n");
        System.out.println();
        // TODO stap 5 calculate and show total revenues per zipcode, use forEach and lambda expression
        Map<String, Double> revenues = this.getRevenueByZipcode();

        System.out.println();
        System.out.printf(">>> Revenues per interval of %d minutes\n", INTERVAL_IN_MINUTES);
        System.out.println();
        // TODO stap 5: show the revenues per time interval of 15 minutes, use forEach and lambda expression

    }


    /**
     * @return Map with total number of purchases per product
     */
    public Map<Product, Integer> findNumberOfProductsBought() {
        Map<Product, Integer> numberOfProductsBought = new HashMap<>();

        for (Customer customer : customers) {
            Map<Product, Integer> itemsCart = customer.getItemsCart();
            for (Map.Entry<Product, Integer> entry : itemsCart.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();

                numberOfProductsBought.put(product, numberOfProductsBought.getOrDefault(product, 0) + quantity);
            }
        }

        return numberOfProductsBought;

    }

    /**
     * builds a map of products and set of zipcodes where product has been bought
     *
     * @return Map with set of zipcodes per product
     */
    public Map<Product, Set<String>> findZipcodesPerProduct() {
        Map<Product, Set<String>> zipcodesPerProduct = new HashMap<>();

        for (Customer customer : customers) {
            Map<Product, Integer> itemsCart = customer.getItemsCart();
            for (Product product : itemsCart.keySet()) {
                String zipCode = customer.getZipCode();

                if (!zipcodesPerProduct.containsKey(product)) {
                    zipcodesPerProduct.put(product, new HashSet<>());
                }

                zipcodesPerProduct.get(product).add(zipCode);
            }
        }

        return zipcodesPerProduct;
    }

    /**
     * builds a map of zipcodes with maps of products with number bougth
     *
     * @return Map with map of product and number per zipcode
     */
    public Map<String, Map<Product, Integer>> findNumberOfProductsByZipcode() {
        Map<String, Map<Product, Integer>> numberOfProductsByZipcode = new HashMap<>();

        for (Customer customer : customers) {
            String zipCode = customer.getZipCode();
            Map<Product, Integer> itemsCart = customer.getItemsCart();

            if (!numberOfProductsByZipcode.containsKey(zipCode)) {
                numberOfProductsByZipcode.put(zipCode, new HashMap<>());
            }

            for (Map.Entry<Product, Integer> entry : itemsCart.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();

                numberOfProductsByZipcode.get(zipCode).put(product, numberOfProductsByZipcode.get(zipCode).getOrDefault(product, 0) + quantity);
            }
        }

        return numberOfProductsByZipcode;
    }

    /**
     * builds a map of zipcodes with maps of products with number bougth
     *
     * @return Map with map of product and number per zipcode
     */
    public Map<String, Map<Product, Integer>> getNumberOfProductsByZipcode() {
        return findNumberOfProductsByZipcode();
    }

    /**
     * calculates a map with number of customers per time interval that is also ordered by time
     *
     * @return Map with number of customers per time interval
     */
    public Map<LocalTime, Integer> countCustomersPerInterval(int minutes) {
        Map<LocalTime, Integer> customersPerInterval = new TreeMap<>();
        LocalTime startTime = openTime;
        LocalTime endTime = openTime.plusMinutes(minutes);
        while (endTime.isBefore(closingTime) || endTime.equals(closingTime)) {
            LocalTime finalStartTime = startTime;
            LocalTime finalEndTime = endTime;
            int count = (int) customers.stream()
                    .filter(customer -> customer.getQueuedAt().isAfter(finalStartTime) && customer.getQueuedAt().isBefore(finalEndTime))
                    .count();
            customersPerInterval.put(startTime, count);
            startTime = endTime;
            endTime = endTime.plusMinutes(minutes);
        }
        return customersPerInterval;
    }
    /** in de hulp countCustomersPerInterval staat dit :
     * je moet een map maken met als keys de starttijden van de intervallen en als waarde
     het aantal customers in het interval vanaf de betreffende starttijd. Zet met behulp van
     een for-loop eerst alle begintijden van de tijdsintervallen in een map met een waarde
     0. Loop dan alle customers na en bepaal per customer in welk tijdsinterval de
     customer zit en verhoog dan de juiste waarde in de map

     daar heb ik de code hieronder voor gemaakt maar ik zag dat ik dan geen gebruik maak van streams en Lambada expresies en ik wist niet zeker welk jullie wilde zien.
     dus hieronder op basis van de uitleg.
     */
//     public Map<LocalTime, Integer> countCustomersPerInterval(int minutes) {
//         Map<LocalTime, Integer> customersPerInterval = new TreeMap<>();
//
//         // Maak de map met begintijden en initialiseer de waarden met 0
//         LocalTime startTime = openTime;
//         while (startTime.plusMinutes(minutes).isBefore(closingTime) || startTime.plusMinutes(minutes).equals(closingTime)) {
//             customersPerInterval.put(startTime, 0);
//             startTime = startTime.plusMinutes(minutes);
//         }
//
//         // Loop door alle customers en verhoog de waarde in de map voor het juiste tijdsinterval
//         for (Customer customer : customers) {
//             LocalTime queuedAt = customer.getQueuedAt();
//             for (LocalTime intervalStart : customersPerInterval.keySet()) {
//                 LocalTime intervalEnd = intervalStart.plusMinutes(minutes);
//                 if (queuedAt.isAfter(intervalStart) && queuedAt.isBefore(intervalEnd)) {
//                     customersPerInterval.put(intervalStart, customersPerInterval.get(intervalStart) + 1);
//                     break;
//                 }
//             }
//         }
//         return customersPerInterval;
//     }


    /**
     * @return value of the highest bill
     */
    public double findHighestBill() {
        return customers.stream()
                .mapToDouble(Customer::calculateTotalBill)
                .max()
                .orElse(0.0);
    }

    /**
     * @return customer with highest bill
     */
    public Customer findMostPayingCustomer() {
        Optional<Customer> mostPayingCustomer = customers.stream()
                .max(Comparator.comparingDouble(Customer::calculateTotalBill));

        return mostPayingCustomer.orElse(null);
    }


    /**
     * calculates the total revenue of all customers purchases
     *
     * @return total revenue
     */
    public double findTotalRevenue() {
        // TODO Stap 5: use a stream to find the total of all bills
        return customers.stream()
                .flatMap(customer -> customer.getItemsCart().entrySet().stream())
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    /**
     * calculates the average revenue of all customers purchases
     *
     * @return average revenue
     */
    public double findAverageRevenue() {
        // TODO Stap 5: use a stream to find the average of the bills
        return customers.stream()
                .mapToDouble(Customer::calculateTotalBill)
                .average()
                .orElse(0.0);
    }

    /**
     * calculates a map of aggregated revenues per zip code that is also ordered by zip code
     *
     * @return Map with revenues per zip code
     */
    public Map<String, Double> getRevenueByZipcode() {
        // TODO Stap 5: create an appropriate data structure for the revenue
        //  use stream and collector to find the content
        return customers.stream()
                .collect(Collectors.groupingBy(Customer::getZipCode, Collectors.summingDouble(Customer::calculateTotalBill)));
    }


    /**
     * finds the product(s) found in the most carts of customers
     *
     * @return Set with products bought by most customers
     */
    public Set<Product> findMostPopularProducts() {
        // TODO Stap 5: create an appropriate data structure for the most popular products and find its contents

        return null;
    }

    /**
     * calculates a map of most bought products per zip code that is also ordered by zip code
     * if multiple products have the same maximum count, just pick one.
     *
     * @return Map with most bought product per zip code
     */
    public Map<String, Product> findMostBoughtProductByZipcode() {
        // TODO Stap 5: create an appropriate data structure for the mostBought
        //  and calculate its contents

        return null;
    }

    /**
     * calculates a map of revenues per time interval based on the length of the interval in minutes
     *
     * @return Map with revenues per interval
     */
    public Map<LocalTime, Double> calculateRevenuePerInterval(int minutes) {
        // TODO Stap 5: create an appropiate data structure for the revenue per time interval
        //  Start time of an interval is a key. Find the total revenue for each interval

        return null;
    }

    private void printTopCustomerStatistics() {
        System.out.printf("\nCustomer Statistics of '%s' between %s and %s\n",
                this.name, this.openTime, this.closingTime);
    }

    private void printProductSummary() {
        System.out.println("\n>>>>> Product Statistics of all purchases <<<<<");
        System.out.println();
        System.out.printf("%d customers have shopped %d items out of %d different products\n",
                this.customers.size(), this.getTotalNumberOfItems(), this.products.size());
        System.out.println();
        System.out.println(">>> Products and total number bought:");

        // Sorteer de producten op alfabetische volgorde
        List<Product> sortedProducts = new ArrayList<>(products);
        Collections.sort(sortedProducts);
        for (Product product : sortedProducts) {
            int numBought = findNumberOfProductsBought().getOrDefault(product, 0);
            System.out.printf("%-30s \t%d %n", product.getDescription(), numBought);
        }
        System.out.println();
    }

    private void printProductZipCodes() {
        System.out.println(">>> Products and zipcodes");
        Map<Product, Set<String>> zipCodesPerProduct = findZipcodesPerProduct();
        List<Map.Entry<Product, Set<String>>> sortedEntries = new ArrayList<>(zipCodesPerProduct.entrySet());
        Collections.sort(sortedEntries, (entry1, entry2) -> Integer.compare(entry2.getValue().size(), entry1.getValue().size()));
        for (Map.Entry<Product, Set<String>> entry : sortedEntries) {
            Product product = entry.getKey();
            Set<String> zipCodes = entry.getValue();
            String formattedZipCodes = formatZipCodes(zipCodes);
            System.out.printf("%-30s\n \t%s\n", product.getDescription(), formattedZipCodes);
        }
        System.out.println();
    }


    private String formatZipCodes(Set<String> zipCodes) {
        final int ZIP_CODE_SEPARATOR_LENGTH = 2;
        // Verwijderen van de vierkante haken [] uit zipCodes
        String cleanZipCodes = zipCodes.toString().replaceAll("\\[|\\]", "");

        // Nieuwe regel na elke 8 zipcodes
        String[] zipCodeArray = cleanZipCodes.split(", ");
        StringBuilder formattedZipCodes = new StringBuilder();
        for (int i = 0; i < zipCodeArray.length; i++) {
            if (i > 0 && i % 8 == 0) {
                formattedZipCodes.append("\n\t");
            }
            formattedZipCodes.append(zipCodeArray[i]).append(", ");
        }
        // Verwijder de komma en spatie aan het einde van de laatste regel
        if (formattedZipCodes.length() > 0) {
            formattedZipCodes.setLength(formattedZipCodes.length() - ZIP_CODE_SEPARATOR_LENGTH);
        }

        return formattedZipCodes.toString();
    }

    private void printMostPopularProducts() {
        System.out.println(">>> Most popular products");
        System.out.println();
        // TODO stap 5: display the product(s) that most customers bought
        System.out.println("Product(s) bought by most customers: ");
        System.out.println();
    }

    private void printMostBoughtProductsPerZipCode() {
        System.out.println(">>> Most bought products per zipcode");
        System.out.println();
        // TODO stap 5: display most bought products per zipcode
        System.out.println();
    }


    public Set<Product> getProducts() {
        return products;
    }

    public Set<Customer> getCustomers() {
        return customers;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }


}
