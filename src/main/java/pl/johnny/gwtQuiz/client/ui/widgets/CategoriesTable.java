package pl.johnny.gwtQuiz.client.ui.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.Tooltip;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.gwtbootstrap3.client.ui.constants.Pull;
import org.gwtbootstrap3.client.ui.gwt.ButtonCell;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.NoSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;

public class CategoriesTable extends Composite {

	public CategoriesTable() {
		
		//first make a list to store the cells, you want to combine
				final ArrayList<HasCell> cellsArrayList = new ArrayList<HasCell>();

				//then define the cells and add them to the list    
				HasCell textInputCell = new HasCell(){
				    @Override
				    public Cell getCell() {
				        return new TextInputCell();
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
				cellsArrayList.add(textInputCell);


				HasCell buttonCell = new HasCell(){

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
//				cellsArrayList.add(buttonCell);
				
				/**
				   * The list of data to display.
				   */
				final List<String> DAYS = Arrays.asList("Sunday", "Monday",
						"Tuesday", "Wednesday", "Thursday", "Friday", "Saturday");

				// Create a CellList that uses the cell.
				CellList<String> cellList = new CellList<String>(new CompositeCell(cellsArrayList));
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
			    valueBox.getElement().getStyle().setWidth(100.0, Unit.PCT);
			    valueBox.setPlaceholder("Enter new category");
			    
			    //Add new category value on pressed enter key button.
			    valueBox.addKeyDownHandler(new KeyDownHandler() {
					
					@Override
					public void onKeyDown(KeyDownEvent event) {
						if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
							//Return if no value is provided.
					    	if(valueBox.getText().trim() == "" || valueBox.getText().trim().length() > 20)return;  
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
			    	if(valueBox.getText().trim() == "" || valueBox.getText().trim().length() > 20)return;  
			        // Get the value from the text box.
			        String newValue = valueBox.getText();

			        // Add the value to the list. The dataProvider will update the cellList.
			        list.add(newValue);
			        //Clear valueBox after value has been submitted.
			        valueBox.clear();
			      }
			    });
			    
			    Button removeButton = new Button("", new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						list.remove(cat[0]);
					}
				});
			    removeButton.setPull(Pull.RIGHT);
			    removeButton.setIcon(IconType.TRASH);
			    
			   
				
				// Add it to the flowPanel
			    FlowPanel container = new FlowPanel();
			    container.add(cellList);

				//Add controls
			    container.add(valueBox);
			    container.add(addButton);
			    container.add(removeButton);
			    
			    //Wrap container by Composite.
			    initWidget(container);
	}
}