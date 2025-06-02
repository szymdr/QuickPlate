package com.quickplate.model;

public class OrderStatus {
    public static final String PENDING   = "pending";
    public static final String PAID      = "paid";
    public static final String ACCEPTED  = "accepted";
    public static final String PREPARING = "preparing";
    public static final String READY     = "ready";
    public static final String COMPLETED = "completed";
    public static final String CANCELLED = "cancelled";

    private OrderStatus() {}

    public static boolean isValidStatus(String status) {
        return PENDING.equals(status)
            || PAID.equals(status)
            || ACCEPTED.equals(status)
            || PREPARING.equals(status)
            || READY.equals(status)
            || COMPLETED.equals(status)
            || CANCELLED.equals(status);
    }

    public static String getNextStatus(String currentStatus) {
        switch (currentStatus) {
            case PENDING:   return PAID;
            case PAID:      return ACCEPTED;
            case ACCEPTED:  return PREPARING;
            case PREPARING: return READY;
            case READY:     return COMPLETED;
            case COMPLETED:
            case CANCELLED: return null;
            default: throw new IllegalArgumentException("Invalid order status: " + currentStatus);
        }
    }
}
