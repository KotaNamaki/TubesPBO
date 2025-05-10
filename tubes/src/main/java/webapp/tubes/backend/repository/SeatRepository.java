package webapp.tubes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webapp.tubes.backend.entity.Seat;
import webapp.tubes.backend.entity.TheaterRoom;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheaterRoomAndisBookedFalse(TheaterRoom room);
    List<Seat> findByTheaterRoom(TheaterRoom room);

}

