package com.pluralsight;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Store {

    public static void main(String[] args) {
        // Initialize variables
        ArrayList<Product> inventory = new ArrayList<Product>();
        ArrayList<Product> cart = new ArrayList<Product>();
        double totalAmount = 0.0;

        // Load inventory from CSV file
        loadInventory("products.csv", inventory);

        // Create scanner to read user input
        Scanner scanner = new Scanner(System.in);
        int choice = -1;

        // Display menu and get user choice until they choose to exit
        while (choice != 3) {
            System.out.println("Welcome to the Online Store!");
            System.out.println("1. Show Products");
            System.out.println("2. Show Cart");
            System.out.println("3. Exit");

            choice = scanner.nextInt();
            scanner.nextLine();

            // Call the appropriate method based on user choice
            switch (choice) {
                case 1:
                    displayProducts(inventory, cart, scanner);
                    break;
                case 2:
                    displayCart(cart, scanner, totalAmount);
                    break;
                case 3:
                    System.out.println("Thank you for shopping with us!");
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }

    public static void loadInventory(String fileName, ArrayList<Product> inventory) {

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 3) {
                    String id = parts[0];
                    String name = parts[1];
                    double price = Double.parseDouble(parts[2]);
                    inventory.add(new Product(id, name, price));
                } else {
                    System.err.println("Invalid line in CSV: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
        }
    }

    public static void displayProducts(ArrayList<Product> inventory, ArrayList<Product> cart, Scanner scanner) {
        System.out.println("Available Products:");
        for (Product product : inventory) {
            System.out.println(product.getId() + ": " + product.getName() + " - $" + product.getPrice());
        }

        System.out.println("Enter the ID of the product you want to add to cart (or type 'done' to exit):");
        String productId = scanner.nextLine();
        if (!productId.equals("done")) {
            Product selectedProduct = findProductById(productId, inventory);
            if (selectedProduct != null) {
                cart.add(selectedProduct);
                System.out.println(selectedProduct.getName() + " added to cart.");
            } else {
                System.out.println("Invalid product ID.");
            }
        }

    }

    public static void displayCart(ArrayList<Product> cart, Scanner scanner, double totalAmount) {

        System.out.println("Your Cart:");
        for (Product product : cart) {
            System.out.println(product.getId() + ": " + product.getName() + " - $" + product.getPrice());
            totalAmount += product.getPrice();
        }
        System.out.println("Total amount: $" + totalAmount);
    }



    public static void checkOut(ArrayList<Product> cart, double totalAmount) {
        for (Product product : cart) {
            totalAmount += product.getPrice();
        }

        // Display summary of purchase
        System.out.println("Your Cart:");
        for (Product product : cart) {
            System.out.println(product.getId() + ": " + product.getName() + " - $" + product.getPrice());
        }
        System.out.println("Total amount: $" + totalAmount);

        // Prompt user to confirm purchase
        System.out.println("Confirm purchase? (yes/no)");
        Scanner scanner = new Scanner(System.in);
        String confirmation = scanner.nextLine().toLowerCase();

        // Deduct total cost from account if confirmed
        if (confirmation.equals("yes")) {
            System.out.println("Purchase confirmed. Total amount deducted from your account: $" + totalAmount);
            // Perform deduction from account here
            // Example: deductFromAccount(totalAmount);
            // Reset cart and totalAmount
            cart.clear();
            totalAmount = 0.0;
        } else {
            System.out.println("Purchase canceled.");
        }
    }



    private static void writePurchaseDetailsToFile(ArrayList<Product> cart, double totalAmount) {
        String fileName = "purchase_history.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            // Get current date and time
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTime = now.format(formatter);

            // Write purchase details to the file
            writer.write("Purchase Date: " + dateTime + "\n");
            writer.write("Total Amount: $" + totalAmount + "\n");
            writer.write("Items Purchased:\n");
            for (Product product : cart) {
                writer.write(product.getId() + ": " + product.getName() + " - $" + product.getPrice() + "\n");
            }
            writer.write("--------------------------------------------------\n");

            System.out.println("Purchase details recorded in " + fileName);
        } catch (IOException e) {
            System.err.println("Error writing purchase details to file: " + e.getMessage());
        }
    }
    public static Product findProductById(String id, ArrayList<Product> inventory) {


        for (Product product : inventory) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }
}
