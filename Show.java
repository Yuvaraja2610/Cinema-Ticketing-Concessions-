package JavaAssign;
import java.util.*;

public class Show {
    private final int id;
    private final Movie movie;
    private final Screen screen;
    private final String dateTime; // simple string representation
    private final double baseFare;

    // seat layout & bookings
    private final Map<String, Seat> seats = new LinkedHashMap<>();
    private final List<Booking> bookings = new ArrayList<>();

    // default layout (can be customized later)
    private static final char[] DEFAULT_ROWS = {'A','B','C','D'};
    private static final int DEFAULT_COLS = 8;

    public Show(int id, Movie movie, Screen screen, String dateTime, double baseFare) {
        this.id = id;
        if (movie == null) throw new IllegalArgumentException("Movie required");
        if (screen == null) throw new IllegalArgumentException("Screen required");
        if (dateTime == null || dateTime.trim().isEmpty()) throw new IllegalArgumentException("Date/time required");
        if (baseFare < 0) throw new IllegalArgumentException("Base fare cannot be negative");

        this.movie = movie;
        this.screen = screen;
        this.dateTime = dateTime.trim();
        this.baseFare = baseFare;
        initDefaultSeats();
    }

    private void initDefaultSeats() {
        for (char r : DEFAULT_ROWS) {
            for (int c = 1; c <= DEFAULT_COLS; c++) {
                String id = "" + r + c;
                seats.put(id, new Seat(id));
            }
        }
    }

    public int getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public String getDateTime() { return dateTime; }
    public double getBaseFare() { return baseFare; }

    public Optional<Seat> getSeatById(String seatId) {
        if (seatId == null) return Optional.empty();
        return Optional.ofNullable(seats.get(seatId.toUpperCase()));
    }

    public Collection<Seat> getAllSeats() {
        return Collections.unmodifiableCollection(seats.values());
    }

    public void addBooking(Booking b) {
        bookings.add(b);
    }

    public void removeBooking(Booking b) {
        bookings.remove(b);
    }

    public String brief() {
        return String.format("Show %d - %s @ %s (Screen: %s, Fare: %.2f)", id, movie.getTitle(), dateTime, screen.getName(), baseFare);
    }

    public void printSeatAvailability() {
        System.out.println("Seat Layout for Show " + id + " (" + movie.getTitle() + " @ " + dateTime + ")");
        for (char r : DEFAULT_ROWS) {
            StringBuilder row = new StringBuilder();
            for (int c = 1; c <= DEFAULT_COLS; c++) {
                String sid = "" + r + c;
                Seat s = seats.get(sid);
                char ch = s.isAvailable() ? 'O' : (s.isHeld() ? 'H' : 'X');
                row.append(String.format("%s[%c] ", sid, ch));
            }
            System.out.println(row.toString());
        }
        System.out.println("Legend: O=Available, X=Booked, H=Held");
    }
}
