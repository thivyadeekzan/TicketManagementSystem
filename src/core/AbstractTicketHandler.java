package core;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractTicketHandler {
    protected final TicketPool ticketPool;
    protected final Lock lock = new ReentrantLock();

    public AbstractTicketHandler(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    // Abstract method to be implemented by subclasses
    public abstract void handleTickets();

    // Utility method to log actions
    protected void logAction(String message) {
        System.out.println(message);
    }
}