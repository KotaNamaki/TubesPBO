package webapp.tubes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webapp.tubes.backend.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String title);
    List<Movie> findByGenre(String genre);
    List<Movie> findByTitleContainingIgnoreCaseAndGenre(String title, String genre);


}
