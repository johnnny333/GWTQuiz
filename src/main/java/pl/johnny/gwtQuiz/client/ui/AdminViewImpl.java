/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.gwt.CellTable;

import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextInputCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

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
		if (tmpQuestion.size() == 0) {
			panelGroup.clear();
			panelGroup.add(noQuestionAlert);
			return;
		}
		;

		panelWidgets = new PanelWidget[tmpQuestion.size()];

		// Avoid doubling widgets...
		panelGroup.clear();

		for (int i = 0; i < panelWidgets.length; i++) {

			panelWidgets[i] = new PanelWidget(listener);
			panelWidgets[i].setUserCategoryListBox(categories, tmpQuestion.get(i).getCategory());
			panelWidgets[i].setHeaderAndIDs(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getQuestion());
			panelWidgets[i].setUserImage(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getImageURL());
			panelWidgets[i].setUserQuestionField(tmpQuestion.get(i).getQuestion());

			for (int j = 0; j < tmpQuestion.get(i).getAnswers().length; j++) {
				panelWidgets[i].setUserAnswer1Field(tmpQuestion.get(i).getAnswer(j));

				switch (j) {
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
		 * A simple data type that represents a contact with a unique ID.
		 */
		class Contact {
			private int nextId = 0;

			private final int id;
			private String name;

			public Contact(String name) {
				nextId++;
				this.id = nextId;
				this.name = name;
			}
		}

		/**
		 * The list of data to display.
		 */
		final List<Contact> CONTACTS = Arrays.asList(new Contact("John"), new Contact("Joe"), new Contact("George"));

		/**
		 * The key provider that allows us to identify Contacts even if a field
		 * changes. We identify contacts by their unique ID.
		 */
		final ProvidesKey<Contact> KEY_PROVIDER = new ProvidesKey<Contact>() {
			@Override
			public Object getKey(Contact item) {
				return item.id;
			}
		};

		// Create a CellTable with a key provider.
		final CellTable<Contact> table = new CellTable<Contact>(KEY_PROVIDER);
		table.setStriped(true);

		// Add a text input column to edit the name.
		final TextInputCell nameCell = new TextInputCell();
		Column<Contact, String> nameColumn = new Column<Contact, String>(nameCell) {
			@Override
			public String getValue(Contact object) {
				// Return the name as the value of this column.
				return object.name;
			}
		};
		table.addColumn(nameColumn, "Name");

		// Add a field updater to be notified when the user enters a new name.
		nameColumn.setFieldUpdater(new FieldUpdater<Contact, String>() {
			@Override
			public void update(int index, Contact object, String value) {
				// Inform the user of the change.
				Window.alert("You changed the name of " + object.name + " to " + value);

				// Push the changes into the Contact. At this point, you could
				// send an
				// asynchronous request to the server to update the database.
				object.name = value;

				// Redraw the table with the new data.
				table.redraw();
			}
		});

		// Push the data into the widget.
		table.setRowData(CONTACTS);

		// Add it to the root panel.
		categoriesTableContainer.add(table);
	}
}