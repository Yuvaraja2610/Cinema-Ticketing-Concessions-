package JavaAssign;
import java.util.*;

public class ConcessionOrder {
    private final int id;
    private final Booking linkedBooking; // may be null for standalone orders
    private final List<ConcessionItem> items = new ArrayList<>();
    private double subtotal;
    private double taxes;
    private double total;
    private final Date orderedAt;

    // Tax rates reused
    public static final double SERVICE_TAX_RATE = 0.05;
    public static final double GST_RATE = 0.12;

    public ConcessionOrder(int id, Booking linkedBooking) {
        this.id = id;
        this.linkedBooking = linkedBooking;
        this.orderedAt = new Date();
    }

    public int getId() { return id; }
    public Booking getLinkedBooking() { return linkedBooking; }

    public void addItem(ConcessionItem item) {
        if (item == null) throw new IllegalArgumentException("Item required");
        items.add(item);
    }

    public List<ConcessionItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void calculateTotals() {
        subtotal = items.stream().mapToDouble(ConcessionItem::total).sum();
        taxes = subtotal * (SERVICE_TAX_RATE + GST_RATE);
        total = subtotal + taxes;
    }

    public double getSubtotal() { return subtotal; }
    public double getTaxes() { return taxes; }
    public double getTotal() { return total; }

    public void printReceipt() {
        System.out.println("----- Concession Receipt -----");
        System.out.println("Order ID: " + id + (linkedBooking != null ? " (Linked to Booking " + linkedBooking.getId() + ")" : ""));
        for (ConcessionItem i : items) System.out.println(i);
        System.out.printf("Subtotal: %.2f\nTaxes: %.2f\nTotal: %.2f\n", subtotal, taxes, total);
        System.out.println("Ordered at: " + orderedAt);
        System.out.println("------------------------------");
    }

    // Small helper class for items
    public static class ConcessionItem {
        private final int id;
        private final String name;
        private final double unitPrice;
        private final int qty;

        public ConcessionItem(int id, String name, double unitPrice, int qty) {
            if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Item name required");
            if (unitPrice < 0) throw new IllegalArgumentException("Unit price >= 0");
            if (qty <= 0) throw new IllegalArgumentException("Quantity must be positive");
            this.id = id;
            this.name = name;
            this.unitPrice = unitPrice;
            this.qty = qty;
        }

        public double total() { return unitPrice * qty; }

        @Override
        public String toString() {
            return String.format("%d x %s @ %.2f = %.2f", qty, name, unitPrice, total());
        }
    }

    // Menu item representation (for the app's static menu)
    public static class MenuItem {
        private final int id;
        private final String name;
        private final double price;

        public MenuItem(int id, String name, double price) {
            this.id = id; this.name = name; this.price = price;
        }
        public int getId() { return id; }
        public String getName() { return name; }
        public double getPrice() { return price; }
        @Override
        public String toString() { return id + ": " + name + " - " + price; }
    }
}
