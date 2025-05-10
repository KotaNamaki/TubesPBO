package webapp.tubes.ui;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import webapp.tubes.backend.entity.Seat;
import webapp.tubes.backend.entity.TheaterRoom;
import webapp.tubes.backend.repository.SeatRepository;
import webapp.tubes.backend.repository.TheaterRoomRepository;

public class SeatManagementView extends VerticalLayout {
    private final TheaterRoomRepository theaterRoomRepository;
    private final SeatRepository seatRepository;
    private ComboBox<TheaterRoom> roomComboBox = new ComboBox<>("Pilih Ruang Theater");
    private Grid<Seat> grid = new Grid<>(Seat.class);
    HorizontalLayout sideways = new HorizontalLayout();
    VerticalLayout Vertical = new VerticalLayout();
    H1 title = new H1();
    TextField text = new TextField();
    H2 subtitle = new H2();
    Paragraph paragraph = new Paragraph();


    public SeatManagementView(TheaterRoomRepository theaterRoomRepository, SeatRepository seatRepository) {
        this.theaterRoomRepository = theaterRoomRepository;
        this.seatRepository = seatRepository;
        setSizeFull();
        title();


    }

    private void title() {


    }

}
