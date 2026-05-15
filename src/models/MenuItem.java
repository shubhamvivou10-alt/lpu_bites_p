package models;

public class MenuItem {
    private String id;
    private String restaurantId;
    private String name;
    private double price;
    private String type; // VEG or NON-VEG
    private int prepTimeMins;
    private String imagePath;

    public MenuItem(String id, String restaurantId, String name, double price, String type, int prepTimeMins, String imagePath) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.price = price;
        this.type = type;
        this.prepTimeMins = prepTimeMins;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public String getRestaurantId() { return restaurantId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getType() { return type; }
    public int getPrepTimeMins() { return prepTimeMins; }
    public String getImagePath() { return imagePath; }

    public String[] toCSVRow() {
        return new String[]{id, restaurantId, name, String.valueOf(price), type, String.valueOf(prepTimeMins), imagePath};
    }
}
