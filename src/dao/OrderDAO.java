package dao;

import models.Order;
import utils.CSVHelper;

import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    private static final String FILE_PATH = "data/orders.csv";

    public List<Order> getAllOrders() {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        List<Order> orders = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 8) {
                orders.add(new Order(row[0], row[1], row[2], row[3], Double.parseDouble(row[4]), row[5], Long.parseLong(row[6]), Integer.parseInt(row[7])));
            }
        }
        return orders;
    }

    public List<Order> getOrdersByUser(String userId) {
        List<Order> userOrders = new ArrayList<>();
        for (Order o : getAllOrders()) {
            if (o.getUserId().equals(userId)) {
                userOrders.add(o);
            }
        }
        return userOrders;
    }
    
    public Order getOrderById(String id) {
        for (Order o : getAllOrders()) {
            if (o.getId().equals(id)) return o;
        }
        return null;
    }

    public void saveOrder(Order order) {
        CSVHelper.appendCSV(FILE_PATH, order.toCSVRow());
    }

    public void updateOrderStatus(String orderId, String newStatus) {
        List<Order> allOrders = getAllOrders();
        List<String[]> newData = new ArrayList<>();
        for (Order o : allOrders) {
            if (o.getId().equals(orderId)) {
                o.setStatus(newStatus);
            }
            newData.add(o.toCSVRow());
        }
        CSVHelper.writeCSV(FILE_PATH, newData);
    }
}
