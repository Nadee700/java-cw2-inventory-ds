package com.inventory.system;

import inventory.InventoryServiceGrpc;
import inventory.InventoryServiceGrpc.InventoryServiceBlockingStub;
import inventory.AddItemRequest;
import inventory.DeleteItemRequest;
import inventory.EditItemRequest;
import inventory.ReservationRequest;
import inventory.Response;
import inventory.ListItemsRequest;
import inventory.ListItemsResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Scanner;

public class InventoryClient {
    private final InventoryServiceBlockingStub blockingStub;

    public InventoryClient(InventoryServiceBlockingStub blockingStub) {
        this.blockingStub = blockingStub;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    createNewItem(scanner);
                    break;
                case 2:
                    modifyItem(scanner);
                    break;
                case 3:
                    removeItem(scanner);
                    break;
                case 4:
                    buyItem(scanner);
                    break;
                case 5:
                    showItems();
                    break;
                case 6:
                    showHelp();
                    break;
                case 0:
                    System.out.println("Quitting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please select again.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("\nInventory System Menu");
        System.out.println("1. Create New Item");
        System.out.println("2. Modify Item");
        System.out.println("3. Remove Item");
        System.out.println("4. Purchase Item");
        System.out.println("5. Show Items");
        System.out.println("6. Help");
        System.out.println("0. Quit");
        System.out.print("Enter your choice: ");
    }

    private void showHelp() {
        System.out.println("\nHelp Information");
        System.out.println("1. Create New Item: Add a new item to the inventory.");
        System.out.println("2. Modify Item: Update the details of an existing item.");
        System.out.println("3. Remove Item: Delete an item from the inventory.");
        System.out.println("4. Purchase Item: Reserve a specific quantity of an item.");
        System.out.println("5. Show Items: List all items currently in the inventory.");
        System.out.println("0. Quit: Exit the application.");
    }

    private void createNewItem(Scanner scanner) {
        System.out.print("Item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Item name: ");
        String name = scanner.nextLine();
        System.out.print("Item quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("Item Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        AddItemRequest request = AddItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .setPrice(price)
                .build();
        Response response = blockingStub.addItem(request);
        System.out.println("Create New Item Response: " + response.getMessage());
    }

    private void modifyItem(Scanner scanner) {
        System.out.print("Item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Item name: ");
        String name = scanner.nextLine();
        System.out.print("New quantity: ");
        int quantity = scanner.nextInt();
        System.out.print("New price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        EditItemRequest request = EditItemRequest.newBuilder()
                .setItemId(itemId)
                .setName(name)
                .setQuantity(quantity)
                .setPrice(price)
                .build();
        Response response = blockingStub.editItem(request);
        System.out.println("Modify Item Response: " + response.getMessage());
    }

    private void removeItem(Scanner scanner) {
        System.out.print("Item ID: ");
        String itemId = scanner.nextLine();

        DeleteItemRequest request = DeleteItemRequest.newBuilder()
                .setItemId(itemId)
                .build();
        Response response = blockingStub.deleteItem(request);
        System.out.println("Remove Item Response: " + response.getMessage());
    }

    private void buyItem(Scanner scanner) {
        System.out.print("Reservation ID: ");
        String reservationId = scanner.nextLine();
        System.out.print("Item ID: ");
        String itemId = scanner.nextLine();
        System.out.print("Quantity: ");
        int quantity = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email address: ");
        String emailAddress = scanner.nextLine();

        ReservationRequest request = ReservationRequest.newBuilder()
                .setReservationId(reservationId)
                .setItemId(itemId)
                .setQuantity(quantity)
                .setName(name)
                .setEmailAddress(emailAddress)
                .build();
        Response response = blockingStub.reserveItem(request);
        System.out.println("Reserve Item Response: " + response.getMessage());
    }

    private void showItems() {
        ListItemsRequest request = ListItemsRequest.newBuilder().build();
        ListItemsResponse response = blockingStub.listItems(request);
        for (inventory.ItemInfo itemInfo : response.getItemsList()) {
            System.out.printf("ID: %s, Name: %s, Quantity: %d, Reserved: %d, Price: %.2f%n",
                    itemInfo.getItemId(), itemInfo.getName(), itemInfo.getQuantity(),
                    itemInfo.getReservedQuantity(), itemInfo.getPrice());
        }
    }
}
