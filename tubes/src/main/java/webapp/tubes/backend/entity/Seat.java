package webapp.tubes.backend.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String seatNumber;

    @Enumerated(EnumType.STRING)  // This stores the enum as a string in the database
    private SeatType seatType;

    private boolean isBooked = false;

    @ManyToOne
    @JoinColumn(name = "theater_room_id")
    private TheaterRoom theaterRoom;

    public Seat(Long id, String seatNumber, SeatType seatType, boolean isBooked, TheaterRoom theaterRoom) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = isBooked;
        this.theaterRoom = theaterRoom;
    }

    public Seat() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public TheaterRoom getTheaterRoom() {
        return theaterRoom;
    }

    public void setTheaterRoom(TheaterRoom theaterRoom) {
        this.theaterRoom = theaterRoom;
    }



    public enum SeatType { REGULAR, VIP, HANDICAP }


}
