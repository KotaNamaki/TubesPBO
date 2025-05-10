package webapp.tubes.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import webapp.tubes.backend.entity.Seat;
import webapp.tubes.backend.entity.TheaterRoom;
import webapp.tubes.backend.repository.SeatRepository;
import webapp.tubes.backend.repository.TheaterRoomRepository;

@Route("seats")
public class SeatManagementView extends VerticalLayout {

    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private ComboBox<TheaterRoom> roomComboBox = new ComboBox<>("Select Theater Room");
    private Grid<Seat> seatGrid = new Grid<>(Seat.class);

    public SeatManagementView(TheaterRoomRepository theaterRoomRepository, SeatRepository seatRepository) {
        this.theaterRoomRepository = theaterRoomRepository;
        this.seatRepository = seatRepository;

        setSizeFull();
        add(new H1("Seat Management"));

        configureRoomComboBox();
        configureSeatGrid();

        Button addRoomButton = new Button("Add New Room", e -> showRoomDialog());
        Button generateSeatsButton = new Button("Generate Seats", e -> showGenerateSeatsDialog());

        HorizontalLayout buttonLayout = new HorizontalLayout(addRoomButton, generateSeatsButton);
        add(roomComboBox, buttonLayout, seatGrid);
    }

    private void configureRoomComboBox() {
        roomComboBox.setItems(theaterRoomRepository.findAll());
        roomComboBox.setItemLabelGenerator(TheaterRoom::getName);
        roomComboBox.addValueChangeListener(e -> updateSeatGrid());
    }

    private void configureSeatGrid() {
        seatGrid.removeAllColumns();
        seatGrid.addColumn(Seat::getSeatNumber).setHeader("Seat Number");
        seatGrid.addColumn(seat -> seat.getType().toString()).setHeader("Type");
        seatGrid.addColumn(Seat::isBooked).setHeader("Booked");

        seatGrid.addComponentColumn(seat -> {
            Button toggleButton = new Button(seat.isBooked() ? "Release" : "Book");
            toggleButton.addClickListener(e -> {
                seat.setBooked(!seat.isBooked());
                seatRepository.save(seat);
                updateSeatGrid();
            });
            return toggleButton;
        });
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

        Button saveButton = new Button("Save", e -> {
            TheaterRoom room = new TheaterRoom();
            room.setName(nameField.getValue());
            room.setCapacity(capacityField.getValue());
            theaterRoomRepository.save(room);
            roomComboBox.setItems(theaterRoomRepository.findAll());
            dialog.close();
        });

        dialog.add(new VerticalLayout(
                new H3("Add New Theater Room"),
                nameField,
                capacityField,
                saveButton
        ));
        dialog.open();
    }

    private void showGenerateSeatsDialog() {
        if (roomComboBox.getValue() == null) {
            Notification.show("Please select a room first");
            return;
        }

        Dialog dialog = new Dialog();
        IntegerField rowsField = new IntegerField("Number of Rows");
        IntegerField seatsPerRowField = new IntegerField("Seats per Row");
        ComboBox<Seat.SeatType> typeComboBox = new ComboBox<>("Default Seat Type");
        typeComboBox.setItems(Seat.SeatType.values());

        Button generateButton = new Button("Generate", e -> {
            // Validate all required fields
            if (rowsField.getValue() == null || seatsPerRowField.getValue() == null || typeComboBox.getValue() == null) {
                Notification.show("Please fill in all fields");
                return;
            }
            
            if (rowsField.getValue() <= 0 || seatsPerRowField.getValue() <= 0) {
                Notification.show("Number of rows and seats must be positive");
                return;
            }
            
            TheaterRoom room = roomComboBox.getValue();
            seatRepository.deleteAll(seatRepository.findByTheaterRoom(room));

            int rows = rowsField.getValue();
            int seatsPerRow = seatsPerRowField.getValue();
            Seat.SeatType defaultType = typeComboBox.getValue();

            for (int row = 0; row < rows; row++) {
                char rowChar = (char) ('A' + row);
                for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                    Seat seat = new Seat();
                    seat.setSeatNumber(rowChar + String.valueOf(seatNum));
                    seat.setType(defaultType);
                    seat.setTheaterRoom(room);
                    seatRepository.save(seat);
                }
            }

            updateSeatGrid();
            dialog.close();
            Notification.show("Generated " + (rows * seatsPerRow) + " seats");
        });

        dialog.add(new VerticalLayout(
                new H3("Generate Seats for " + roomComboBox.getValue().getName()),
                rowsField,
                seatsPerRowField,
                typeComboBox,
                generateButton
        ));
        dialog.open();
    }
}