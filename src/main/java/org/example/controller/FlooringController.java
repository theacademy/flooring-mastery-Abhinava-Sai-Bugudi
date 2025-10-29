package org.example.controller;

import org.example.dao.FlooringException;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Tax;
import org.example.service.FlooringService;
import org.example.view.FlooringView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Controller
public class FlooringController {

    private final FlooringService service;
    private final FlooringView view;

    @Autowired
    public FlooringController(FlooringService service, FlooringView view) {
        this.service = service;
        this.view = view;
    }

    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            int choice = view.printMenuAndGetSelection();
            try {
                switch (choice) {
                    case 1 -> displayOrders();
                    case 2 -> addOrder();
                    case 3 -> editOrder();
                    case 4 -> removeOrder();
                    case 5 -> exportAllData();
                    case 6 -> {
                        view.displayMessage("Exiting Flooring Program. Goodbye!");
                        keepRunning = false;
                    }
                    default -> view.displayMessage("Unknown command.");
                }
            } catch (FlooringException e) {
                view.displayMessage("ERROR: " + e.getMessage());
            }
        }
    }

    private void displayOrders() throws FlooringException {
        //display orders by date
        LocalDate date = view.promptDate();
        List<Order> orders = service.getOrdersByDate(date);
        if (orders.isEmpty()) {
            view.displayMessage("No orders found for " + date);
        }
        else {
            view.displayOrders(orders);
        }
    }

    // adding a new order
    private void addOrder() {
        try {
            LocalDate date = view.promptFutureDate();
            String name = view.promptCustomerName();

            List<Tax> taxes = service.getAllTaxes();
            String state = view.promptState(taxes);

            List<Product> products = service.getAllProducts();
            String productType = view.promptProduct(products);

            BigDecimal area = view.promptArea();

            // Build new order object
            Order newOrder = new Order();
            newOrder.setCustomerName(name);
            newOrder.setState(state);
            newOrder.setProductType(productType);
            newOrder.setArea(area);

            service.calculateCosts(newOrder);

            // Display a summary
            view.displayMessage("\nOrder Summary:");
            view.displayMessage("-----------------------------------------");
            view.displayMessage("Customer: " + newOrder.getCustomerName());
            view.displayMessage("State: " + newOrder.getState());
            view.displayMessage("Tax Rate: " + newOrder.getTaxRate() + "%");
            view.displayMessage("Product: " + newOrder.getProductType());
            view.displayMessage("Area: " + newOrder.getArea() + " sq ft");
            view.displayMessage("Material Cost: $" + newOrder.getMaterialCost());
            view.displayMessage("Labor Cost: $" + newOrder.getLaborCost());
            view.displayMessage("Tax: $" + newOrder.getTax());
            view.displayMessage("Total: $" + newOrder.getTotal());
            view.displayMessage("-----------------------------------------");

            // Ask user if they want to confirm
            String confirm = view.promptConfirmation("Would you like to place this order? (Y/N): ");

            if (confirm.equalsIgnoreCase("Y")) {
                // The order number will be assigned by DAO (next available)
                Order saved = service.addOrder(date, newOrder);
                view.displayMessage("Order #" + saved.getOrderNumber() + " successfully added!");
            } else {
                view.displayMessage("Order discarded. Returning to main menu.");
            }

        } catch (FlooringException e) {
            view.displayMessage("Error adding order: " + e.getMessage());
        }
    }


    // editing existing order
    private void editOrder() {
        try {
            LocalDate date = view.promptDate(); // allow past dates here
            List<Order> orders = service.getOrdersByDate(date);

            if (orders.isEmpty()) {
                view.displayMessage("No orders found for " + date);
                return;
            }

            // Display all orders for that date
            view.displayMessage("Existing Orders:");
            view.displayOrders(orders);

            int orderNumber = view.promptInt("Enter order number to edit: ");
            Order existingOrder = orders.stream()
                    .filter(o -> o.getOrderNumber() == orderNumber)
                    .findFirst()
                    .orElse(null);

            if (existingOrder == null) {
                view.displayMessage("No order found with that number.");
                return;
            }

            // Show current info
            view.displayMessage("\nEditing Order #" + existingOrder.getOrderNumber());
            view.displayMessage("(Leave blank to keep existing value)");

            // Prompt for optional edits
            String newName = view.promptOptionalCustomerName(existingOrder.getCustomerName());
            if (!newName.isBlank()) existingOrder.setCustomerName(newName);

            List<Tax> taxes = service.getAllTaxes();
            String newState = view.promptOptionalState(taxes, existingOrder.getState());
            if (!newState.isBlank()) existingOrder.setState(newState);

            List<Product> products = service.getAllProducts();
            String newProductType = view.promptOptionalProduct(products, existingOrder.getProductType());
            if (!newProductType.isBlank()) existingOrder.setProductType(newProductType);

            BigDecimal newArea = view.promptOptionalArea(existingOrder.getArea());
            if (newArea != null) existingOrder.setArea(newArea);

            // Recalculate costs after any change
            service.calculateCosts(existingOrder);

            // Show summary before saving
            view.displayMessage("\nUpdated Order Summary:");
            view.displayMessage("-----------------------------------------");
            view.displayMessage("Customer: " + existingOrder.getCustomerName());
            view.displayMessage("State: " + existingOrder.getState());
            view.displayMessage("Tax Rate: " + existingOrder.getTaxRate() + "%");
            view.displayMessage("Product: " + existingOrder.getProductType());
            view.displayMessage("Area: " + existingOrder.getArea() + " sq ft");
            view.displayMessage("Material Cost: $" + existingOrder.getMaterialCost());
            view.displayMessage("Labor Cost: $" + existingOrder.getLaborCost());
            view.displayMessage("Tax: $" + existingOrder.getTax());
            view.displayMessage("Total: $" + existingOrder.getTotal());
            view.displayMessage("-----------------------------------------");

            String confirm = view.promptConfirmation("Would you like to save these changes? (Y/N): ");

            if (confirm.equalsIgnoreCase("Y")) {
                service.editOrder(date, existingOrder);
                view.displayMessage("Order #" + existingOrder.getOrderNumber() + " successfully updated!");
            } else {
                view.displayMessage("Edit discarded. Returning to main menu.");
            }

        } catch (Exception e) {
            view.displayMessage("Error editing order: " + e.getMessage());
        }
    }

    private void removeOrder() throws FlooringException {
        LocalDate date = view.promptDate();
        List<Order> orders = service.getOrdersByDate(date);
        if (orders.isEmpty()) {
            view.displayMessage("No orders found for " + date);
            return;
        }

        view.displayOrders(orders);
        int orderNumber = view.promptInt("Enter order number to remove: ");
        Order removed = service.removeOrder(date, orderNumber);
        if (removed != null)
            view.displayMessage("Order removed:\n" + removed);
        else
            view.displayMessage("No order found with that number.");
    }

    private void exportAllData() {
        view.displayMessage("Not Implemented ");
    }
}
