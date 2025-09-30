package JavaAssign;
import java.util.Objects;

public class Movie {
    private final int id;
    private String title;
    private String genre;
    private int durationMinutes;

    public Movie(int id, String title, String genre, int durationMinutes) {
        this.id = id;
        setTitle(title);
        setGenre(genre);
        setDurationMinutes(durationMinutes);
    }

    public int getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title required");
        this.title = title.trim();
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) {
        this.genre = (genre == null ? "" : genre.trim());
    }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration must be positive");
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String toString() {
        return String.format("%d - %s (%s, %dm)", id, title, genre, durationMinutes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Movie)) return false;
        Movie movie = (Movie) o;
        return id == movie.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

