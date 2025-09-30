package JavaAssign;
import java.util.*;

public class Booking {
    private final int id;
    private final Customer customer;
    private final Show show;
    private final List<Seat> seats;
    private final double fare;
    private final double taxes;
    private final double total;
    private final Date bookedAt;

    public Booking(int id, Customer customer, Show show, List<Seat> seats, double fare, double taxes, double total) {
        if (customer == null || show == null || seats == null || seats.isEmpty()) throw new IllegalArgumentException("Booking requires customer, show and seats");
        this.id = id;
        this.customer = customer;
        this.show = show;
        this.seats = new ArrayList<>(seats);
        this.fare = fare;
        this.taxes = taxes;
        this.total = total;
        this.bookedAt = new Date();
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Show getShow() { return show; }
    public List<Seat> getSeats() { return Collections.unmodifiableList(seats); }
    public double getFare() { return fare; }
    public double getTaxes() { return taxes; }
    public double getTotal() { return total; }

    public void printReceipt() {
        System.out.println("------ Booking Receipt ------");
        System.out.println("Booking ID: " + id);
        System.out.println("Customer: " + customer);
        System.out.println("Show: " + show.brief());
        System.out.println("Seats: " + seatListToString(seats));
        System.out.printf("Fare: %.2f\nTaxes: %.2f\nTotal: %.2f\n", fare, taxes, total);
        System.out.println("Booked at: " + bookedAt);
        System.out.println("------------------------------");
    }

    public String shortSummary() {
        return String.format("Booking %d | Show %d | Cust: %s | Seats: %s", id, show.getId(), customer.getName(), seatListToString(seats));
    }

    private static String seatListToString(List<Seat> seats) {
        StringJoiner sj = new StringJoiner(", ");
        for (Seat s : seats) sj.add(s.getSeatId());
        return sj.toString();
    }
}
