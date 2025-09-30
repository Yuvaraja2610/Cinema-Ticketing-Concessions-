package JavaAssign;

public class Seat {
    public enum Status { AVAILABLE, HELD, BOOKED }

    private final String seatId;
    private Status status;

    public Seat(String seatId) {
        if (seatId == null || seatId.trim().isEmpty()) throw new IllegalArgumentException("Seat ID required");
        this.seatId = seatId.trim().toUpperCase();
        this.status = Status.AVAILABLE;
    }

    public String getSeatId() { return seatId; }
    public boolean isAvailable() { return status == Status.AVAILABLE; }
    public boolean isHeld() { return status == Status.HELD; }
    public boolean isBooked() { return status == Status.BOOKED; }

    // mark temporarily during selection
    public void hold() {
        if (!isAvailable()) throw new IllegalStateException("Seat not available to hold");
        this.status = Status.HELD;
    }

    public void releaseHold() {
        if (isHeld()) this.status = Status.AVAILABLE;
    }

    // finalize booking
    public void book() {
        if (!isHeld() && !isAvailable()) throw new IllegalStateException("Seat not in bookable state");
        this.status = Status.BOOKED;
    }

    // release booked seat (cancellation)
    public void release() {
        this.status = Status.AVAILABLE;
    }

    @Override
    public String toString() {
        return seatId + " (" + status + ")";
    }
}

