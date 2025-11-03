package JavaAssign;

public class Customer {
    private int id;
    private String name;
    private String phone;

    public Customer(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }

    public void displayInfo() {
        System.out.println("Customer ID: " + id);
        System.out.println("Name: " + name);
        System.out.println("Phone: " + phone);
    }
}

