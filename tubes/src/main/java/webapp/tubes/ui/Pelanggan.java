package webapp.tubes.ui;


import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import webapp.tubes.backend.entity.Person;
import webapp.tubes.backend.repository.PersonRepository;
import webapp.tubes.ui.layout.MainLayout;

@Route(value = "", layout = MainLayout.class)
@CssImport("./styles/style-shared.css")
@PageTitle("Admin Page")
public class Pelanggan extends VerticalLayout {
    private final PersonRepository repository;
    HorizontalLayout sideways = new HorizontalLayout();
    VerticalLayout Vertical = new VerticalLayout();
    H1 title = new H1();
    TextField text = new TextField();
    H2 subtitle = new H2();
    Paragraph paragraph = new Paragraph();
    private final Grid<Person> grid = new Grid<>(Person.class);
    private final TextField name = new TextField("Full name");
    private final TextField PhoneNumber = new TextField("Phone Number");
    private final TextField email = new TextField("Email");
    private final Button save = new Button("Save");
    private final TextField searchField = new TextField();
    private final Button searchButton = new Button("Search");

    //Essentials

    @Autowired
    public Pelanggan(PersonRepository repository) {
        this.repository = repository;

        setSizeFull();
        getStyle().set("background", "#fffff");
        createTitle();
        configureGrid();

        createForm();
        search();

        add(grid); // Add grid to layout
        updateGrid();
    }

    private void configureGrid() {
        grid.addClassName("person-grid");


        grid.setWidth("100%");
        grid.setAllRowsVisible(true);
        grid.getStyle().set("background-color", "transparent");
        grid.setColumns("id", "name", "phoneNumber", "email");
        grid.addComponentColumn(person -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> {
                try {
                    repository.delete(person);
                    updateGrid();
                    Notification.show("Person deleted");
                } catch (Exception ex) {
                    Notification.show("Error deleting person: " + ex.getMessage());
                }
            });
                    return deleteButton;
        });

        updateGrid();

        //grid.asSingleSelect().addValueChangeListener(e ->{
        //    if(e.getValue() != null){
        //        Person person = e.getValue();
        //        name.setValue(person.getName());
        //        PhoneNumber.setValue(person.getPhoneNumber());
        //        email.setValue(person.getEmail());

       //     }});
    }

    private void createForm() {
        HorizontalLayout form = new HorizontalLayout(name, PhoneNumber, email, save);
        form.setAlignItems(Alignment.BASELINE);

        save.addClickListener(e -> saveCustomer());

        add(form);
    }

    private void saveCustomer() {
        if (isValidInput()) {
            try {
                Person person = new Person(name.getValue(), PhoneNumber.getValue(), email.getValue());
                repository.save(person);
                updateGrid();
                clearForm();
            } catch (Exception ex) {
                Notification.show("Error saving person: " + ex.getMessage());
            }
        }
    }

    private boolean isValidInput() {
        return !name.isEmpty() && PhoneNumber.getValue().matches("^\\d+$") &&
                !email.isEmpty() && email.getValue().matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void clearForm() {
        name.clear();
        PhoneNumber.clear();
        email.clear();
    }
    private void search(){
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidth("100%");
        searchField.setWidth("100%");
        searchField.setPlaceholder("Search by name, phone, or email");
        searchField.setClearButtonVisible(true);

        Button searchButton = new Button("Search", e -> {
            String searchTerm = searchField.getValue();
            if (searchTerm == null || searchTerm.isEmpty()) {
                updateGrid(); // Show all if search is empty
            } else {
                // Search in each field separately
                grid.setItems(repository.findByNameContainingIgnoreCaseOrPhoneNumberContainingOrEmailContainingIgnoreCase(
                        searchTerm, searchTerm, searchTerm));
            }
        });

        // Add a "clear" shortcut when pressing Escape
        searchField.addKeyPressListener(Key.ESCAPE, e -> {
            searchField.clear();
            updateGrid();
        });

        searchLayout.add(searchField, searchButton);
        add(searchLayout);
    }
    private void updateGrid() {
        grid.setItems(repository.findAll());
    }
    private void createTitle(){
        title.addClassName("title");
        subtitle.addClassName("subtitle");
        title.setText("Pelanggan");
        subtitle.setText("Page ini digunakan untuk menambah data atau menghapus data pelanggan");
        add(title);
        add(subtitle);
    }
}
