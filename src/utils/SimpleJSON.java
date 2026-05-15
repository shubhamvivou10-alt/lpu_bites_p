package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleJSON {

    // Very basic JSON object builder
    public static class Builder {
        private StringBuilder sb;
        private boolean first = true;

        public Builder() {
            sb = new StringBuilder("{");
        }

        public Builder put(String key, String value) {
            if (!first) sb.append(",");
            sb.append("\"").append(key).append("\":");
            if (value == null) {
                sb.append("null");
            } else {
                sb.append("\"").append(value.replace("\"", "\\\"")).append("\"");
            }
            first = false;
            return this;
        }

        public Builder put(String key, int value) {
            if (!first) sb.append(",");
            sb.append("\"").append(key).append("\":").append(value);
            first = false;
            return this;
        }

        public Builder put(String key, double value) {
            if (!first) sb.append(",");
            sb.append("\"").append(key).append("\":").append(value);
            first = false;
            return this;
        }

        public Builder putRaw(String key, String rawJsonValue) {
            if (!first) sb.append(",");
            sb.append("\"").append(key).append("\":").append(rawJsonValue);
            first = false;
            return this;
        }

        public String build() {
            return sb.append("}").toString();
        }
    }

    public static class ArrayBuilder {
        private StringBuilder sb;
        private boolean first = true;

        public ArrayBuilder() {
            sb = new StringBuilder("[");
        }

        public ArrayBuilder addRaw(String rawJsonValue) {
            if (!first) sb.append(",");
            sb.append(rawJsonValue);
            first = false;
            return this;
        }
        
        public ArrayBuilder addString(String val) {
            if (!first) sb.append(",");
            sb.append("\"").append(val.replace("\"", "\\\"")).append("\"");
            first = false;
            return this;
        }

        public String build() {
            return sb.append("]").toString();
        }
    }

    // Basic and naive JSON parser for flat key-value pairs (String to String)
    public static Map<String, String> parseFlatJSON(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;
        
        json = json.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length() - 1);
        
        boolean inQuotes = false;
        StringBuilder currentKey = new StringBuilder();
        StringBuilder currentValue = new StringBuilder();
        boolean parsingKey = true;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '\"') {
                if (i > 0 && json.charAt(i - 1) == '\\') {
                    if (parsingKey) currentKey.append(c);
                    else currentValue.append(c);
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ':' && !inQuotes && parsingKey) {
                parsingKey = false;
            } else if (c == ',' && !inQuotes) {
                map.put(currentKey.toString().trim(), currentValue.toString().trim());
                currentKey.setLength(0);
                currentValue.setLength(0);
                parsingKey = true;
            } else {
                if (inQuotes || (c != ' ' && c != '\n' && c != '\r' && c != '\t')) {
                    if (parsingKey) currentKey.append(c);
                    else currentValue.append(c);
                }
            }
        }
        
        if (currentKey.length() > 0) {
            map.put(currentKey.toString().trim(), currentValue.toString().trim());
        }
        
        return map;
    }
}
