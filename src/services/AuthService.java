package services;

import dao.UserDAO;
import models.User;
import utils.HashUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    private UserDAO userDAO = new UserDAO();
    // In-memory session store: token -> userId
    private static Map<String, String> activeSessions = new HashMap<>();

    public User registerUser(String regNo, String name, String hostelBlock, String phone, String role, String password) {
        if (userDAO.getUserByRegNo(regNo) != null) {
            return null; // User exists
        }
        String id = UUID.randomUUID().toString();
        String hash = HashUtils.hashPassword(password);
        User user = new User(id, regNo, name, hostelBlock, phone, role, hash);
        userDAO.saveUser(user);
        return user;
    }

    public String login(String regNo, String password) {
        User user = userDAO.getUserByRegNo(regNo);
        if (user != null) {
            String hash = HashUtils.hashPassword(password);
            if (user.getPasswordHash().equals(hash)) {
                String token = UUID.randomUUID().toString();
                activeSessions.put(token, user.getId());
                return token;
            }
        }
        return null;
    }

    public User getUserByToken(String token) {
        if (token == null) return null;
        String userId = activeSessions.get(token);
        if (userId != null) {
            return userDAO.getUserById(userId);
        }
        return null;
    }

    public void logout(String token) {
        activeSessions.remove(token);
    }
}
