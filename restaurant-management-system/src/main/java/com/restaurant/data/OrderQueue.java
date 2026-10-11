package com.restaurant.data;

import com.restaurant.model.Order;

import java.util.*;

/**
 * Custom Queue manager utilizing Java's PriorityQueue and LinkedList Queue.
 * Prioritizes orders in the kitchen based on wait time and VIP/urgency criteria.
 */
public class OrderQueue {

    /**
     * Kitchen ticket wrapper holding priority scoring.
     */
    public static class KitchenTicket implements Comparable<KitchenTicket> {
        private final Order order;
        private final long queueEntryTimestamp;
        private final int priorityScore; // Higher value = higher priority

        public KitchenTicket(Order order, int priorityScore) {
            this.order = order;
            this.queueEntryTimestamp = System.currentTimeMillis();
            this.priorityScore = priorityScore;
        }

        public Order getOrder() {
            return order;
        }

        public long getQueueEntryTimestamp() {
            return queueEntryTimestamp;
        }

        public int getPriorityScore() {
            return priorityScore;
        }

        @Override
        public int compareTo(KitchenTicket other) {
            // Higher priority score first, then older queue entry timestamp (FIFO tie breaker)
            if (this.priorityScore != other.priorityScore) {
                return Integer.compare(other.priorityScore, this.priorityScore);
            }
            return Long.compare(this.queueEntryTimestamp, other.queueEntryTimestamp);
        }
    }

    private final PriorityQueue<KitchenTicket> kitchenPriorityQueue;
    private final Queue<Order> regularFifoQueue;

    public OrderQueue() {
        this.kitchenPriorityQueue = new PriorityQueue<>();
        this.regularFifoQueue = new LinkedList<>();
    }

    public synchronized void enqueue(Order order, int priority) {
        if (order != null) {
            kitchenPriorityQueue.offer(new KitchenTicket(order, priority));
            regularFifoQueue.offer(order);
        }
    }

    public synchronized Order dequeuePriority() {
        KitchenTicket ticket = kitchenPriorityQueue.poll();
        if (ticket != null) {
            regularFifoQueue.remove(ticket.getOrder());
            return ticket.getOrder();
        }
        return null;
    }

    public synchronized List<Order> getAllPendingPriorityOrders() {
        List<KitchenTicket> copy = new ArrayList<>(kitchenPriorityQueue);
        Collections.sort(copy);
        List<Order> orderedList = new ArrayList<>();
        for (KitchenTicket ticket : copy) {
            orderedList.add(ticket.getOrder());
        }
        return orderedList;
    }

    public synchronized int size() {
        return kitchenPriorityQueue.size();
    }

    public synchronized boolean isEmpty() {
        return kitchenPriorityQueue.isEmpty();
    }

    public synchronized void clear() {
        kitchenPriorityQueue.clear();
        regularFifoQueue.clear();
    }
}
