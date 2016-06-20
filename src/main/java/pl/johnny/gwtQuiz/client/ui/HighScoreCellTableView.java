package pl.johnny.gwtQuiz.client.ui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class HighScoreCellTableView extends VerticalPanel {

	/**
	* The list of data to display.
	*/
	private static final List<Contact> CONTACTS = Arrays.asList(
			new Contact("John", new Date(80, 4, 12), "123 Fourth Avenue"),
			new Contact("Joe", new Date(85, 2, 22), "22 Lance Ln"),
			new Contact("George", new Date(46, 6, 6), "1600 Pennsylvania Avenue"));

	public HighScoreCellTableView() {

		CellTable<Contact> cellTableHighScores = new CellTable<Contact>();
		cellTableHighScores.setWidth("100%", true);
		cellTableHighScores.setStriped(true);
//		cellTableHighScores.setCondensed(true);
//		cellTableHighScores.setBordered(true);
		cellTableHighScores.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);

		// Add a text column to show the name.
		Column<Contact, String> nameColumn = new Column<Contact, String>(new EditTextCell()) {
			@Override
			public String getValue(Contact object) {
				return object.name;
			}
		};
		cellTableHighScores.addColumn(nameColumn, "Name");

		// Add a date column to show the birthday.
		Column<Contact, Date> dateColumn = new Column<Contact, Date>(new DateCell()) {
			@Override
			public Date getValue(Contact object) {
				return object.birthday;
			}
		};
		cellTableHighScores.addColumn(dateColumn, "Birthday");

		// Add a text column to show the address.
		TextColumn<Contact> addressColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact object) {
				return object.address;
			}
		};
		cellTableHighScores.addColumn(addressColumn, "Address");

		// Add a selection model to handle user selection.
		final SingleSelectionModel<Contact> selectionModel = new SingleSelectionModel<Contact>();
		cellTableHighScores.setSelectionModel(selectionModel);
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				Contact selected = selectionModel.getSelectedObject();
				if(selected != null) {
					GWT.log("You selected: " + selected.name);
				}
			}
		});

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		cellTableHighScores.setRowCount(CONTACTS.size(), true);

		// Push the data into the widget.
		cellTableHighScores.setRowData(0, CONTACTS);
	
		add(cellTableHighScores);
	}
}
