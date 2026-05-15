package src;

import com.sun.net.httpserver.HttpServer;
import controllers.*;
import services.AuthService;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException {
        String portEnv = System.getenv("PORT");
        int port = (portEnv != null) ? Integer.parseInt(portEnv) : 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        AuthService authService = new AuthService();

        // Register Contexts
        server.createContext("/api/auth", new AuthHandler(authService));
        server.createContext("/api/restaurants", new RestaurantHandler());
        server.createContext("/api/menu", new RestaurantHandler());
        server.createContext("/api/orders", new OrderHandler());
        server.createContext("/api/admin/orders", new OrderHandler());
        server.createContext("/api/reviews", new OrderHandler());
        server.createContext("/api/ai/recommend", new AIHandler());
        
        // Static files (Must be last or more specific if needed)
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null); // default executor
        System.out.println("CampusBites Backend started on http://localhost:" + port);
        server.start();
    }
}
