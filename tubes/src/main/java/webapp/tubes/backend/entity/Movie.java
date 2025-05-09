package webapp.tubes.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Entity
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Director is required")
    @Column(nullable = false)
    private String director;

    @Positive(message = "Duration must be positive")
    @Column(nullable = false)
    private int durationMinutes;

    @Future(message = "Show time must be in the future")
    @Column(nullable = false)
    private LocalDateTime showTime;

    @NotBlank(message = "Genre is required")
    @Column(nullable = false)
    private String genre;

    // Constructors
    public Movie() {
        // Default constructor required by JPA
    }

    public Movie(String title, String director, int durationMinutes,
                 LocalDateTime showTime, String genre) {
        this.title = title;
        this.director = director;
        this.durationMinutes = durationMinutes;
        this.showTime = showTime;
        this.genre = genre;
    }

    // Getters and Setters


    public Movie(String value, String value1, Integer value2, LocalDateTime value3, String value4) {
        this.title = value;
        this.director = value1;
        this.durationMinutes = value2;
        this.showTime = value3;
        this.genre = value4;
    }


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}