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
    private static final String PRICE_FORMAT = "Rp %.2f";
    private static final double VIP_PRICE = 145000;
    private static final double HANDICAP_PRICE = 30000;
    private static final double REGULAR_PRICE = 60000;

    private final PersonRepository personRepository;
    private final MovieRepository movieRepository;
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;

    private final ComboBox<Person> personComboBox = new ComboBox<>("Pelanggan");
    private final ComboBox<Movie> movieComboBox = new ComboBox<>("Film");
    private final ComboBox<TheaterRoom> theaterRoomComboBox = new ComboBox<>("Ruang Theater");
    private final Grid<Seat> availableSeatsGrid = new Grid<>();
    private final List<Seat> selectedSeats = new ArrayList<>();
    private final Span totalSpan = new Span("Total Bayar: Rp 0");
    private final Button bookButton = new Button("Konfirmasi pesanan");

    public BookingTiket(PersonRepository personRepository, MovieRepository movieRepository,
                       TheaterRoomRepository theaterRoomRepository, SeatRepository seatRepository,
                       BookingRepository bookingRepository) {
        this.personRepository = personRepository;
        this.movieRepository = movieRepository;
        this.theaterRoomRepository = theaterRoomRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;

        setupLayout();
        configureComponents();
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
        availableSeatsGrid.asMultiSelect().addValueChangeListener(e -> {
            selectedSeats.clear();
            selectedSeats.addAll(e.getValue());
            updateTotal();
        });
        bookButton.addClickListener(e -> handleBooking());
    }

    private void handleBooking() {
        if (!isValidBooking()) {
            showErrorNotification();
            return;
        }

        Booking booking = createBooking();
        updateSeatsStatus();
        bookingRepository.save(booking);
        showSuccessNotification(booking);
        resetForm();
    }

    private boolean isValidBooking() {
        return !personComboBox.isEmpty() && !movieComboBox.isEmpty() && !selectedSeats.isEmpty();
    }

    private void showErrorNotification() {
        Notification.show("Pilih pelanggan, film, dan tempat duduk terlebih dahulu",
                3000, Notification.Position.MIDDLE);
    }

    private Booking createBooking() {
        Booking booking = new Booking();
        booking.setPerson(personComboBox.getValue());
        booking.setMovie(movieComboBox.getValue());
        booking.setSeats(selectedSeats);
        booking.setTotalPrice(calculateTotalPrice());
        booking.setBookingTime(LocalDateTime.now());
        return booking;
    }

    private void updateSeatsStatus() {
        selectedSeats.forEach(seat -> {
            seat.setBooked(true);
            seatRepository.save(seat);
        });
    }

    private void showSuccessNotification(Booking booking) {
        Notification.show("Pesanan Berhasil dipesan! Pesanan ID: " + booking.getId(),
                5000, Notification.Position.MIDDLE);
    }

    private void resetForm() {
        personComboBox.clear();
        movieComboBox.clear();
        theaterRoomComboBox.clear();
        availableSeatsGrid.asMultiSelect().deselectAll();
        availableSeatsGrid.setItems(Collections.emptyList());
        totalSpan.setText("Total Bayar: Rp 0");
    }

    private void updateAvailableSeats() {
        var theaterRoom = theaterRoomComboBox.getValue();
        availableSeatsGrid.setItems(theaterRoom != null
                ? seatRepository.findByTheaterRoomAndIsBookedFalse(theaterRoom)
                : Collections.emptyList());
    }

    private void updateTotal() {
        double total = calculateTotalPrice();
        totalSpan.setText(String.format("Total Bayar: " + PRICE_FORMAT, total));
    }

    private String formatSeatPrice(Seat seat) {
        double price = getPriceForSeatType(seat.getSeatType());
        return String.format(PRICE_FORMAT, price);
    }

    private double calculateTotalPrice() {
        return selectedSeats.stream()
                .mapToDouble(seat -> getPriceForSeatType(seat.getSeatType()))
                .sum();
    }

    private double getPriceForSeatType(Seat.SeatType seatType) {
        return switch (seatType) {
            case VIP -> VIP_PRICE;
            case HANDICAP -> HANDICAP_PRICE;
            default -> REGULAR_PRICE;
        };
    }
}