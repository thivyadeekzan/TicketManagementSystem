import core.TicketPool;
import threads.Vendor;
//import threads.Customer;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Welcome to the Real-Time Ticketing System");
        System.out.println("Please configure the system:");

        // Configuration inputs
        // int totalTickets = promptForValidInput(scanner, "Enter the total tickets
        // available: ");
        int ticketReleaseRate = promptForValidInput(scanner, "Enter the ticket release rate: ");
        int customerRetrievalRate = promptForValidInput(scanner, "Enter the customer retrieval rate: ");
        int maxTicketCapacity = promptForValidInput(scanner, "Enter the maximum ticket capacity: ");

        // Create a TicketPool instance
        TicketPool ticketPool = new TicketPool(maxTicketCapacity);

        // Initialize threads for vendors and customers
        Thread vendorThread = new Thread(new Vendor(ticketPool, ticketReleaseRate));
        Thread customerThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ticketPool.removeTicket();
                    Thread.sleep(1000 / customerRetrievalRate);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Customer thread interrupted.");
                }
            }
        });

        // Start threads
        System.out.println("Starting the system...");
        vendorThread.start();
        customerThread.start();

        // CLI controls
        while (true) {
            System.out.println("\nCommands: [status, stop]");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "status":
                    System.out.println("Current tickets in pool: " + ticketPool.getCurrentTicketCount());
                    break;
                case "stop":
                    vendorThread.interrupt();
                    customerThread.interrupt();
                    System.out.println("System stopped.");
                    return;
                default:
                    System.out.println("Invalid command. Try again.");
            }
        }
    }

    // Utility method to prompt for valid integer inputs
    private static int promptForValidInput(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value > 0)
                    return value;
                System.out.println("Value must be greater than 0. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
