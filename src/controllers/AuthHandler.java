package controllers;

import com.sun.net.httpserver.HttpExchange;
import models.User;
import services.AuthService;
import utils.SimpleJSON;

import java.io.IOException;
import java.util.Map;

public class AuthHandler extends BaseHandler {
    private AuthService authService;

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("POST".equals(method)) {
            if ("/api/auth/register".equals(path)) {
                handleRegister(exchange);
            } else if ("/api/auth/login".equals(path)) {
                handleLogin(exchange);
            } else {
                sendError(exchange, 404, "Not Found");
            }
        } else {
            sendError(exchange, 405, "Method Not Allowed");
        }
    }

    private void handleRegister(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String regNo = body.get("regNo");
        String name = body.get("name");
        String hostelBlock = body.get("hostelBlock");
        String phone = body.get("phone");
        String role = body.get("role");
        String password = body.get("password");

        if (regNo == null || password == null || name == null) {
            sendError(exchange, 400, "Missing required fields");
            return;
        }

        User user = authService.registerUser(regNo, name, hostelBlock, phone, role, password);
        if (user != null) {
            String json = new SimpleJSON.Builder()
                .put("message", "User registered successfully")
                .put("userId", user.getId())
                .build();
            sendResponse(exchange, 201, json);
        } else {
            sendError(exchange, 409, "User with this registration number already exists");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        Map<String, String> body = parseBody(exchange);
        String regNo = body.get("regNo");
        String password = body.get("password");

        if (regNo == null || password == null) {
            sendError(exchange, 400, "Missing regNo or password");
            return;
        }

        String token = authService.login(regNo, password);
        if (token != null) {
            User user = authService.getUserByToken(token);
            String json = new SimpleJSON.Builder()
                .put("message", "Login successful")
                .put("token", token)
                .put("role", user.getRole())
                .put("name", user.getName())
                .put("userId", user.getId())
                .build();
            sendResponse(exchange, 200, json);
        } else {
            sendError(exchange, 401, "Invalid credentials");
        }
    }
}
