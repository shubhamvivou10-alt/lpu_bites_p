package dao;

import models.Review;
import utils.CSVHelper;

import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    private static final String FILE_PATH = "data/reviews.csv";

    public List<Review> getReviewsForRestaurant(String restaurantId) {
        List<String[]> rows = CSVHelper.readCSV(FILE_PATH);
        List<Review> reviews = new ArrayList<>();
        for (String[] row : rows) {
            if (row.length == 6 && row[3].equals(restaurantId)) {
                reviews.add(new Review(row[0], row[1], row[2], row[3], row[4], row[5]));
            }
        }
        return reviews;
    }

    public void saveReview(Review review) {
        CSVHelper.appendCSV(FILE_PATH, review.toCSVRow());
    }
}
