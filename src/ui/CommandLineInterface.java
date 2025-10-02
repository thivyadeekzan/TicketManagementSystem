package ui;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

// Configuration class to manage system parameters
class Configuration {
    private int totalTickets;
    private int ticketReleaseRate;
    private int customerRetrievalRate;
    private int maxTicketCapacity;

    public void configureSystem() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Configure the ticketing system:");
        totalTickets = promptForValidInput(scanner, "Total tickets available:");
        ticketReleaseRate = promptForValidInput(scanner, "Ticket release rate:");
        customerRetrievalRate = promptForValidInput(scanner, "Customer retrieval rate:");
        maxTicketCapacity = promptForValidInput(scanner, "Maximum ticket capacity:");
    }

    private int promptForValidInput(Scanner scanner, String prompt) {
        int value;
        while (true) {
            System.out.print(prompt);
            try {
                value = Integer.parseInt(scanner.nextLine());
                if (value > 0)
                    break;
                System.out.println("Value must be greater than 0. Please try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
        return value;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
}

// Ticket pool class to manage tickets
class TicketPool {
    private final Queue<Integer> tickets = new ConcurrentLinkedQueue<>();
    private final int maxCapacity;
    private final Lock lock = new ReentrantLock();

    public TicketPool(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public boolean addTickets(int count) {
        lock.lock();
        try {
            if (tickets.size() + count <= maxCapacity) {
                for (int i = 0; i < count; i++) {
                    tickets.add(1);
                }
                System.out.println(count + " tickets added. Current tickets: " + tickets.size());
                return true;
            } else {
                System.out.println("Cannot add tickets. Max capacity reached.");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean removeTicket() {
        lock.lock();
        try {
            if (!tickets.isEmpty()) {
                tickets.poll();
                System.out.println("Ticket purchased. Remaining tickets: " + tickets.size());
                return true;
            } else {
                System.out.println("No tickets available for purchase.");
                return false;
            }
        } finally {
            lock.unlock();
        }
    }

    public int getCurrentTicketCount() {
        return tickets.size();
    }
}

// Vendor class implementing Runnable for ticket producers
class Vendor implements Runnable {
    private final TicketPool ticketPool;
    private final int releaseRate;

    public Vendor(TicketPool ticketPool, int releaseRate) {
        this.ticketPool = ticketPool;
        this.releaseRate = releaseRate;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                if (!ticketPool.addTickets(releaseRate)) {
                    Thread.sleep(1000);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Vendor interrupted.");
            }
        }
    }
}

// Customer class implementing Runnable for ticket consumers
class Customer implements Runnable {
    private final TicketPool ticketPool;

    public Customer(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ticketPool.removeTicket();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Customer interrupted.");
            }
        }
    }
}

// Main class to manage the system
public class CommandLineInterface {
    public static void main(String[] args) {
        Configuration config = new Configuration();
        config.configureSystem();

        TicketPool ticketPool = new TicketPool(config.getMaxTicketCapacity());
        Thread vendorThread = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()));
        Thread customerThread = new Thread(new Customer(ticketPool));

        System.out.println("Starting the ticketing system...");
        vendorThread.start();
        customerThread.start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Enter 'stop' to terminate the system:");
            if (scanner.nextLine().equalsIgnoreCase("stop")) {
                vendorThread.interrupt();
                customerThread.interrupt();
                break;
            }
        }

        scanner.close();
        System.out.println("System terminated.");
    }
}
