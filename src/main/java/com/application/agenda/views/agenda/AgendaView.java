package com.application.agenda.views.agenda;

import com.application.agenda.data.entity.Agenda;
import com.application.agenda.data.service.AgendaService;
import com.application.agenda.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Agenda")
@Route(value = "agenda/:agendaID?/:action?(edit)", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@PermitAll
public class AgendaView extends Div implements BeforeEnterObserver {

    private final String AGENDA_ID = "agendaID";
    private final String AGENDA_EDIT_ROUTE_TEMPLATE = "agenda/%s/edit";

    private final Grid<Agenda> grid = new Grid<>(Agenda.class, false);

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField celular;
    private DatePicker dataNascimento;
    private DatePicker dataAgendamento;
    private DateTimePicker horaInicio;
    private DateTimePicker horaFim;
    private TextField idCliente;
    private TextField observacaoAgenda;
    private TextField valorTotal;
    private TextField statusAgenda;
    private TextField idAgenda;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Agenda> binder;

    private Agenda agenda;

    private final AgendaService agendaService;

    @Autowired
    public AgendaView(AgendaService agendaService) {
        this.agendaService = agendaService;
        addClassNames("agenda-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("celular").setAutoWidth(true);
        grid.addColumn("dataNascimento").setAutoWidth(true);
        grid.addColumn("dataAgendamento").setAutoWidth(true);
        grid.addColumn("horaInicio").setAutoWidth(true);
        grid.addColumn("horaFim").setAutoWidth(true);
        grid.addColumn("idCliente").setAutoWidth(true);
        grid.addColumn("observacaoAgenda").setAutoWidth(true);
        grid.addColumn("valorTotal").setAutoWidth(true);
        grid.addColumn("statusAgenda").setAutoWidth(true);
        grid.addColumn("idAgenda").setAutoWidth(true);
        grid.setItems(query -> agendaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(AGENDA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(AgendaView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Agenda.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idCliente).withConverter(new StringToIntegerConverter("Only numbers are allowed"))
                .bind("idCliente");
        binder.forField(idAgenda).withConverter(new StringToUuidConverter("Invalid UUID")).bind("idAgenda");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.agenda == null) {
                    this.agenda = new Agenda();
                }
                binder.writeBean(this.agenda);
                agendaService.update(this.agenda);
                clearForm();
                refreshGrid();
                Notification.show("Agenda details stored.");
                UI.getCurrent().navigate(AgendaView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the agenda details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> agendaId = event.getRouteParameters().get(AGENDA_ID).map(UUID::fromString);
        if (agendaId.isPresent()) {
            Optional<Agenda> agendaFromBackend = agendaService.get(agendaId.get());
            if (agendaFromBackend.isPresent()) {
                populateForm(agendaFromBackend.get());
            } else {
                Notification.show(String.format("The requested agenda was not found, ID = %s", agendaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(AgendaView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        celular = new TextField("Celular");
        dataNascimento = new DatePicker("Data Nascimento");
        dataAgendamento = new DatePicker("Data Agendamento");
        horaInicio = new DateTimePicker("Hora Inicio");
        horaInicio.setStep(Duration.ofSeconds(1));
        horaFim = new DateTimePicker("Hora Fim");
        horaFim.setStep(Duration.ofSeconds(1));
        idCliente = new TextField("Id Cliente");
        observacaoAgenda = new TextField("Observacao Agenda");
        valorTotal = new TextField("Valor Total");
        statusAgenda = new TextField("Status Agenda");
        idAgenda = new TextField("Id Agenda");
        formLayout.add(firstName, lastName, email, celular, dataNascimento, dataAgendamento, horaInicio, horaFim,
                idCliente, observacaoAgenda, valorTotal, statusAgenda, idAgenda);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(save, cancel);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(Agenda value) {
        this.agenda = value;
        binder.readBean(this.agenda);

    }
}
