package controllers;

import com.sun.net.httpserver.HttpExchange;
import services.AIService;
import utils.SimpleJSON;

import java.io.IOException;

public class AIHandler extends BaseHandler {
    private AIService aiService = new AIService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("GET".equals(method) && "/api/ai/recommend".equals(path)) {
            String query = exchange.getRequestURI().getQuery();
            String weather = "";
            if (query != null && query.contains("weather=")) {
                weather = query.split("=")[1];
            }
            
            String suggestion = aiService.getFoodSuggestion(weather);
            String json = new SimpleJSON.Builder()
                .put("suggestion", suggestion)
                .build();
            sendResponse(exchange, 200, json);
        } else {
            sendError(exchange, 404, "Not Found");
        }
    }
}
