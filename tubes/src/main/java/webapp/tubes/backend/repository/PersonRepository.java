package webapp.tubes.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webapp.tubes.backend.entity.Person;

import java.util.List;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByNameContainingIgnoreCase(String name);
    List<Person> findByPhoneNumberContainingIgnoreCase(String phoneNumber);
    List<Person> findByEmailContainingIgnoreCase(String email);
    List<Person> findByNameContainingIgnoreCaseAndPhoneNumberContainingIgnoreCaseAndEmailContainingIgnoreCase(String name, String phoneNumber, String email);

    List<Person> findByNameContainingIgnoreCaseOrPhoneNumberContainingOrEmailContainingIgnoreCase(String name, String phone, String email);
}

