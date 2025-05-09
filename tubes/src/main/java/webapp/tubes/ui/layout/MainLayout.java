package webapp.tubes.ui.layout;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import webapp.tubes.ui.MainMovie;
import webapp.tubes.ui.MainView;

@Route("app-layout")
@Layout

public class MainLayout extends AppLayout {
    public MainLayout() {
        DrawerToggle toggle = new DrawerToggle();
        H1 title = new H1("BIBD Theater CRM");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");
        HorizontalLayout header = new HorizontalLayout(toggle, title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);

        Tabs tabs = new Tabs(
                new Tab(new RouterLink("main", MainView.class)),
                new Tab(new RouterLink("film", MainMovie.class))
        );
        tabs.setOrientation(Tabs.Orientation.VERTICAL);

        addToDrawer(tabs);

    }

}
