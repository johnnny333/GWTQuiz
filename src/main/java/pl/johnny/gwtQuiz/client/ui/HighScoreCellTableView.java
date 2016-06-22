package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

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

import pl.johnny.gwtQuiz.client.ui.QuestionView.Presenter;
import pl.johnny.gwtQuiz.shared.UserScore;

/** 
 * View class in charge of creating high scores cell table from given models.
 * Integral part of QuestionView - separated for convenience and readability.
 * <br/><br/>
 * Building table cell is kinda complex due to the nature of asynchronomous RPC call made to 
 * retrieve high score list from server.<br/>
 * It goes...
 * <br/>
 * <ol >
 * <li>
 * This class is instatianted in QuestionViewImpl with listener parameter, which allows to 
 * call QuestionActivity.getUserScores(this).
 * </li>
 *  <br/>
 * <li>
 * After said call is made - on success - the result of RPC call is handed to already 
 * instatianted highScoreCellTableView.buildHighScoreCellTable(ArrayList&lt;UserScore> result)
 * available from passed argument, where said result parameter from RPC call is used as a model to build and populate our cell list.
 * </li>
 * </ol>
 * @author jzarewicz
 *
 */
public class HighScoreCellTableView extends VerticalPanel {

	/**
	* The list of data to display.
	*/
//	private static final List<UserScore> CONTACTS = Arrays.asList(
//			new UserScore("John", "5", true),
//			new UserScore("Joe", "4", false),
//			new UserScore("George", "4", false));
	
	
	//Package access modifiers
	HighScoreCellTableView(Presenter listener) {
		listener.getUserScores(this);
	}

	public void buildHighScoreCellTable(ArrayList<UserScore> result){
		CellTable<UserScore> cellTableHighScores = new CellTable<UserScore>();
		cellTableHighScores.setWidth("100%", true);
		cellTableHighScores.setStriped(true);
		//		cellTableHighScores.setCondensed(true);
		//		cellTableHighScores.setBordered(true);
		cellTableHighScores.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Add a text column to show the name.
		Column<UserScore, String> nameColumn = new Column<UserScore, String>(new MyTextInputCell()) {
			@Override
			public String getValue(UserScore object) {
				return object.playerDisplay;
			}
		};

		nameColumn.setFieldUpdater(new FieldUpdater<UserScore, String>() {

			@Override
			public void update(int index, UserScore object, String value) {
				GWT.log("changed value " + value);
			}
		});

		cellTableHighScores.addColumn(nameColumn, "Player");

		// Add a text column to show the address.
		TextColumn<UserScore> addressColumn = new TextColumn<UserScore>() {
			@Override
			public String getValue(UserScore object) {
				return object.score;
			}
		};
		cellTableHighScores.addColumn(addressColumn, "Score");

		// Add a selection model to handle user selection.
		final NoSelectionModel<UserScore> selectionModel = new NoSelectionModel<UserScore>();
		cellTableHighScores.setSelectionModel(selectionModel);

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		cellTableHighScores.setRowCount(result.size(), true);

		// Push the data into the widget.
		cellTableHighScores.setRowData(0, result);
		add(cellTableHighScores);
	}
	
	/**
	 * Custom TextInputCell class to enable certain cells to be editable in CellTable - 
	 * when 'isThisCellEditable' of UserScore field is set to true.
	 * @author jzarewicz
	 */
	public class MyTextInputCell extends TextInputCell {
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {
			UserScore object = (UserScore) context.getKey();
			if(object.isThisCellEditable) {
				super.render(context, value, sb);
			} else {
				sb.appendEscaped(value);
			}
		}
	}
}