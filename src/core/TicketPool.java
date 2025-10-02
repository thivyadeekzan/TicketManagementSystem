package core;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TicketPool {
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