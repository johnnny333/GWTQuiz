package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;

import pl.johnny.gwtQuiz.client.ui.QuestionView.Presenter;
import pl.johnny.gwtQuiz.shared.UserScore;

/** 
 * TODO Describe flow of building and filling this Cell
 * @author jzarewicz
 *
 */
public class HighScoreCellTableViewImpl extends VerticalPanel implements HighScoreCellTableView {

	/**
	* The list of data to display.
	*/
	//	private static final List<UserScore> CONTACTS = Arrays.asList(
	//			new UserScore("John", "5", true),
	//			new UserScore("Joe", "4", false),
	//			new UserScore("George", "4", false));
	private Presenter listener;
	private CellTable<UserScore> cellTableHighScores;
	private Boolean isNameFieldFilled = false;
	private int userScoreLastID;
	private int lastUserScore;

	/** Builds empty High Score Cell Table */
	public HighScoreCellTableViewImpl() {
		
		cellTableHighScores = new CellTable<UserScore>();
		cellTableHighScores.setWidth("100%", true);
		cellTableHighScores.setStriped(true);
		//		cellTableHighScores.setCondensed(true);
		//		cellTableHighScores.setBordered(true);
		cellTableHighScores.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		// Add a text column to show the name.
		Column<UserScore, String> nameColumn = new Column<UserScore, String>(new MyTextInputCell()) {
			@Override
			public String getValue(UserScore object) {
				return object.userDisplay;
			}
		};

		nameColumn.setFieldUpdater(new FieldUpdater<UserScore, String>() {

			@Override
			public void update(int index, UserScore object, String value) {
				GWT.log("changed value " + value);

				UserScore userScore = new UserScore(userScoreLastID, value,
						lastUserScore, false);

				listener.updateUserScore(userScore);
				isNameFieldFilled = true;
			}
		});

		cellTableHighScores.addColumn(nameColumn, "Player");

		// Add a number column to show the address.
		Column<UserScore, Number> addressColumn = new Column<UserScore, Number>(new NumberCell()) {
			@Override
			public Integer getValue(UserScore object) {
				return object.score;
			}
		};
		cellTableHighScores.addColumn(addressColumn, "Score");

		// Add a selection model to handle user selection.
		final NoSelectionModel<UserScore> selectionModel = new NoSelectionModel<UserScore>();
		cellTableHighScores.setSelectionModel(selectionModel);

		//Add CellTable onto the extended Vertical Panel
		add(cellTableHighScores);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
	
	@Override
	public void fillHighScoreCellTable(final ArrayList<UserScore> result) {

		// Set the total row count. This isn't strictly necessary,
		// but it affects paging calculations, so its good habit to
		// keep the row count up to date.
		cellTableHighScores.setRowCount(result.size(), true);

		// Push the data into the widget.
		cellTableHighScores.setRowData(0, result);
				
		/* 
		 * Find last (highest id) record from result - which is our not (yet) named record fields - 
		 * and get its values
		 */
		int max = Integer.MIN_VALUE;
		for(int i = 0; i < result.size(); i++) {
		      if(result.get(i).userScoreID > max ) {
		    	  max = result.get(i).userScoreID;
		    	  userScoreLastID = result.get(i).userScoreID;
		    	  lastUserScore = result.get(i).score;
		      }
		}
	}
	
	@Override
	public void fillEmptyRecord(){
		UserScore userScore = new UserScore(userScoreLastID, "mysteriousPlayer", 
				lastUserScore, false);
		listener.updateUserScore(userScore);
	}

	@Override
	public Boolean getIsNameFieldFilled() {
		return isNameFieldFilled;
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
			if(object.isEditable) {
				super.render(context, value, sb);
			} else {
				sb.appendEscaped(value);
			}
		}
	}
}