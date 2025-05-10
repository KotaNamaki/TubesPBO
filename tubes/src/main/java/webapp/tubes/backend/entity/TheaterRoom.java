package webapp.tubes.backend.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TheaterRoom {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int capacity;

    public TheaterRoom() {
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TheaterRoom(Long id, String name, int capacity, List<Seat> seats) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
        this.seats = seats;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @OneToMany(mappedBy = "theaterRoom", cascade = CascadeType.ALL)
    private List<Seat> seats = new ArrayList<>();

    // Getters and setters
}
