package controllers;

import com.sun.net.httpserver.HttpExchange;
import dao.MenuDAO;
import dao.RestaurantDAO;
import models.MenuItem;
import models.Restaurant;
import utils.SimpleJSON;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RestaurantHandler extends BaseHandler {
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private MenuDAO menuDAO = new MenuDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("GET".equals(method)) {
            if ("/api/restaurants".equals(path)) {
                handleGetRestaurants(exchange);
            } else if ("/api/menu".equals(path)) {
                handleGetMenu(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else if ("POST".equals(method)) {
            if ("/api/restaurants".equals(path)) {
                handleSaveRestaurant(exchange);
            } else if ("/api/menu".equals(path)) {
                handleSaveMenuItem(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else {
            sendError(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleGetRestaurants(HttpExchange exchange) throws IOException {
        List<Restaurant> list = restaurantDAO.getAllRestaurants();
        SimpleJSON.ArrayBuilder ab = new SimpleJSON.ArrayBuilder();
        for (Restaurant r : list) {
            ab.addRaw(new SimpleJSON.Builder()
                .put("id", r.getId())
                .put("name", r.getName())
                .put("location", r.getLocation())
                .put("image", r.getImagePath())
                .build());
        }
        sendResponse(exchange, 200, ab.build());
    }

    private void handleGetMenu(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String restaurantId = null;
        if (query != null && query.contains("restaurantId=")) {
            restaurantId = query.split("=")[1];
        }

        if (restaurantId == null) {
            sendError(exchange, 400, "Missing restaurantId");
            return;
        }

        List<MenuItem> items = menuDAO.getMenuByRestaurant(restaurantId);
        SimpleJSON.ArrayBuilder ab = new SimpleJSON.ArrayBuilder();
        for (MenuItem item : items) {
            ab.addRaw(new SimpleJSON.Builder()
                .put("id", item.getId())
                .put("name", item.getName())
                .put("price", item.getPrice())
                .put("type", item.getType())
                .put("prepTime", item.getPrepTimeMins())
                .put("image", item.getImagePath())
                .build());
        }
        sendResponse(exchange, 200, ab.build());
    }

    private void handleSaveRestaurant(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String name = body.get("name");
        String location = body.get("location");

        if (name == null || location == null) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        Restaurant r = new Restaurant(UUID.randomUUID().toString(), name, location, "default.png");
        restaurantDAO.saveRestaurant(r);
        sendResponse(exchange, 201, "{\"message\": \"Restaurant added\"}");
    }

    private void handleSaveMenuItem(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String resId = body.get("restaurantId");
        String name = body.get("name");
        String price = body.get("price");
        String type = body.get("type");
        String prepTime = body.get("prepTime");

        if (resId == null || name == null || price == null) {
            sendError(exchange, 400, "Missing fields");
            return;
        }

        MenuItem item = new MenuItem(UUID.randomUUID().toString(), resId, name, Double.parseDouble(price), type, Integer.parseInt(prepTime), "default_food.png");
        menuDAO.saveMenuItem(item);
        sendResponse(exchange, 201, "{\"message\": \"Menu item added\"}");
    }
}
