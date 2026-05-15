package models;

public class User {
    private String id;
    private String regNo;
    private String name;
    private String hostelBlock;
    private String phone;
    private String role; // STUDENT or ADMIN
    private String passwordHash;

    public User(String id, String regNo, String name, String hostelBlock, String phone, String role, String passwordHash) {
        this.id = id;
        this.regNo = regNo;
        this.name = name;
        this.hostelBlock = hostelBlock;
        this.phone = phone;
        this.role = role;
        this.passwordHash = passwordHash;
    }

    public String getId() { return id; }
    public String getRegNo() { return regNo; }
    public String getName() { return name; }
    public String getHostelBlock() { return hostelBlock; }
    public String getPhone() { return phone; }
    public String getRole() { return role; }
    public String getPasswordHash() { return passwordHash; }
    
    // Convert to CSV Row
    public String[] toCSVRow() {
        return new String[]{id, regNo, name, hostelBlock, phone, role, passwordHash};
    }
}
