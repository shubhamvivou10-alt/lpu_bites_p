package dao;

import models.MenuItem;
import utils.CSVHelper;

import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    private static final String FILE_PATH = "data/menu_items.csv";

    public List<MenuItem> getMenuByRestaurant(String restaurantId) {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        List<MenuItem> items = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 7 && row[1].equals(restaurantId)) {
                items.add(new MenuItem(row[0], row[1], row[2], Double.parseDouble(row[3]), row[4], Integer.parseInt(row[5]), row[6]));
            }
        }
        return items;
    }

    public MenuItem getItemById(String id) {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        for (String[] row : rows) {
            if (row.length == 7 && row[0].equals(id)) {
                return new MenuItem(row[0], row[1], row[2], Double.parseDouble(row[3]), row[4], Integer.parseInt(row[5]), row[6]);
            }
        }
        return null;
    }

    public void saveMenuItem(MenuItem item) {
        CSVHelper.appendCSV(FILE_PATH, item.toCSVRow());
    }
}
