package models;

public class Restaurant {
    private String id;
    private String name;
    private String location;
    private String imagePath;

    public Restaurant(String id, String name, String location, String imagePath) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imagePath = imagePath;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getImagePath() { return imagePath; }

    public String[] toCSVRow() {
        return new String[]{id, name, location, imagePath};
    }
}
