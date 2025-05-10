package webapp.tubes.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import webapp.tubes.backend.entity.Movie;
import webapp.tubes.backend.repository.MovieRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Route("movie")
public class Film extends VerticalLayout {
    private final MovieRepository movieRepository;
    private final Grid<Movie> grid = new Grid<>(Movie.class);
    private final TextField title = new TextField("Movie Title");
    private final TextField director = new TextField("Director");
    private final IntegerField duration = new IntegerField("Durasi (menit)");
    private final DateTimePicker timeShow = new DateTimePicker("Waktu tayang");
    private final TextField genre = new TextField("Genre");
    private final TextField searchField = new TextField();

    public Film(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
        configureSearch();
        configureGrid();
        form();

        add(searchField, grid);
        setSizeFull();
    }

    private void configureSearch() {
        searchField.setPlaceholder("Cari film");
        searchField.setWidth("50%");
        searchField.addValueChangeListener(e -> {
            String searchTerm = searchField.getValue();
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                updateGrid();
            } else {
                grid.setItems(movieRepository.findByTitleContainingIgnoreCase(e.getValue()));
            }
        });
    }

    private void configureGrid() {
        grid.removeAllColumns();
        grid.addColumn(Movie::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(Movie::getDirector).setHeader("Director");
        grid.addColumn(Movie::getDurationMinutes).setHeader("Duration");
        grid.addColumn(movie -> movie.getShowTime()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")))
                .setHeader("Waktu Tayang");
        grid.addColumn(Movie::getGenre).setHeader("Genre");

        grid.addComponentColumn(movie -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> {
                try {
                    movieRepository.delete(movie);
                    updateGrid();
                    Notification.show("Movie deleted");
                } catch (Exception ex) {
                    Notification.show("Error deleting movie: " + ex.getMessage(),
                            3000, Notification.Position.MIDDLE);
                }
            });
            return deleteButton;
        });
        updateGrid();
    }

    private void form() {
        Button button = new Button("Simpan Film");
        HorizontalLayout formLayout = new HorizontalLayout(
                title, director, duration, timeShow, genre, button
        );
        formLayout.setAlignItems(Alignment.BASELINE);

        button.addClickListener(e -> saveMovie());

        add(formLayout);
    }

    private void saveMovie() {
        try {
            // Validate all fields before saving
            if (title.isEmpty() || title.getValue().trim().isEmpty()) {
                Notification.show("Judul film harus diisi", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (director.isEmpty() || director.getValue().trim().isEmpty()) {
                Notification.show("Nama sutradara harus diisi", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (duration.isEmpty() || duration.getValue() <= 0) {
                Notification.show("Durasi harus lebih dari 0 menit", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (timeShow.isEmpty()) {
                Notification.show("Waktu tayang harus diisi", 3000, Notification.Position.MIDDLE);
                return;
            }

            if (genre.isEmpty() || genre.getValue().trim().isEmpty()) {
                Notification.show("Genre film harus diisi", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Additional validation for showtime
            if (timeShow.getValue().isBefore(LocalDateTime.now())) {
                Notification.show("Waktu tayang harus di masa depan", 3000, Notification.Position.MIDDLE);
                return;
            }

            // Create and save the movie
            Movie movie = new Movie(
                    title.getValue().trim(),
                    director.getValue().trim(),
                    duration.getValue(),
                    timeShow.getValue(),
                    genre.getValue().trim()
            );

            movieRepository.save(movie);
            updateGrid();
            Notification.show("Film berhasil disimpan!", 3000, Notification.Position.MIDDLE);
            clearForm();

        } catch (Exception e) {
            Notification.show("Gagal menyimpan film: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void clearForm() {
        title.clear();
        director.clear();
        duration.clear();
        timeShow.clear();
        genre.clear();
    }

    private void updateGrid() {
        grid.setItems(movieRepository.findAll());
    }
}