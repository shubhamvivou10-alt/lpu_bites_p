package controllers;

import com.sun.net.httpserver.HttpExchange;
import dao.OrderDAO;
import dao.ReviewDAO;
import models.Order;
import models.Review;
import services.AIService;
import services.OrderService;
import utils.SimpleJSON;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrderHandler extends BaseHandler {
    private OrderService orderService = new OrderService();
    private OrderDAO orderDAO = new OrderDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private AIService aiService = new AIService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            if ("/api/orders".equals(path)) {
                handlePlaceOrder(exchange);
            } else if ("/api/reviews".equals(path)) {
                handleSaveReview(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else if ("GET".equals(method)) {
            if ("/api/orders".equals(path)) {
                handleGetOrders(exchange);
            } else if ("/api/admin/orders".equals(path)) {
                handleGetAllOrders(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else if ("PUT".equals(method)) {
            if ("/api/admin/orders/status".equals(path)) {
                handleUpdateStatus(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else {
            sendError(exchange, 405, "Method Not Allowed");
        }
    }

    private void handlePlaceOrder(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String userId = body.get("userId");
        String hostel = body.get("hostelBlock");
        String resId = body.get("restaurantId");
        String resLoc = body.get("restaurantLocation");
        String items = body.get("items");
        String total = body.get("totalAmount");

        if (userId == null || resId == null || items == null) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        Order order = orderService.placeOrder(userId, hostel, resId, resLoc, items, Double.parseDouble(total));
        String json = new SimpleJSON.Builder()
            .put("id", order.getId())
            .put("estimatedTime", order.getEstimatedDeliveryTimeMins())
            .build();
        sendResponse(exchange, 201, json);
    }

    private void handleGetOrders(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String userId = null;
        if (query != null && query.contains("userId=")) {
            userId = query.split("=")[1];
        }
        if (userId == null) {
            sendError(exchange, 400, "Missing userId");
            return;
        }

        List<Order> orders = orderDAO.getOrdersByUser(userId);
        sendOrderList(exchange, orders);
    }

    private void handleGetAllOrders(HttpExchange exchange) throws IOException {
        List<Order> orders = orderDAO.getAllOrders();
        sendOrderList(exchange, orders);
    }

    private void sendOrderList(HttpExchange exchange, List<Order> orders) throws IOException {
        SimpleJSON.ArrayBuilder ab = new SimpleJSON.ArrayBuilder();
        for (Order o : orders) {
            ab.addRaw(new SimpleJSON.Builder()
                .put("id", o.getId())
                .put("restaurantId", o.getRestaurantId())
                .put("totalAmount", o.getTotalAmount())
                .put("status", o.getStatus())
                .put("timestamp", o.getOrderTimestamp())
                .put("estTime", o.getEstimatedDeliveryTimeMins())
                .build());
        }
        sendResponse(exchange, 200, ab.build());
    }

    private void handleUpdateStatus(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String orderId = body.get("orderId");
        String status = body.get("status");

        if (orderId == null || status == null) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        orderDAO.updateOrderStatus(orderId, status);
        sendResponse(exchange, 200, "{\"message\": \"Status updated\"}");
    }

    private void handleSaveReview(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String orderId = body.get("orderId");
        String userId = body.get("userId");
        String resId = body.get("restaurantId");
        String text = body.get("reviewText");

        if (orderId == null || text == null) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        String sentiment = aiService.analyzeSentiment(text);
        Review review = new Review(UUID.randomUUID().toString(), orderId, userId, resId, text, sentiment);
        reviewDAO.saveReview(review);

        String json = new SimpleJSON.Builder()
            .put("message", "Review saved")
            .put("sentiment", sentiment)
            .build();
        sendResponse(exchange, 201, json);
    }
}
