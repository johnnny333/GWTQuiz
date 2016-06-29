package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.client.SafeHtmlTemplates.Template;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.NoSelectionModel;

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
	private QuestionView.Presenter listener;
	private CellTable<UserScore> cellTableHighScores;
	private Boolean isNameFieldFilled = false;
	private int userScoreLastID;
	private int lastUserScore;
	/** Holds actual user position on the score table. */
	private int actualRecordPosition;

	/** Builds empty High Score Cell Table */
	public HighScoreCellTableViewImpl() {

		cellTableHighScores = new CellTable<UserScore>();
		cellTableHighScores.setWidth("100%", true);
		cellTableHighScores.setStriped(true);
		cellTableHighScores.setCondensed(true);
		//		cellTableHighScores.setBordered(true);
		cellTableHighScores.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);

		//Add position column
		Column<UserScore, Integer> positionColumn = new RowNumberColumn();
		cellTableHighScores.setColumnWidth(positionColumn, 15.0, Unit.PCT);
		cellTableHighScores.addColumn(positionColumn, "Position");

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
						lastUserScore, false, null);

				/*
				 * Issue when user edit record and then deletes it. Empty name field is thus inserted to db.
				 */
				if(value != "") {
					listener.updateUserScore(userScore);
					isNameFieldFilled = true;
				} ;
			}
		});
		cellTableHighScores.setColumnWidth(nameColumn, 58.0, Unit.PCT);
		cellTableHighScores.addColumn(nameColumn, "Player");

		// Add a NumberCell() column to show the user score.
		Column<UserScore, Number> scoreColumn = new Column<UserScore, Number>(new NumberCell()) {
			@Override
			public Integer getValue(UserScore object) {
				return object.score;
			}
		};
		cellTableHighScores.setColumnWidth(scoreColumn, 12.0, Unit.PCT);
		//		scoreColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		cellTableHighScores.addColumn(scoreColumn, "Score");

		// Add a Created at column to show the creation time.
		TextColumn<UserScore> createdAtColumn = new TextColumn<UserScore>() {
			@Override
			public String getValue(UserScore object) {
				return object.createdAt;
			}
		};
		cellTableHighScores.addColumn(createdAtColumn, "Date");

		// Add a selection model to handle user selection.
		final NoSelectionModel<UserScore> selectionModel = new NoSelectionModel<UserScore>();
		cellTableHighScores.setSelectionModel(selectionModel);

		//Add CellTable onto the extended Vertical Panel
		add(cellTableHighScores);
	}

	@Override
	public void setPresenter(QuestionView.Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void fillHighScoreCellTable(final ArrayList<UserScore> result) {

		/* 
		 * Fix issue when user previously provided name but on next game left name field blank 
		 * which resulted in isNameFieldFilled set to true thus displaying old, editable record
		 * on List
		 */
		isNameFieldFilled = false;

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
			if(result.get(i).userScoreID > max) {
				max = result.get(i).userScoreID;
				userScoreLastID = result.get(i).userScoreID;
				lastUserScore = result.get(i).score;
			}
		}
	}

	@Override
	public void deleteEmptyRecord() {
		UserScore userScore = new UserScore(userScoreLastID, "mysteriousPlayer",
				lastUserScore, false, null);
		listener.deleteUserScore(userScore);
	}

	@Override
	public Boolean getIsNameFieldFilled() {
		return isNameFieldFilled;
	}
	
	@Override
	public int getActualRecordPosition() {
		return actualRecordPosition;
	}

/*================================MyTextInputCellClassOverrides================================*/
	
	private static Template template;

	interface Template extends SafeHtmlTemplates {
		@Template("<input type=\"text\" value=\"{0}\" tabindex=\"-1\" placeholder=\"Fill in your name!\" maxlength=\"15\"></input>")
		SafeHtml input(String value);
	}
	/**
	 * Custom TextInputCell class to enable certain cells to be editable in CellTable - 
	 * when 'isThisCellEditable' of UserScore field is set to true, 
	 * <br/>
	 * add placeholder and maxlength attributes to empty input ("placeholder=\"Fill in your name!\"")
	 * <br/> 
	 * and to get position of actual record ('actualRecordPosition' field).
	 * @author jzarewicz
	 */
	public class MyTextInputCell extends TextInputCell {
		
		@Override
		public void render(Context context, String value, SafeHtmlBuilder sb) {

			if(template == null) {
				template = GWT.create(Template.class);
			}

			UserScore object = (UserScore) context.getKey();
			if(object.isEditable) {
				//================== super.render(context, value, sb);

				// Get the view data.
				Object key = context.getKey();
				//Get actual row position
				GWT.log("context.getIndex()" + (context.getIndex() + 1));
				actualRecordPosition = context.getIndex() + 1;
				
				ViewData viewData = getViewData(key);
				if(viewData != null && viewData.getCurrentValue().equals(value)) {
					clearViewData(key);
					viewData = null;
				}

				String s = (viewData != null) ? viewData.getCurrentValue() : value;
				if(s != null) {
					sb.append(template.input(s));
				} else {
					sb.appendHtmlConstant("<input type=\"text\" tabindex=\"-1\"></input>");
				}
				/*================================*/

			} else {
				sb.appendEscaped(value);
			}
		}
	}
/*===========================ENDOF==MyTextInputCellClassOverrides================================*/

	/** Cell numberer. */
	public class RowNumberColumn extends Column<UserScore, Integer> {

		public RowNumberColumn() {
			super(new AbstractCell<Integer>() {
				@Override
				public void render(Context context, Integer o, SafeHtmlBuilder safeHtmlBuilder) {
					safeHtmlBuilder.append(context.getIndex() + 1);
				}
			});
		}

		@Override
		public Integer getValue(UserScore s) {
			return null;
		}
	}
}