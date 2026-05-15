package services;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class AIService {

    // Smart Craving Suggester
    public String getFoodSuggestion(String weather) {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();

        String suggestion = "";
        if (hour >= 6 && hour < 11) {
            suggestion = "Morning vibes! How about some Hot Parathas or a fresh Coffee?";
        } else if (hour >= 11 && hour < 16) {
            suggestion = "Lunch time! A hearty Thali or Biryani sounds perfect right now.";
        } else if (hour >= 16 && hour < 20) {
            suggestion = "Evening cravings? Grab some Momos or a quick Sandwich.";
        } else {
            suggestion = "Late night hunger? Maggi or a juicy Burger is the way to go.";
        }

        if (weather != null && !weather.isEmpty()) {
            if (weather.equalsIgnoreCase("rainy")) {
                suggestion += " It's raining outside! Hot Pakoras would be amazing.";
            } else if (weather.equalsIgnoreCase("cold")) {
                suggestion += " Chilly weather! Stay warm with a hot soup or tea.";
            }
        }
        return suggestion;
    }

    // Dynamic Delivery Estimator
    public int calculateEstimatedDelivery(String userHostel, String restaurantLocation, int pendingOrders) {
        int basePrepTime = 15; // default prep time in minutes
        int queueDelay = pendingOrders * 3; // 3 minutes per pending order
        
        // Simple distance penalty based on arbitrary rules
        int distancePenalty = 5; 
        if (userHostel.equalsIgnoreCase(restaurantLocation) || 
           (userHostel.contains("BH-") && restaurantLocation.contains("Uni-Mall"))) {
            distancePenalty = 10;
        } else if (userHostel.contains("GH-")) {
            distancePenalty = 15;
        }

        return basePrepTime + queueDelay + distancePenalty;
    }

    // Sentiment Review Analyzer
    public String analyzeSentiment(String reviewText) {
        if (reviewText == null || reviewText.isEmpty()) return "AVERAGE";
        
        String text = reviewText.toLowerCase();
        
        List<String> positiveKeywords = Arrays.asList("delicious", "fast", "hot", "amazing", "great", "good", "loved", "tasty", "awesome", "perfect");
        List<String> negativeKeywords = Arrays.asList("cold", "late", "bad", "stale", "worst", "terrible", "slow", "poor", "yuck");
        
        int posCount = 0;
        int negCount = 0;

        for (String word : text.split("\\s+")) {
            // Remove punctuation
            word = word.replaceAll("[^a-zA-Z]", "");
            if (positiveKeywords.contains(word)) posCount++;
            if (negativeKeywords.contains(word)) negCount++;
        }

        if (posCount > negCount) return "EXCELLENT";
        if (negCount > posCount) return "POOR";
        return "AVERAGE";
    }
}
