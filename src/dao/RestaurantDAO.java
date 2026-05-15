package dao;

import models.Restaurant;
import utils.CSVHelper;

import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {
    private static final String FILE_PATH = "data/restaurants.csv";

    public List<Restaurant> getAllRestaurants() {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        List<Restaurant> restaurants = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 4) {
                restaurants.add(new Restaurant(row[0], row[1], row[2], row[3]));
            }
        }
        return restaurants;
    }

    public void saveRestaurant(Restaurant r) {
        CSVHelper.appendCSV(FILE_PATH, r.toCSVRow());
    }
}
