package com.application.agenda.views.produtos;

import com.application.agenda.data.entity.Produtos;
import com.application.agenda.data.service.ProdutosService;
import com.application.agenda.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
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

@PageTitle("Produtos")
@Route(value = "produtos/:produtosID?/:action?(edit)", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ProdutosView extends Div implements BeforeEnterObserver {

    private final String PRODUTOS_ID = "produtosID";
    private final String PRODUTOS_EDIT_ROUTE_TEMPLATE = "produtos/%s/edit";

    private final Grid<Produtos> grid = new Grid<>(Produtos.class, false);

    private TextField codigo;
    private TextField descricao;
    private TextField valor;
    private TextField tempoMedio;
    private TextField tipo;
    private Checkbox statusProduto;
    private TextField idProduto;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");

    private final BeanValidationBinder<Produtos> binder;

    private Produtos produtos;

    private final ProdutosService produtosService;

    @Autowired
    public ProdutosView(ProdutosService produtosService) {
        this.produtosService = produtosService;
        addClassNames("produtos-view");

        // Create UI
        SplitLayout splitLayout = new SplitLayout();

        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);

        add(splitLayout);

        // Configure Grid
        grid.addColumn("codigo").setAutoWidth(true);
        grid.addColumn("descricao").setAutoWidth(true);
        grid.addColumn("valor").setAutoWidth(true);
        grid.addColumn("tempoMedio").setAutoWidth(true);
        grid.addColumn("tipo").setAutoWidth(true);
        LitRenderer<Produtos> statusProdutoRenderer = LitRenderer.<Produtos>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", statusProduto -> statusProduto.isStatusProduto() ? "check" : "minus")
                .withProperty("color",
                        statusProduto -> statusProduto.isStatusProduto()
                                ? "var(--lumo-primary-text-color)"
                                : "var(--lumo-disabled-text-color)");

        grid.addColumn(statusProdutoRenderer).setHeader("Status Produto").setAutoWidth(true);

        grid.addColumn("idProduto").setAutoWidth(true);
        grid.setItems(query -> produtosService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(PRODUTOS_EDIT_ROUTE_TEMPLATE, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(ProdutosView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(Produtos.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.forField(codigo).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("codigo");
        binder.forField(valor).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("valor");
        binder.forField(tipo).withConverter(new StringToIntegerConverter("Only numbers are allowed")).bind("tipo");
        binder.forField(idProduto).withConverter(new StringToUuidConverter("Invalid UUID")).bind("idProduto");

        binder.bindInstanceFields(this);

        cancel.addClickListener(e -> {
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                if (this.produtos == null) {
                    this.produtos = new Produtos();
                }
                binder.writeBean(this.produtos);
                produtosService.update(this.produtos);
                clearForm();
                refreshGrid();
                Notification.show("Produtos details stored.");
                UI.getCurrent().navigate(ProdutosView.class);
            } catch (ValidationException validationException) {
                Notification.show("An exception happened while trying to store the produtos details.");
            }
        });

    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<UUID> produtosId = event.getRouteParameters().get(PRODUTOS_ID).map(UUID::fromString);
        if (produtosId.isPresent()) {
            Optional<Produtos> produtosFromBackend = produtosService.get(produtosId.get());
            if (produtosFromBackend.isPresent()) {
                populateForm(produtosFromBackend.get());
            } else {
                Notification.show(String.format("The requested produtos was not found, ID = %s", produtosId.get()),
                        3000, Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                refreshGrid();
                event.forwardTo(ProdutosView.class);
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
        codigo = new TextField("Codigo");
        descricao = new TextField("Descricao");
        valor = new TextField("Valor");
        tempoMedio = new TextField("Tempo Medio");
        tipo = new TextField("Tipo");
        statusProduto = new Checkbox("Status Produto");
        idProduto = new TextField("Id Produto");
        formLayout.add(codigo, descricao, valor, tempoMedio, tipo, statusProduto, idProduto);

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

    private void populateForm(Produtos value) {
        this.produtos = value;
        binder.readBean(this.produtos);

    }
}
