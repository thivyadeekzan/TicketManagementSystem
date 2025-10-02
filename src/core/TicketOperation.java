package core;

public class TicketOperation extends AbstractTicketHandler {

    private final int ticketCount;
    private final OperationType operationType;

    public enum OperationType {
        ADD, REMOVE
    }

    public TicketOperation(TicketPool ticketPool, int ticketCount, OperationType operationType) {
        super(ticketPool);
        this.ticketCount = ticketCount;
        this.operationType = operationType;
    }

    @Override
    public void handleTickets() {
        lock.lock();
        try {
            if (operationType == OperationType.ADD) {
                boolean success = ticketPool.addTickets(ticketCount);
                logAction(success ? ticketCount + " tickets added." : "Failed to add tickets. Max capacity reached.");
            } else if (operationType == OperationType.REMOVE) {
                for (int i = 0; i < ticketCount; i++) {
                    boolean success = ticketPool.removeTicket();
                    if (!success) {
                        logAction("No tickets available for removal.");
                        break;
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}