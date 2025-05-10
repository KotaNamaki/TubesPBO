package webapp.tubes.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import jakarta.transaction.Transactional;
import webapp.tubes.backend.entity.Booking;
import webapp.tubes.backend.entity.Seat;
import webapp.tubes.backend.repository.BookingRepository;
import webapp.tubes.ui.layout.MainLayout;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@Transactional

public class BookingList extends VerticalLayout {
    private final BookingRepository bookingRepository;
    private Grid<Booking> grid = new Grid<>(Booking.class);

    public BookingList(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;

        setSizeFull();
        add(new H1("Booking list"));
        configureGrid();
        add(grid);
        updateGrid();
    }

    private void updateGrid() {
        grid.setItems(bookingRepository.findAll());
    }

    private void configureGrid() {
        grid.removeAllColumns();

        grid.addColumn(booking -> booking.getPerson().getName()).setHeader("Pelanggan");
        grid.addColumn(booking -> booking.getMovie().getTitle()).setHeader("Film");
        grid.addColumn(booking ->
                booking.getBookingTime().format(DateTimeFormatter.ofPattern("dd MMM, yyyy HH:mm"))
        ).setHeader("Waktu Tayang");

        grid.addColumn(booking ->
                String.format("$%.2f", booking.getTotalPrice())
        ).setHeader("Total");

        grid.addColumn(booking ->
                booking.getSeats().stream()
                        .map(Seat::getSeatNumber)
                        .collect(Collectors.joining(", "))
        ).setHeader("Tempat Duduk");

        grid.addComponentColumn(booking -> {
            Button cancelButton = new Button("Cancel booking", VaadinIcon.TRASH.create());
            cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            cancelButton.addClickListener(e -> cancelBooking(booking));
            return cancelButton;
        });
    }

    private void cancelBooking(Booking booking) {
        booking.getSeats().forEach(seat -> {
            seat.setBooked(false);
        });

        bookingRepository.delete(booking);
        updateGrid();
        Notification.show("Booking cancelled");

    }



}
