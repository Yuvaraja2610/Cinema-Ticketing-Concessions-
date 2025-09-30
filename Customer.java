package JavaAssign;

public class Customer extends Person {
    private String phone;

    public Customer(int id, String name, String phone) {
        super(id, name);
        setPhone(phone);
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) {
        if (phone == null || !phone.matches("\\d{7,15}")) throw new IllegalArgumentException("Phone must be 7-15 digits");
        this.phone = phone;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (%s)", getId(), getName(), getPhone());
    }
}
