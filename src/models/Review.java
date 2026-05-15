package models;

public class Review {
    private String id;
    private String orderId;
    private String userId;
    private String restaurantId;
    private String reviewText;
    private String sentimentTag;

    public Review(String id, String orderId, String userId, String restaurantId, String reviewText, String sentimentTag) {
        this.id = id;
        this.orderId = orderId;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.reviewText = reviewText;
        this.sentimentTag = sentimentTag;
    }

    public String getId() { return id; }
    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public String getRestaurantId() { return restaurantId; }
    public String getReviewText() { return reviewText; }
    public String getSentimentTag() { return sentimentTag; }

    public String[] toCSVRow() {
        return new String[]{id, orderId, userId, restaurantId, reviewText, sentimentTag};
    }
}
