package model;

import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Supermarket Customer and purchase statistics
 *
 * @author Max de Bood
 */
public class Supermarket {

    private String name;
    private Set<Product> products;
    private Set<Customer> customers;
    private LocalTime openTime;
    private LocalTime closingTime;
    private static final int INTERVAL_IN_MINUTES = 15;

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
     * heb ik opgesplitst in kleiner methodes
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
     *ik heb deze methode ook uit elkaar gehaald omdat hij anders te lang werd
     */
    public void printCustomerStatistics() {
        if (checkSetupErrorProductCustomers()) {
            printErrorMessage();
            return;
        }
        System.out.println("\n>>>>> Customer Statistics of all purchases <<<<<\n");
        System.out.printf("Customer with the highest bill of %.2f euro:\n%s\n\n", findHighestBill(), findMostPayingCustomer());
        printCustomersPerInterval(INTERVAL_IN_MINUTES);
    }


    private void printCustomersPerInterval(int intervalInMinutes) {
        System.out.println(">>> Time intervals with number of customers\n");
        countCustomersPerInterval(intervalInMinutes).forEach((startTime, count) -> {
            LocalTime endTime = startTime.plusMinutes(intervalInMinutes);
            System.out.printf("Between %s and %s, the number of customers was %d\n", startTime, endTime, count);
        });
    }
    /**
     * report statistics of the input data of customer
     * deze heb ik ook uit elkaar gehaald en opgesplitst in kleinere methodes
     */
    public void printRevenueStatistics() {
        System.out.println("\n>>>>> Revenue Statistics of all purchases <<<<<");
        System.out.printf("\nTotal revenue = %.2f\nAverage revenue per customer = %.2f\n", findTotalRevenue(), findAverageRevenue());
        System.out.println();
        printRevenuesByZipcode();
        System.out.println();
        printRevenuesByTimeInterval();
    }

    private void printRevenuesByZipcode() {
        System.out.print(">>> Revenues per zip-code:\n");
        Map<String, Double> revenues = this.getRevenueByZipcode();
        revenues.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("%s: \t%.2f\n", entry.getKey(), entry.getValue()));

    }

    private void printRevenuesByTimeInterval() {
        System.out.printf(">>> Revenues per interval of %d minutes\n", INTERVAL_IN_MINUTES);
        Map<LocalTime, Double> revenuesPerInterval = calculateRevenuePerInterval(INTERVAL_IN_MINUTES);

        for (Map.Entry<LocalTime, Double> entry : revenuesPerInterval.entrySet()) {
            LocalTime startTime = entry.getKey();
            double revenue = entry.getValue();
            LocalTime endTime = startTime.plusMinutes(INTERVAL_IN_MINUTES);

            System.out.printf("Between %s and %s the revenue was %.2f\n", startTime, endTime, revenue);
        }
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
        String cleanZipCodes = zipCodes.toString().replaceAll("\\[|\\]", "");
        String[] zipCodeArray = cleanZipCodes.split(", ");
        StringBuilder formattedZipCodes = new StringBuilder();
        for (int i = 0; i < zipCodeArray.length; i++) {
            if (i > 0 && i % 8 == 0) {
                formattedZipCodes.append("\n\t");
            }
            formattedZipCodes.append(zipCodeArray[i]).append(", ");
        }
        if (formattedZipCodes.length() > 0) {
            formattedZipCodes.setLength(formattedZipCodes.length() - ZIP_CODE_SEPARATOR_LENGTH);
        }
        return formattedZipCodes.toString();
    }

    private void printMostPopularProducts() {
        System.out.println(">>> Most popular products");
        System.out.println();
        Set<Product> mostPopularProducts = findMostPopularProducts();
        System.out.println("Product(s) bought by most customers: ");
        for (Product product : mostPopularProducts) {
            System.out.printf("\t %s", product.getDescription());
        }
        System.out.println();
    }

    private void printMostBoughtProductsPerZipCode() {
        System.out.println(">>> Most bought products per zipcode");
        System.out.println();
        Map<String, Map<Product, Integer>> productsByZipcode = findNumberOfProductsByZipcode();
        List<String> sortedZipcodes = new ArrayList<>(productsByZipcode.keySet());
        Collections.sort(sortedZipcodes);
        for (String zipcode : sortedZipcodes) {
            Map<Product, Integer> productCount = productsByZipcode.get(zipcode);
            int maxCount = productCount.values().stream()
                    .max(Integer::compare)
                    .orElse(0);
            Set<Product> mostBoughtProducts = productCount.entrySet().stream()
                    .filter(e -> e.getValue() == maxCount)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());
            for (Product product : mostBoughtProducts) {
                System.out.printf("%s \t %s%n", zipcode, product.getDescription());
            }
        }
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
        return customers.stream()
                .collect(Collectors.groupingBy(Customer::getZipCode, Collectors.summingDouble(Customer::calculateTotalBill)))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }


    /**
     * finds the product(s) found in the most carts of customers
     *
     * @return Set with products bought by most customers
     */
    public Set<Product> findMostPopularProducts() {
        Map<Product, Long> productCount = customers.stream()
                .flatMap(customer -> customer.getItemsCart().keySet().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        long maxCount = productCount.values().stream().max(Long::compare).orElse(0L);
        Set<Product> mostPopularProducts = productCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        return mostPopularProducts;
    }

    /**
     * calculates a map of most bought products per zip code that is also ordered by zip code
     * if multiple products have the same maximum count, just pick one.
     *
     * @return Map with most bought product per zip code
     */
    public Map<String, Product> findMostBoughtProductByZipcode() {
        Map<String, Map<Product, Integer>> productsByZipcode = findNumberOfProductsByZipcode();
        Map<String, Product> mostBoughtProductByZipcode = new HashMap<>();
        for (Map.Entry<String, Map<Product, Integer>> entry : productsByZipcode.entrySet()) {
            String zipcode = entry.getKey();
            Map<Product, Integer> productCount = entry.getValue();
            int maxCount = productCount.values().stream()
                    .max(Integer::compare)
                    .orElse(0);
            for (Map.Entry<Product, Integer> productEntry : productCount.entrySet()) {
                if (productEntry.getValue() == maxCount) {
                    mostBoughtProductByZipcode.put(zipcode, productEntry.getKey());
                    break;
                }
            }
        }
        return mostBoughtProductByZipcode;
    }

    /**
     * calculates a map of revenues per time interval based on the length of the interval in minutes
     *
     * @return Map with revenues per interval
     */
    public Map<LocalTime, Double> calculateRevenuePerInterval(int minutes) {
        Map<LocalTime, Double> revenuePerInterval = new TreeMap<>();
        LocalTime startTime = openTime;
        LocalTime endTime = openTime.plusMinutes(minutes);

        while (endTime.isBefore(closingTime) || endTime.equals(closingTime)) {
            LocalTime finalStartTime = startTime;
            LocalTime finalEndTime = endTime;
            double totalRevenue = customers.stream()
                    .filter(customer -> customer.getQueuedAt().isAfter(finalStartTime) && customer.getQueuedAt().isBefore(finalEndTime))
                    .flatMap(customer -> customer.getItemsCart().entrySet().stream())
                    .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                    .sum();

            revenuePerInterval.put(startTime, totalRevenue);

            startTime = endTime;
            endTime = endTime.plusMinutes(minutes);
        }

        return revenuePerInterval;
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
