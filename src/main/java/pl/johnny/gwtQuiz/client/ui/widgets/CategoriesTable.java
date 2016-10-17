package pl.johnny.gwtQuiz.client.ui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ButtonType;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

import pl.johnny.gwtQuiz.client.ui.AdminView.Presenter;

public class CategoriesTable extends Composite {

	public CategoriesTable(String[][] categories, final Presenter listener) {

		// first make a list to store the cells, you want to combine
		final ArrayList<HasCell> cellsArrayList = new ArrayList<HasCell>();

		// Create a list data provider.
		final ListDataProvider<String> dataProvider = new ListDataProvider<String>();

		// Get the underlying list from data dataProvider.
		final List<String> dataProviderList = dataProvider.getList();

		// then define the cells and add them to the list
		HasCell textInputCell = new HasCell() {
			@Override
			public Cell getCell() {
				return new TextInputCell();
			}

			@Override
			public FieldUpdater<String, String> getFieldUpdater() {
				return new FieldUpdater<String, String>() {

					@Override
					public void update(int index, String object, String value) {
						String oldCategoryValue = dataProviderList.get(index);
						// Update record on database.
						listener.updateCategory(SafeHtmlUtils.htmlEscape(value.trim()), oldCategoryValue, index, dataProviderList);

					}
				};
			}

			@Override
			public Object getValue(Object object) {
				return object;
			}
		};
		cellsArrayList.add(textInputCell);

		HasCell buttonCell = new HasCell() {

			@Override
			public Cell getCell() {
				return new ButtonCell();
			}

			@Override
			public FieldUpdater getFieldUpdater() {
				return null;
			}

			@Override
			public Object getValue(Object object) {
				return object;
			}
		};
		// cellsArrayList.add(buttonCell);

		/**
		 * The list of data to display.
		 */
		String[] categoriesList = new String[categories.length];

		for (int i = 0; i < categories.length; i++) {
			categoriesList[i] = categories[i][1];
		}
		final List<String> categoriesListForCellList = Arrays.asList(categoriesList);

		// Create a CellList that uses the cell.
		CellList<String> cellList = new CellList<String>(new CompositeCell(cellsArrayList));
		cellList.addStyleName("categories-table-container");

		// Add the cellList to the dataProvider.
		dataProvider.addDataDisplay(cellList);

		dataProviderList.addAll(categoriesListForCellList);

		/** We use array here as a hack to avoid enclosing type error */
		final String[] selectedCategory = new String[1];

		// Add a selection model to handle user selection.
		final NoSelectionModel<String> selectionModel = new NoSelectionModel<String>();
		cellList.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				String selected = selectionModel.getLastSelectedObject();
				if (selected != null) {
					// Assing to array NOT to string - avoid enclosing type
					// constraint.
					selectedCategory[0] = selected;
				}
			}
		});

		// Create a value updater that will be called when the value in a cell
		// changes.
		ValueUpdater<String> valueUpdater = new ValueUpdater<String>() {
			@Override
			public void update(String newValue) {
				// GWT.log("You typed: " + newValue);
			}
		};

		// Add the value updater to the cellList.
		cellList.setValueUpdater(valueUpdater);

		// Create a form to add values to the data provider.
		final TextBox valueBox = new TextBox();
		valueBox.getElement().getStyle().setMarginTop(10, Unit.PX);
		valueBox.getElement().getStyle().setMarginBottom(10, Unit.PX);
		valueBox.getElement().getStyle().setWidth(100.0, Unit.PCT);
		valueBox.setPlaceholder("Enter new category");
		valueBox.setTitle("To add press either \"Enter\" key or \"Add Category\" button");

		// Add new category value on pressed enter key button.
		valueBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					// Return if no value is provided.
					if (valueBox.getText().trim() == "" || valueBox.getText().trim().length() > 20)
						return;
					// Get the value from the text box.
					String newValue = valueBox.getText();

					// Add the value to the list. The dataProvider will update
					// the cellList.
					dataProviderList.add(newValue);
					// Clear valueBox after value has been submitted.
					valueBox.clear();
				}
			}
		});

		Button addButton = new Button("Add Category", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String sanitizedValue = SafeHtmlUtils.htmlEscape(valueBox.getText().trim());
				
				if (sanitizedValue == "" || sanitizedValue.length() > 20)
					return; 
				
				listener.addCategory(sanitizedValue, dataProviderList);
				valueBox.clear();
			}
		});

		// Modal settings.
		final Modal modal = new Modal();
		modal.setVisible(false);
		modal.setClosable(false);
		modal.setFade(true);
		modal.setTitle("Deletion problem");
		ModalBody modalBody = new ModalBody();
		Label modalBodyText = new Label(
				"Selected category is used by one of the questions and thus cannot be deleted.");
		modalBody.add(modalBodyText);
		modal.add(modalBody);

		ModalFooter modalFooter = new ModalFooter();
		Button okBtn = new Button("OK");
		okBtn.setType(ButtonType.PRIMARY);
		okBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				modal.hide();
			}
		});
		modalFooter.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		modalFooter.add(okBtn);
		modal.add(modalFooter);

		Button removeButton = new Button("", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Delete selected category from database.
				listener.removeCategory(selectedCategory[0], dataProviderList, modal);
			}
		});

		removeButton.setPull(Pull.RIGHT);
		removeButton.setIcon(IconType.TRASH);

		// Add it to the flowPanel
		final FlowPanel container = new FlowPanel();
		container.add(cellList);

		// Add controls
		container.add(valueBox);
		container.add(addButton);
		container.add(removeButton);
		container.add(modal);

		// Wrap container by Composite.
		initWidget(container);
	}
}