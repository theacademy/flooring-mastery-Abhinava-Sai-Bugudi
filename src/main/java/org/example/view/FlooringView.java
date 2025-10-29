package org.example.view;

import org.example.dao.FlooringException;
import org.example.model.Order;
import org.example.model.Product;
import org.example.model.Tax;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class FlooringView {

    private final Scanner scanner = new Scanner(System.in);

    //main menu
    public int printMenuAndGetSelection() {
        while (true) {
            try {
                System.out.println("\n* * * * * * * * * * * * * * * *");
                System.out.println("1. Display Orders");
                System.out.println("2. Add an Order");
                System.out.println("3. Edit an Order");
                System.out.println("4. Remove an Order");
                System.out.println("5. Export All Data");
                System.out.println("6. Quit");
                System.out.println("* * * * * * * * * * * * * * * *");
                System.out.print("Choose an option: ");
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= 6) return choice;
                System.out.println("Please enter a number between 1 and 6.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    //displaying orders and error messages
    public void displayMessage(String msg) {
        System.out.println(msg);
    }

    public void displayOrders(List<Order> orders) {
        for (Order o : orders) System.out.println(o);
    }

    //Implementing all the UX validations


    //Getting date for an existing order
    public LocalDate promptDate() {
        while (true) {
            try {
                System.out.print("Enter date (YYYY-MM-DD): ");
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("Invalid format. Try again.");
            }
        }
    }

    //providing date for a new order
    public LocalDate promptFutureDate() {
        while (true) {
            try {
                System.out.print("Enter order date (YYYY-MM-DD): ");
                LocalDate date = LocalDate.parse(scanner.nextLine().trim());
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Date must be in the future.");
                } else return date;
            } catch (Exception e) {
                System.out.println("Invalid format. Try again.");
            }
        }
    }

    private static final Set<Character> ALLOWED_CHARS = Set.of(
            ' ', '.', ',', '-', '\'',
            'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
            'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
            '0','1','2','3','4','5','6','7','8','9'
    );//accepted set of characters

    //making sure name follows compliance
    public String promptCustomerName() {
        while (true) {
            System.out.print("Enter customer name: ");
            String name = scanner.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name cannot be blank.");
                continue;
            }

            // allow only A-Z, a-z, 0-9, space, comma, period
            for (char c : name.trim().toCharArray()) {
                if (!ALLOWED_CHARS.contains(c))
                    throw new FlooringException("Invalid character '" + c + "' in name.");
            }

            return name;
        }
    }

    //editing name follows compliance
    public String promptOptionalCustomerName(String current) {
        System.out.print("Enter new name (" + current + "): ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) return current;

        for (char c : name.trim().toCharArray()) {
            if (!ALLOWED_CHARS.contains(c))
                throw new FlooringException("Invalid character '" + c + "' in name.");
        }
        return name;
    }

    //state follows compliance
    public String promptState(List<Tax> taxes) {
        while (true) {
            System.out.println("Available states:");
            taxes.forEach(t -> System.out.println(" - " + t.getStateAbbreviation() + " (" + t.getStateName() + ")"));
            System.out.print("Enter state abbreviation: ");
            String input = scanner.nextLine().trim().toUpperCase();

            boolean valid = taxes.stream()
                    .anyMatch(t -> t.getStateAbbreviation().equalsIgnoreCase(input));

            if (!valid) {
                System.out.println("Invalid state abbreviation. Please choose from the list.");
            } else {
                return input;
            }
        }
    }

    //ediitng state follows compliance
    public String promptOptionalState(List<Tax> taxes, String current) {
        System.out.println("Available states:");
        taxes.forEach(t -> System.out.println(" - " + t.getStateAbbreviation()));
        System.out.print("Enter new state (" + current + "): ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return current;

        boolean valid = taxes.stream()
                .anyMatch(t -> t.getStateAbbreviation().equalsIgnoreCase(input));

        if (!valid) {
            System.out.println("Invalid state abbreviation. Keeping old value.");
            return current;
        }

        return input.toUpperCase();
    }

    //product follows compliance
    public String promptProduct(List<Product> products) {
        while (true) {
            System.out.println("Available products:");
            products.forEach(p -> System.out.println(" - " + p.getProductType()));
            System.out.print("Enter product type: ");
            String input = scanner.nextLine().trim();

            boolean valid = products.stream()
                    .anyMatch(p -> p.getProductType().equalsIgnoreCase(input));

            if (!valid) {
                System.out.println("Invalid product type. Please select from the list.");
            } else {
                return input;
            }
        }
    }

    //editing product follows compliance
    public String promptOptionalProduct(List<Product> products, String current) {
        System.out.println("Available products:");
        products.forEach(p -> System.out.println(" - " + p.getProductType()));
        System.out.print("Enter new product (" + current + "): ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return current;

        boolean valid = products.stream()
                .anyMatch(p -> p.getProductType().equalsIgnoreCase(input));

        if (!valid) {
            System.out.println("Invalid product type. Keeping old value.");
            return current;
        }

        return input;
    }

    //area follows compliance
    public BigDecimal promptArea() {
        while (true) {
            try {
                System.out.print("Enter area in sq ft (min 100): ");
                BigDecimal area = new BigDecimal(scanner.nextLine().trim());
                if (area.compareTo(new BigDecimal("100")) < 0)
                    System.out.println("Must be at least 100 sq ft.");
                else return area;
            } catch (Exception e) {
                System.out.println("Invalid input. Try again.");
            }
        }
    }

    //editing area is greater than 100 and is a big decimal
    public BigDecimal promptOptionalArea(BigDecimal current) {
        System.out.print("Enter new area (" + current + "): ");
        String input = scanner.nextLine().trim();
        if (input.isBlank()) return current;

        try {
            BigDecimal area = new BigDecimal(input);
            if (area.compareTo(new BigDecimal("100")) < 0) {
                System.out.println("Must be at least 100 sq ft. Keeping old value.");
                return current;
            }
            return area;
        } catch (Exception e) {
            System.out.println("Invalid input. Keeping old value.");
            return current;
        }
    }

    //menu option check
    public int promptInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    //confirmation check for editing and adding order after displaying summary
    public String promptConfirmation(String message) {
        while (true) {
            System.out.print(message);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y") || input.equals("N")) {
                return input;
            }
            System.out.println("Please enter Y or N.");
        }
    }

}
