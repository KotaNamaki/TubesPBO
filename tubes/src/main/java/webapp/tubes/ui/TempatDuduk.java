package webapp.tubes.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import webapp.tubes.backend.entity.Seat;
import webapp.tubes.backend.entity.TheaterRoom;
import webapp.tubes.backend.repository.SeatRepository;
import webapp.tubes.backend.repository.TheaterRoomRepository;
import webapp.tubes.ui.layout.MainLayout;

@Route(value = "seats", layout = MainLayout.class)
@PageTitle("Tempat Duduk")
public class TempatDuduk extends VerticalLayout {
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private final ComboBox<TheaterRoom> roomComboBox;
    private final Grid<Seat> seatGrid;

    public TempatDuduk(TheaterRoomRepository theaterRoomRepository, SeatRepository seatRepository) {
        this.theaterRoomRepository = theaterRoomRepository;
        this.seatRepository = seatRepository;
        this.roomComboBox = new ComboBox<>("Select Theater Room");
        this.seatGrid = new Grid<>(Seat.class);

        setupLayout();
    }

    private void setupLayout() {
        setSizeFull();
        
        configureRoomComboBox();
        configureSeatGrid();
        
        HorizontalLayout buttonLayout = createButtonLayout();
        
        add(
            new H1("Tempat Duduk"),
            roomComboBox,
            buttonLayout,
            seatGrid
        );
    }

    private HorizontalLayout createButtonLayout() {
        Button addRoomButton = new Button("Tambah Ruang", e -> showRoomDialog());
        Button generateSeatsButton = new Button("Tambah tempat duduk", e -> showGenerateSeatsDialog());
        return new HorizontalLayout(addRoomButton, generateSeatsButton);
    }

    private void configureRoomComboBox() {
        roomComboBox.setItems(theaterRoomRepository.findAll());
        roomComboBox.setItemLabelGenerator(TheaterRoom::getName);
        roomComboBox.addValueChangeListener(e -> updateSeatGrid());
    }

    private void configureSeatGrid() {
        seatGrid.removeAllColumns();
        seatGrid.addColumn(Seat::getSeatNumber).setHeader("Seat Number");
        seatGrid.addColumn(Seat::getSeatType).setHeader("Type");
        seatGrid.addColumn(Seat::isBooked).setHeader("Booked");
        addBookingColumn();
    }

    private void addBookingColumn() {
        seatGrid.addComponentColumn(seat -> {
            Button toggleButton = new Button(seat.isBooked() ? "Release" : "Book");
            toggleButton.addClickListener(e -> toggleSeatBooking(seat));
            return toggleButton;
        });
    }

    private void toggleSeatBooking(Seat seat) {
        seat.setBooked(!seat.isBooked());
        seatRepository.save(seat);
        updateSeatGrid();
    }

    private void updateSeatGrid() {
        if (roomComboBox.getValue() != null) {
            seatGrid.setItems(seatRepository.findByTheaterRoom(roomComboBox.getValue()));
        }
    }

    private void showRoomDialog() {
        Dialog dialog = new Dialog();
        TextField nameField = new TextField("Room Name");
        IntegerField capacityField = new IntegerField("Capacity");
        Button saveButton = createSaveRoomButton(dialog, nameField, capacityField);

        dialog.add(new VerticalLayout(
            new H3("Add New Theater Room"),
            nameField,
            capacityField,
            saveButton
        ));
        dialog.open();
    }

    private Button createSaveRoomButton(Dialog dialog, TextField nameField, IntegerField capacityField) {
        return new Button("Save", e -> {
            TheaterRoom room = new TheaterRoom();
            room.setName(nameField.getValue());
            room.setCapacity(capacityField.getValue());
            theaterRoomRepository.save(room);
            roomComboBox.setItems(theaterRoomRepository.findAll());
            dialog.close();
        });
    }

    private void showGenerateSeatsDialog() {
        if (roomComboBox.getValue() == null) {
            Notification.show("Please select a room first");
            return;
        }

        Dialog dialog = createGenerateSeatsDialog();
        dialog.open();
    }

    private Dialog createGenerateSeatsDialog() {
        Dialog dialog = new Dialog();
        IntegerField rowsField = new IntegerField("Number of Rows");
        IntegerField seatsPerRowField = new IntegerField("Seats per Row");
        ComboBox<Seat.SeatType> typeComboBox = createSeatTypeComboBox();
        Button generateButton = createGenerateButton(dialog, rowsField, seatsPerRowField, typeComboBox);

        dialog.add(new VerticalLayout(
            new H3("Generate Seats for " + roomComboBox.getValue().getName()),
            rowsField,
            seatsPerRowField,
            typeComboBox,
            generateButton
        ));
        return dialog;
    }

    private ComboBox<Seat.SeatType> createSeatTypeComboBox() {
        ComboBox<Seat.SeatType> typeComboBox = new ComboBox<>("Default Seat Type");
        typeComboBox.setItems(Seat.SeatType.values());
        return typeComboBox;
    }

    private Button createGenerateButton(Dialog dialog, IntegerField rowsField, 
            IntegerField seatsPerRowField, ComboBox<Seat.SeatType> typeComboBox) {
        return new Button("Generate", e -> {
            generateSeats(rowsField.getValue(), seatsPerRowField.getValue(), typeComboBox.getValue());
            dialog.close();
        });
    }

    private void generateSeats(int rows, int seatsPerRow, Seat.SeatType defaultType) {
        TheaterRoom room = roomComboBox.getValue();
        seatRepository.deleteAll(seatRepository.findByTheaterRoom(room));

        for (int row = 0; row < rows; row++) {
            char rowChar = (char) ('A' + row);
            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                createSeat(room, rowChar, seatNum, defaultType);
            }
        }

        updateSeatGrid();
        Notification.show("Generated " + (rows * seatsPerRow) + " seats");
    }

    private void createSeat(TheaterRoom room, char rowChar, int seatNum, Seat.SeatType defaultType) {
        Seat seat = new Seat();
        seat.setSeatNumber(rowChar + String.valueOf(seatNum));
        seat.setSeatType(Seat.SeatType.valueOf(defaultType.name()));
        seat.setTheaterRoom(room);
        seatRepository.save(seat);
    }
}