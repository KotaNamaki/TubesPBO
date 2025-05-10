package webapp.tubes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webapp.tubes.backend.entity.Booking;
import webapp.tubes.backend.entity.Movie;
import webapp.tubes.backend.entity.Person;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPerson(Person person);
    List<Booking> findByMovie(Movie movie);
}
