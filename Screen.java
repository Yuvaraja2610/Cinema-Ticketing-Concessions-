package JavaAssign;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Screen {
    private final int id;
    private final String name;
    private final List<Show> shows = new ArrayList<>();

    public Screen(int id, String name) {
        this.id = id;
        if (name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Screen name required");
        this.name = name.trim();
    }

    public int getId() { return id; }
    public String getName() { return name; }

    public void addShow(Show show) {
        if (show == null) throw new IllegalArgumentException("Show required");
        shows.add(show);
    }

    public void removeShow(Show show) {
        shows.remove(show);
    }

    public List<Show> getShows() {
        return Collections.unmodifiableList(shows);
    }

    @Override
    public String toString() {
        return id + " - " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Screen)) return false;
        Screen screen = (Screen) o;
        return id == screen.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

