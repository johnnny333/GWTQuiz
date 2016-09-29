/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.constants.Pull;

import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

import pl.johnny.gwtQuiz.client.ui.widgets.PanelWidget;
import pl.johnny.gwtQuiz.shared.Question;

public class AdminViewImpl extends Composite implements AdminView {

	private static AdminViewImplUiBinder uiBinder = GWT.create(AdminViewImplUiBinder.class);

	interface AdminViewImplUiBinder extends UiBinder<Widget, AdminViewImpl> {
	}

	@UiField
	PanelGroup panelGroup;

	@UiField
	Icon refreshIcon;
	@UiField
	HTMLPanel categoriesTableContainer;

	private Presenter listener;
	private String[] categories;

	private PanelWidget[] panelWidgets;

	public AdminViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		buildCategoriesTable();
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setCategories(String[] categories) {

		this.categories = categories;
	}

	@Override
	public void buildAndFillPanelsWithTmpQuestions(ArrayList<Question> tmpQuestion) {

		Alert noQuestionAlert = new Alert("No user questions!");
		noQuestionAlert.addStyleName("text-center");
		noQuestionAlert.setType(AlertType.INFO);
		if(tmpQuestion.size() == 0) {
			panelGroup.clear();
			panelGroup.add(noQuestionAlert);
			return;
		} ;

		panelWidgets = new PanelWidget[tmpQuestion.size()];

		// Avoid doubling widgets...
		panelGroup.clear();

		for(int i = 0; i < panelWidgets.length; i++) {

			panelWidgets[i] = new PanelWidget(listener);
			panelWidgets[i].setUserCategoryListBox(categories, tmpQuestion.get(i).getCategory());
			panelWidgets[i].setHeaderAndIDs(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getQuestion());
			panelWidgets[i].setUserImage(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getImageURL());
			panelWidgets[i].setUserQuestionField(tmpQuestion.get(i).getQuestion());

			for(int j = 0; j < tmpQuestion.get(i).getAnswers().length; j++) {
				panelWidgets[i].setUserAnswer1Field(tmpQuestion.get(i).getAnswer(j));

				switch(j) {
					case 0:
						panelWidgets[i].setUserAnswer1Field(tmpQuestion.get(i).getAnswer(j));
						break;
					case 1:
						panelWidgets[i].setUserAnswer2Field(tmpQuestion.get(i).getAnswer(j));
						break;
					case 2:
						panelWidgets[i].setUserAnswer3Field(tmpQuestion.get(i).getAnswer(j));
						break;
					case 3:
						panelWidgets[i].setUserAnswer4Field(tmpQuestion.get(i).getAnswer(j));
						break;
				}
			}

			panelWidgets[i].setUserCorrectAnsListBox(tmpQuestion.get(i).getCorrectAnswersInt());
			panelWidgets[i].setUserAuthorField(tmpQuestion.get(i).getAuthor());

			panelGroup.add(panelWidgets[i]);
		}
	}

	@Override
	public void refreshPanel() {
		panelGroup.clear();
		listener.fetchAndBuildPanelWithTmpQuestions();
	}

	@UiHandler("refreshIcon")
	void onRefreshIconClicked(ClickEvent e) {
		refreshPanel();
	}

	@Override
	public PanelWidget[] getPanelWidgets() {
		return panelWidgets;
	}
	

	public void buildCategoriesTable() {
		/**
		   * The list of data to display.
		   */
		final List<String> DAYS = Arrays.asList("Sunday", "Monday",
				"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");
		

		// Create a cell that will interact with a value updater.
		TextInputCell inputCell = new TextInputCell();

		// Create a CellList that uses the cell.
		CellList<String> cellList = new CellList<String>(inputCell);
		cellList.addStyleName("categories-table-container");
		
		// Create a list data provider.
	    final ListDataProvider<String> dataProvider = new ListDataProvider<String>();
	    
	    // Add the cellList to the dataProvider.
	    dataProvider.addDataDisplay(cellList);
	    
	    // Get the underlying list from data dataProvider.
	    final List<String> list = dataProvider.getList();
	    
	    list.addAll(DAYS);
	   final String[] cat = new String[1];
	    
	 // Add a selection model to handle user selection.
	    final NoSelectionModel<String> selectionModel = new NoSelectionModel<String>();
	    cellList.setSelectionModel(selectionModel);
	    selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
	      @Override
		public void onSelectionChange(SelectionChangeEvent event) {
	        String selected = selectionModel.getLastSelectedObject();
	        if (selected != null) {
	          cat[0] = selected;
	        }
	      }
	    });
		
		// Create a value updater that will be called when the value in a cell
		// changes.
		ValueUpdater<String> valueUpdater = new ValueUpdater<String>() {
			@Override
			public void update(String newValue) {
				Window.alert("You typed: " + newValue);
			}
		};

		// Add the value updater to the cellList.
		cellList.setValueUpdater(valueUpdater);
		
		// Create a form to add values to the data provider.
	    final TextBox valueBox = new TextBox();
	    valueBox.getElement().getStyle().setMarginTop(10, Unit.PX);
	    valueBox.getElement().getStyle().setMarginBottom(10, Unit.PX);
	    valueBox.getElement().getStyle().setWidth(90.0, Unit.PCT);
	    valueBox.setPlaceholder("Enter new category");
	    
	    //Add new category value on pressed enter key button.
	    valueBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					//Return if no value is provided.
			    	if(valueBox.getText().trim() == "")return;  
			        // Get the value from the text box.
			        String newValue = valueBox.getText();

			        // Add the value to the list. The dataProvider will update the cellList.
			        list.add(newValue);
			        //Clear valueBox after value has been submitted.
			        valueBox.clear();
	           }
			}
		});
	    
	    Button addButton = new Button("Add value", new ClickHandler() {
	      @Override
		public void onClick(ClickEvent event) {
	    	//Return if no value is provided.
	    	if(valueBox.getText().trim() == "")return;  
	        // Get the value from the text box.
	        String newValue = valueBox.getText();

	        // Add the value to the list. The dataProvider will update the cellList.
	        list.add(newValue);
	        //Clear valueBox after value has been submitted.
	        valueBox.clear();
	      }
	    });
	    
	    Button removeButton = new Button(" X ", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				list.remove(cat[0]);
			}
		});
	    removeButton.setPull(Pull.RIGHT);
		
		// Add it to the root panel.
		categoriesTableContainer.add(cellList);

		//Add controls
		categoriesTableContainer.add(valueBox);
		categoriesTableContainer.add(addButton);
		categoriesTableContainer.add(removeButton);
	}
}