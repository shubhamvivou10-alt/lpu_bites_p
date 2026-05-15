package dao;

import models.User;
import utils.CSVHelper;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private static final String FILE_PATH = "data/users.csv";

    public List<User> getAllUsers() {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        List<User> users = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 7) {
                users.add(new User(row[0], row[1], row[2], row[3], row[4], row[5], row[6]));
            }
        }
        return users;
    }

    public User getUserByRegNo(String regNo) {
        for (User u : getAllUsers()) {
            if (u.getRegNo().equals(regNo)) return u;
        }
        return null;
    }

    public User getUserById(String id) {
        for (User u : getAllUsers()) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public void saveUser(User user) {
        CSVHelper.appendCSV(FILE_PATH, user.toCSVRow());
    }
}
