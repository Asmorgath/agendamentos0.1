package com.application.agenda.views.clientes;

import com.application.agenda.data.entity.Pessoa;
import com.application.agenda.data.service.PessoaService;
import com.application.agenda.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.converter.StringToUuidConverter;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("Clientes")
@Route(value = "clientes/:pessoaID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ClientesView extends Div implements BeforeEnterObserver {

    private final String PESSOA_ID = "pessoaID";
    private final String PESSOA_EDIT_ROUTE_TEMPLATE = "clientes/%s/edit";

    private final Grid<Pessoa> grid = new Grid<>(Pessoa.class, false);

    private TextField primeiroNome;
    private TextField ultimoNome;
    private TextField email;
    private TextField celular;
    private DatePicker dataNascimento;
    private Checkbox status;
    private TextField tipoPessoa;
    private TextField idPessoa;
    private TextField codigo;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Pessoa> binder;

    private Pessoa pessoa;

    private final PessoaService pessoaService;

    @Autowired
    public ClientesView(PessoaService pessoaService) {
        this.pessoaService = pessoaService;
        addClassNames("clientes-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("primeiroNome").setAutoWidth(true);
        grid.addColumn("ultimoNome").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("celular").setAutoWidth(true);
        grid.addColumn("dataNascimento").setAutoWidth(true);
        LitRenderer<Pessoa> statusRenderer = LitRenderer.<Pessoa>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", status -> status.isStatus() ? "check" : "minus").withProperty("color",
                        status -> status.isStatus()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(statusRenderer).setHeader("Status").setAutoWidth(true);

        grid.addColumn("tipoPessoa").setAutoWidth(true);
        grid.addColumn("idPessoa").setAutoWidth(true);
        grid.addColumn("codigo").setAutoWidth(true);
        grid.setItems(query -> pessoaService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PESSOA_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ClientesView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Pessoa.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(idPessoa).withConverter(new StringToUuidConverter("Invalid UUID")).bind("idPessoa");
        binder.forField(codigo).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("codigo");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.pessoa == null) {
                    this.pessoa = new Pessoa();
                }
                binder.writeBean(this.pessoa);
                pessoaService.update(this.pessoa);
                clearForm();
                refreshGrid();
                Notification.show("Pessoa details stored.");
                UI.getCurrent().navigate(ClientesView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the pessoa details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> pessoaId = event.getRouteParameters().get(PESSOA_ID).map(UUID::fromString);
        if (pessoaId.isPresent()) {
            Optional<Pessoa> pessoaFromBackend = pessoaService.get(pessoaId.get());
            if (pessoaFromBackend.isPresent()) {
                populateForm(pessoaFromBackend.get());
            } else {
                Notification.show(String.format("The requested pessoa was not found, ID = %s", pessoaId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ClientesView.class);
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
        primeiroNome = new TextField("Primeiro Nome");
        ultimoNome = new TextField("Ultimo Nome");
        email = new TextField("Email");
        celular = new TextField("Celular");
        dataNascimento = new DatePicker("Data Nascimento");
        status = new Checkbox("Status");
        tipoPessoa = new TextField("Tipo Pessoa");
        idPessoa = new TextField("Id Pessoa");
        codigo = new TextField("Codigo");
        formLayout.add(primeiroNome, ultimoNome, email, celular, dataNascimento, status, tipoPessoa, idPessoa, codigo);

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

    private void populateForm(Pessoa value) {
        this.pessoa = value;
        binder.readBean(this.pessoa);

    }
}
