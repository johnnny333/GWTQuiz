package pl.johnny.gwtQuiz.client.ui;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;

public class HighScoreCellTableView extends VerticalPanel {

	/**
	* The list of data to display.
	*/
	private static final List<Contact> CONTACTS = Arrays.asList(
			new Contact("John", new Date(80, 4, 12), "5", true),
			new Contact("Joe", new Date(85, 2, 22), "4", false),
			new Contact("George", new Date(46, 6, 6), "4", false));

	public HighScoreCellTableView() {

		CellTable<Contact> cellTableHighScores = new CellTable<Contact>();
		cellTableHighScores.setWidth("100%", true);
		cellTableHighScores.setStriped(true);
		//		cellTableHighScores.setCondensed(true);
		//		cellTableHighScores.setBordered(true);
				cellTableHighScores.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Add a text column to show the name.
		Column<Contact, String> nameColumn = new Column<Contact, String>(new MyTextInputCell()) {
			@Override
			public String getValue(Contact object) {
				return object.name;
			}
		};
		
		nameColumn.setFieldUpdater( new FieldUpdater<Contact, String>(){

			@Override
			public void update(int index, Contact object, String value) {
				GWT.log("changed value " + value);	
			}

			 
			});
		
		cellTableHighScores.addColumn(nameColumn, "Player");

		// Add a text column to show the address.
		TextColumn<Contact> addressColumn = new TextColumn<Contact>() {
			@Override
			public String getValue(Contact object) {
				return object.address;
			}
		};
		cellTableHighScores.addColumn(addressColumn, "Score");

		// Add a selection model to handle user selection.
				final NoSelectionModel<Contact> selectionModel = new NoSelectionModel<Contact>();
				cellTableHighScores.setSelectionModel(selectionModel);

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		cellTableHighScores.setRowCount(CONTACTS.size(), true);

		// Push the data into the widget.
		cellTableHighScores.setRowData(0, CONTACTS);

		add(cellTableHighScores);
	}

	public class MyTextInputCell extends TextInputCell {
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			Contact object = (Contact) context.getKey();
			if(object.isThisCellEditable) {
				 super.render(context,value,sb);
			} else {
				sb.appendEscaped(value); // our some other HTML. Whatever you want.
			}
		}
	}
}