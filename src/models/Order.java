package models;

public class Order {
    private String id;
    private String userId;
    private String restaurantId;
    private String items; // Comma separated item IDs
    private double totalAmount;
    private String status; // PENDING, PREPARING, OUT_FOR_DELIVERY, DELIVERED
    private long orderTimestamp;
    private int estimatedDeliveryTimeMins;

    public Order(String id, String userId, String restaurantId, String items, double totalAmount, String status, long orderTimestamp, int estimatedDeliveryTimeMins) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderTimestamp = orderTimestamp;
        this.estimatedDeliveryTimeMins = estimatedDeliveryTimeMins;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public String getItems() { return items; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getOrderTimestamp() { return orderTimestamp; }
    public int getEstimatedDeliveryTimeMins() { return estimatedDeliveryTimeMins; }

    public String[] toCSVRow() {
        return new String[]{id, userId, restaurantId, items, String.valueOf(totalAmount), status, String.valueOf(orderTimestamp), String.valueOf(estimatedDeliveryTimeMins)};
    }
}
