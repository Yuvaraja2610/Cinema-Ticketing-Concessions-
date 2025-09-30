package JavaAssign;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CinemaApp - main menu-driven console application
 */
public class CinemaApp {
    // In-memory data stores
    private static final Map<Integer, Movie> movies = new LinkedHashMap<>();
    private static final Map<Integer, Screen> screens = new LinkedHashMap<>();
    private static final Map<Integer, Show> shows = new LinkedHashMap<>();
    private static final Map<Integer, Customer> customers = new LinkedHashMap<>();
    private static final Map<Integer, Booking> bookings = new LinkedHashMap<>();
    private static final Map<Integer, ConcessionOrder> concessions = new LinkedHashMap<>();

    // ID generators
    private static final AtomicInteger movieIdGen = new AtomicInteger(100);
    private static final AtomicInteger screenIdGen = new AtomicInteger(10);
    private static final AtomicInteger showIdGen = new AtomicInteger(500);
    private static final AtomicInteger customerIdGen = new AtomicInteger(200);
    private static final AtomicInteger bookingIdGen = new AtomicInteger(1000);
    private static final AtomicInteger concessionIdGen = new AtomicInteger(3000);

    private static final Scanner scanner = new Scanner(System.in);

    // Concession menu (static)
    private static final Map<Integer, ConcessionOrder.MenuItem> concessionMenu = new LinkedHashMap<>();
    static {
        concessionMenu.put(1, new ConcessionOrder.MenuItem(1, "Popcorn (Small)", 100.0));
        concessionMenu.put(2, new ConcessionOrder.MenuItem(2, "Popcorn (Large)", 180.0));
        concessionMenu.put(3, new ConcessionOrder.MenuItem(3, "Cold Drink", 80.0));
        concessionMenu.put(4, new ConcessionOrder.MenuItem(4, "Nachos", 120.0));
        concessionMenu.put(5, new ConcessionOrder.MenuItem(5, "Candy", 60.0));
    }

    // Tax rates for bookings
    private static final double SERVICE_TAX_RATE = 0.05;
    private static final double GST_RATE = 0.12;

    public static void main(String[] args) {
        System.out.println("=== Cinema Ticketing & Concessions System ===");
        seedSampleData();
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1 -> addMovie();
                case 2 -> scheduleShow();
                case 3 -> addCustomer();
                case 4 -> bookSeats();
                case 5 -> cancelBooking();
                case 6 -> orderConcessions();
                case 7 -> displayShowsAvailability();
                case 8 -> { running = false; System.out.println("Exiting. Goodbye!"); }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }

    private static void printMainMenu() {
        System.out.println("\nMain Menu:");
        System.out.println("1. Add Movie");
        System.out.println("2. Schedule Show");
        System.out.println("3. Add Customer");
        System.out.println("4. Book Seats");
        System.out.println("5. Cancel Booking");
        System.out.println("6. Order Concessions");
        System.out.println("7. Display Shows & Availability");
        System.out.println("8. Exit");
    }

    // Menu actions
    private static void addMovie() {
        System.out.println("\n--- Add Movie ---");
        String title = readNonEmptyString("Enter movie title: ");
        String genre = readNonEmptyString("Enter genre: ");
        int duration = readIntInRange("Enter duration (minutes): ", 10, 500);
        Movie m = new Movie(movieIdGen.getAndIncrement(), title, genre, duration);
        movies.put(m.getId(), m);
        System.out.println("Added: " + m);
    }

    private static void scheduleShow() {
        System.out.println("\n--- Schedule Show ---");
        if (movies.isEmpty()) { System.out.println("No movies yet. Add movies first."); return; }
        if (screens.isEmpty()) {
            Screen s = new Screen(screenIdGen.getAndIncrement(), "Screen 1");
            screens.put(s.getId(), s);
            System.out.println("Created default screen: " + s);
        }

        System.out.println("Movies:");
        movies.values().forEach(m -> System.out.println(m.getId() + ": " + m.getTitle()));

        int mid = readInt("Enter movie id: ");
        Movie movie = movies.get(mid);
        if (movie == null) { System.out.println("Invalid movie id."); return; }

        System.out.println("Screens:");
        screens.values().forEach(s -> System.out.println(s.getId() + ": " + s.getName()));
        int sid = readInt("Enter screen id: ");
        Screen screen = screens.get(sid);
        if (screen == null) { System.out.println("Invalid screen id."); return; }

        String datetime = readNonEmptyString("Enter show date & time (e.g., 2025-10-01 18:00): ");
        double baseFare = readDouble("Enter base fare per seat: ");

        Show show = new Show(showIdGen.getAndIncrement(), movie, screen, datetime, baseFare);
        shows.put(show.getId(), show);
        screen.addShow(show);
        System.out.println("Scheduled: " + show.brief());
    }

    private static void addCustomer() {
        System.out.println("\n--- Add Customer ---");
        String name = readNonEmptyString("Enter name: ");
        String phone = readPhone("Enter phone (7-15 digits): ");
        Customer c = new Customer(customerIdGen.getAndIncrement(), name, phone);
        customers.put(c.getId(), c);
        System.out.println("Added customer: " + c);
    }

    private static void bookSeats() {
        System.out.println("\n--- Book Seats ---");
        if (shows.isEmpty()) { System.out.println("No shows scheduled."); return; }
        displayShowsBrief();
        int sid = readInt("Enter Show ID to book: ");
        Show show = shows.get(sid);
        if (show == null) { System.out.println("Invalid show id."); return; }

        // customer selection/creation
        System.out.println("Existing customers:");
        customers.values().forEach(c -> System.out.println(c.getId() + ": " + c.getName() + " (" + c.getPhone() + ")"));
        int cid = readInt("Enter customer id or 0 to create new: ");
        Customer customer;
        if (cid == 0) {
            String name = readNonEmptyString("Name: ");
            String phone = readPhone("Phone: ");
            customer = new Customer(customerIdGen.getAndIncrement(), name, phone);
            customers.put(customer.getId(), customer);
            System.out.println("Created: " + customer);
        } else {
            customer = customers.get(cid);
            if (customer == null) { System.out.println("Invalid customer id."); return; }
        }

        show.printSeatAvailability();
        List<Seat> selected = new ArrayList<>();
        while (true) {
            String seatId = readNonEmptyString("Enter seat id to select (e.g., A1) or DONE to finish: ");
            if ("DONE".equalsIgnoreCase(seatId)) break;
            Optional<Seat> maybe = show.getSeatById(seatId.toUpperCase());
            if (maybe.isEmpty()) { System.out.println("Seat not found."); continue; }
            Seat seat = maybe.get();
            if (!seat.isAvailable()) { System.out.println("Seat not available. Choose another."); continue; }
            seat.hold();
            selected.add(seat);
            System.out.println("Selected: " + seat.getSeatId());
        }

        if (selected.isEmpty()) {
            System.out.println("No seats chosen. Aborting booking.");
            return;
        }

        double subtotal = selected.size() * show.getBaseFare();
        double taxes = subtotal * (SERVICE_TAX_RATE + GST_RATE);
        double total = subtotal + taxes;

        System.out.println("\n--- Booking Summary ---");
        System.out.println("Show: " + show.brief());
        System.out.println("Customer: " + customer);
        System.out.println("Seats: " + seatListToString(selected));
        System.out.printf("Fare: %.2f  Taxes: %.2f  Total: %.2f%n", subtotal, taxes, total);

        String conf = readNonEmptyString("Proceed to payment? (Y/N): ");
        if (!conf.equalsIgnoreCase("Y")) {
            selected.forEach(Seat::releaseHold);
            System.out.println("Payment cancelled. Seats released.");
            return;
        }

        if (!processPayment(total)) {
            selected.forEach(Seat::releaseHold);
            System.out.println("Payment failed. Seats released.");
            return;
        }

        // finalize
        selected.forEach(Seat::book);
        Booking booking = new Booking(bookingIdGen.getAndIncrement(), customer, show, selected, subtotal, taxes, total);
        bookings.put(booking.getId(), booking);
        show.addBooking(booking);
        System.out.println("\nBooking confirmed.");
        booking.printReceipt();
    }

    private static void cancelBooking() {
        System.out.println("\n--- Cancel Booking ---");
        if (bookings.isEmpty()) { System.out.println("No bookings exist."); return; }
        bookings.values().forEach(b -> System.out.println(b.getId() + ": " + b.shortSummary()));
        int bid = readInt("Enter booking id to cancel: ");
        Booking b = bookings.get(bid);
        if (b == null) { System.out.println("Invalid booking id."); return; }

        String reason = readNonEmptyString("Enter cancellation reason: ");
        // release seats immediately
        b.getSeats().forEach(Seat::release);
        b.getShow().removeBooking(b);
        bookings.remove(bid);
        System.out.println("Booking cancelled and seats released.");
    }

    private static void orderConcessions() {
        System.out.println("\n--- Order Concessions ---");
        System.out.println("Link to booking? (Y/N)");
        String link = readNonEmptyString("Choice: ");
        Booking linked = null;
        if (link.equalsIgnoreCase("Y") && !bookings.isEmpty()) {
            bookings.values().forEach(b -> System.out.println(b.getId() + ": " + b.shortSummary()));
            int bid = readInt("Enter booking id to link: ");
            linked = bookings.get(bid);
            if (linked == null) System.out.println("Invalid booking id. Proceeding standalone.");
        }

        ConcessionOrder order = new ConcessionOrder(concessionIdGen.getAndIncrement(), linked);
        while (true) {
            System.out.println("\nMenu:");
            concessionMenu.values().forEach(item -> System.out.printf("%d: %s - %.2f%n", item.getId(), item.getName(), item.getPrice()));
            int mid = readInt("Enter menu id to add (0 = done): ");
            if (mid == 0) break;
            ConcessionOrder.MenuItem item = concessionMenu.get(mid);
            if (item == null) { System.out.println("Invalid menu id."); continue; }
            int qty = readIntInRange("Quantity: ", 1, 10);
            order.addItem(new ConcessionOrder.ConcessionItem(item.getId(), item.getName(), item.getPrice(), qty));
            System.out.println("Added " + qty + " x " + item.getName());
        }

        if (order.getItems().isEmpty()) {
            System.out.println("No items selected. Aborting.");
            return;
        }

        order.calculateTotals();
        order.printReceipt();
        concessions.put(order.getId(), order);
    }

    private static void displayShowsAvailability() {
        System.out.println("\n--- Shows & Availability ---");
        if (shows.isEmpty()) { System.out.println("No shows scheduled."); return; }
        for (Show s : shows.values()) {
            System.out.println(s.brief());
            s.printSeatAvailability();
            System.out.println();
        }
    }

    // Utilities
    private static boolean processPayment(double amount) {
        System.out.printf("Processing payment of %.2f ... (type 'ok' to simulate success): ", amount);
        String resp = scanner.nextLine().trim();
        if ("ok".equalsIgnoreCase(resp) || "y".equalsIgnoreCase(resp) || "yes".equalsIgnoreCase(resp)) {
            System.out.println("Payment successful.");
            return true;
        } else {
            System.out.println("Payment not confirmed.");
            return false;
        }
    }

    private static String seatListToString(List<Seat> seats) {
        StringJoiner sj = new StringJoiner(", ");
        for (Seat s : seats) sj.add(s.getSeatId());
        return sj.toString();
    }

    private static void displayShowsBrief() {
        System.out.println("Shows:");
        shows.values().forEach(s -> System.out.println(s.getId() + ": " + s.brief()));
    }

    // Input helpers
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String l = scanner.nextLine().trim();
            try { return Integer.parseInt(l); }
            catch (NumberFormatException e) { System.out.println("Enter a valid integer."); }
        }
    }

    private static int readIntInRange(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v < min || v > max) System.out.println("Enter value between " + min + " and " + max);
            else return v;
        }
    }

    private static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String l = scanner.nextLine().trim();
            try { return Double.parseDouble(l); }
            catch (NumberFormatException e) { System.out.println("Enter a valid number."); }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("Input cannot be empty.");
        }
    }

    private static String readPhone(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.matches("\\d{7,15}")) return s;
            System.out.println("Enter 7-15 digits.");
        }
    }

    // Seed data for demo
    private static void seedSampleData() {
        Movie m1 = new Movie(movieIdGen.getAndIncrement(), "Vikram", "Action", 180);
        Movie m2 = new Movie(movieIdGen.getAndIncrement(), "Joe", "Love", 150);
        movies.put(m1.getId(), m1);
        movies.put(m2.getId(), m2);

        Screen sc1 = new Screen(screenIdGen.getAndIncrement(), "IMAX Screen");
        Screen sc2 = new Screen(screenIdGen.getAndIncrement(), "Standard Screen");
        screens.put(sc1.getId(), sc1);
        screens.put(sc2.getId(), sc2);

        Show sh1 = new Show(showIdGen.getAndIncrement(), m1, sc1, "2025-10-02 19:00", 250.0);
        Show sh2 = new Show(showIdGen.getAndIncrement(), m2, sc2, "2025-10-02 17:30", 180.0);
        shows.put(sh1.getId(), sh1);
        shows.put(sh2.getId(), sh2);
        sc1.addShow(sh1);
        sc2.addShow(sh2);

        Customer c = new Customer(customerIdGen.getAndIncrement(), "Chandru", "9876543210");
        customers.put(c.getId(), c);
    }
}
