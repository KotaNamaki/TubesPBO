package webapp.tubes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webapp.tubes.backend.entity.TheaterRoom;

public interface TheaterRoomRepository extends JpaRepository<TheaterRoom, Long> {

}
