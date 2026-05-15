package services;

import dao.OrderDAO;
import models.Order;

import java.util.List;
import java.util.UUID;

public class OrderService {
    private OrderDAO orderDAO = new OrderDAO();
    private AIService aiService = new AIService();

    public Order placeOrder(String userId, String userHostel, String restaurantId, String restaurantLocation, String items, double totalAmount) {
        int pendingCount = getPendingOrdersCount(restaurantId);
        int estTime = aiService.calculateEstimatedDelivery(userHostel, restaurantLocation, pendingCount);
        
        Order order = new Order(
            UUID.randomUUID().toString(),
            userId,
            restaurantId,
            items,
            totalAmount,
            "PENDING",
            System.currentTimeMillis(),
            estTime
        );
        orderDAO.saveOrder(order);
        return order;
    }

    private int getPendingOrdersCount(String restaurantId) {
        int count = 0;
        List<Order> orders = orderDAO.getAllOrders();
        for (Order o : orders) {
            if (o.getRestaurantId().equals(restaurantId) && 
               (o.getStatus().equals("PENDING") || o.getStatus().equals("PREPARING"))) {
                count++;
            }
        }
        return count;
    }
}
