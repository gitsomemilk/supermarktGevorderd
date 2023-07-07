package utilities;

import com.google.gson.*;
import model.Customer;
import model.Product;
import model.Supermarket;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class SupermarketBuilder {


    private Supermarket supermarket;
    private Scanner scanner;
    private Gson gson;
    private boolean isProduct = false;
    private boolean isCustomer = false;

    public SupermarketBuilder(String filename) {
        try  {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException fileError) {
            System.out.println("File not found.");
        }
        gson = new GsonBuilder().enableComplexMapKeySerialization()
                .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>)
                        (json, typeOfT, context) -> LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
                .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>)
                        (src, typeOfSrc, context) -> new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
                .create();
    }

    public SupermarketBuilder initializeSupermarket() {
        while (scanner.hasNext()) {
            if (scanner.nextLine().equals("Supermarket")) {
                supermarket = gson.fromJson(scanner.nextLine(), Supermarket.class);
                supermarket.initializeCollections();
            }
            if (scanner.nextLine().equals("Products")) {
                isProduct = true;
                break;
            }
        }
        return this;
    }

    public SupermarketBuilder addProducts() {
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (nextLine.equals("Customers")) {
                isProduct = false;
                isCustomer = true;
                break;
            }
            if (isProduct) {
                supermarket.getProducts().add(gson.fromJson(nextLine, Product.class));
            }

        }
        return this;
    }

    public SupermarketBuilder addCustomers() {
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            if (isCustomer) {
                supermarket.getCustomers().add(gson.fromJson(nextLine, Customer.class));
            }
            if (nextLine.equals("Customers")) {
                isCustomer = true;
            }
        }
        return this;
    }

    public Supermarket create() {
        return this.supermarket;
    }

}
