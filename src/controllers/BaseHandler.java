package controllers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import utils.SimpleJSON;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class BaseHandler implements HttpHandler {
    
    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        // Handle CORS if needed for local dev, not strictly necessary if serving from same origin
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    protected Map<String, String> parseBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        StringBuilder body = new StringBuilder();
        int i;
        while ((i = is.read()) != -1) {
            body.append((char) i);
        }
        return SimpleJSON.parseFlatJSON(body.toString());
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = new SimpleJSON.Builder().put("error", message).build();
        sendResponse(exchange, statusCode, json);
    }
}
