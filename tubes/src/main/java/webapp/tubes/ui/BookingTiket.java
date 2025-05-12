package webapp.tubes.ui;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import webapp.tubes.backend.entity.*;
import webapp.tubes.backend.repository.*;
import webapp.tubes.ui.layout.MainLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route(value = "booking", layout = MainLayout.class)
@PageTitle("Booking")
public class BookingTiket extends VerticalLayout {
    // Repository
    private final PersonRepository personRepository;
    private final MovieRepository movieRepository;
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    // Repository

    private ComboBox<Person> personComboBox = new ComboBox<>("Pelanggan");
    private ComboBox<Movie> movieComboBox = new ComboBox<>("Film");
    private ComboBox<TheaterRoom> theaterRoomComboBox = new ComboBox<>("Ruang Theater");
    private Grid<Seat> availabeSeatsGrid = new Grid<>();
    private List<Seat> selectedSeats = new ArrayList<>();
    private Span totalSpan = new Span("Total Bayar: ");
    private final Button bookButton = new Button("Konfirmasi pesanan");

    public BookingTiket(PersonRepository personRepository, MovieRepository movieRepository, TheaterRoomRepository theaterRoomRepository, SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.personRepository = personRepository;
        this.movieRepository = movieRepository;
        this.theaterRoomRepository = theaterRoomRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;

        initializeView();
        configureComponent();
        setupEventListeners();
    }

    private void setupLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        HorizontalLayout selectionLayout = new HorizontalLayout(personComboBox, movieComboBox, theaterRoomComboBox);
        HorizontalLayout buttonLayout = new HorizontalLayout(totalSpan, bookButton);
        selectionLayout.setSpacing(true);
        buttonLayout.setSpacing(true);

        add(
            new H1("Pembelian Tiket Film"),
            selectionLayout,
            availableSeatsGrid,
            buttonLayout
        );
    }

    private void configureComponents() {
        configureComboBoxes();
        configureGrid();
        bookButton.setThemeName("primary");
    }

    private void configureComboBoxes() {
        // Person ComboBox
        personComboBox.setItems(personRepository.findAll());
        personComboBox.setItemLabelGenerator(p -> p.getName() + " (" + p.getEmail() + ")");
        personComboBox.setWidthFull();

        // Movie ComboBox
        movieComboBox.setItems(movieRepository.findAll());
        movieComboBox.setItemLabelGenerator(Movie::getTitle);
        movieComboBox.setWidthFull();

        // Theater Room ComboBox
        theaterRoomComboBox.setItems(theaterRoomRepository.findAll());
        theaterRoomComboBox.setItemLabelGenerator(TheaterRoom::getName);
        theaterRoomComboBox.setWidthFull();
    }

    private void configureGrid() {
        availableSeatsGrid.removeAllColumns();
        availableSeatsGrid.addColumn(Seat::getSeatNumber).setHeader("Tempat Duduk").setAutoWidth(true);
        availableSeatsGrid.addColumn(seat -> seat.getSeatType().toString()).setHeader("Tipe").setAutoWidth(true);
        availableSeatsGrid.addColumn(this::formatSeatPrice).setHeader("Harga").setAutoWidth(true);
        availableSeatsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        availableSeatsGrid.setWidthFull();
    }

    private void setupEventListeners() {
        theaterRoomComboBox.addValueChangeListener(e -> updateAvailableSeats());
        availabeSeatsGrid.asMultiSelect().addValueChangeListener(e ->{
            selectedSeats = new ArrayList<>(e.getValue());
            updateTotal();
        });
        bookButton.addClickListener(e -> createBooking());
    }

    private void createBooking() {
        if (personComboBox.isEmpty() || movieComboBox.isEmpty() || selectedSeats.isEmpty()) {
            Notification.show("Pilih pelanggan, film, dan tempat duduk terlebih dahulu", 3000, Notification.Position.MIDDLE);
            return;
        }
        Booking booking = new Booking();
        booking.setPerson(personComboBox.getValue());
        booking.setMovie(movieComboBox.getValue());
        booking.setSeats(selectedSeats);
        booking.setTotalPrice(getTotalPrice());
        booking.setBookingTime(LocalDateTime.now());

        selectedSeats.forEach(seat -> {
            seat.setBooked(true);
            seatRepository.save(seat);
        });

        bookingRepository.save(booking);

        Notification.show("Pesanann Berhasil dipesan! Pesanan ID: " + booking.getId(), 5000, Notification.Position.MIDDLE);

        //Reset Form
        personComboBox.clear();
        movieComboBox.clear();
        theaterRoomComboBox.clear();
        availabeSeatsGrid.asMultiSelect().deselectAll();
        availabeSeatsGrid.setItems(Collections.emptyList());
        totalSpan.setText("Total Bayar: Rp 0");

    }

    private void updateTotal() {
        double total = getTotalPrice();
        totalSpan.setText("Total Bayar: Rp " + String.format("%.2f", total));

    }
    private void updateAvailableSeats() {
        if (theaterRoomComboBox.getValue() != null){
            availabeSeatsGrid.setItems(seatRepository.findByTheaterRoomAndIsBookedFalse(theaterRoomComboBox.getValue()));
        }else {
            availabeSeatsGrid.setItems(Collections.emptyList());
        }
    }

    private void configureComponent() {
        //Combo boxes
        personComboBox.setItems(personRepository.findAll());
        personComboBox.setItemLabelGenerator(p -> p.getName() + " (" + p.getEmail() + ") ");
        personComboBox.setWidthFull();

        movieComboBox.setItems(movieRepository.findAll());
        movieComboBox.setItemLabelGenerator(Movie::getTitle);
        movieComboBox.setWidthFull();

        theaterRoomComboBox.setItems(theaterRoomRepository.findAll());
        theaterRoomComboBox.setItemLabelGenerator(TheaterRoom::getName);
        theaterRoomComboBox.setWidthFull();
        //ComboBoxes

        //GridConfig
        availabeSeatsGrid.removeAllColumns();
        availabeSeatsGrid.addColumn(Seat::getSeatNumber).setHeader("Tempat Duduk").setAutoWidth(true);
        availabeSeatsGrid.addColumn(seat -> seat.getSeatType().toString()).setHeader("Tipe").setAutoWidth(true);
        availabeSeatsGrid.addColumn(this::getSeatPrice).setHeader("Harga").setAutoWidth(true);
        availabeSeatsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        availabeSeatsGrid.setWidthFull();

        //Button Config
        bookButton.setThemeName("primary");
    }

    private String getSeatPrice(Seat seat) {
        return switch (seat.getSeatType()) {
            case VIP -> "Rp 145.000";
            case HANDICAP -> "Rp 30.000";
            default -> "Rp 60.000";
        };
    }

    private double getTotalPrice() {
        return selectedSeats.stream().mapToDouble(seat -> switch (seat.getSeatType()){
            case VIP -> 145000;
            case HANDICAP -> 30000;
            default -> 60000;
        }).sum();
    }

    private void initializeView() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H1("Pembelian Tiket Film"));
        HorizontalLayout selectionLayout = new HorizontalLayout(personComboBox, movieComboBox, theaterRoomComboBox);
        selectionLayout.setSpacing(true);

        HorizontalLayout buttonLayout = new HorizontalLayout(totalSpan, bookButton);
        buttonLayout.setSpacing(true);
        add(selectionLayout, availabeSeatsGrid, buttonLayout);

    }


}
