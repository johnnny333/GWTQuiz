package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.ListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.ui.widgets.UploadWidget;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;
	
//	@UiField PanelCollapse panelCollapse;
	@UiField HTMLPanel imageWidget; 
	@UiField ListBox categoryListBox;
	@UiField ListBox correctAnsSelect;


	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		//Add image widget to this view
		imageWidget.add(new UploadWidget());
		
		//Set first ListBox.listItem disabled so it acts as a placeholder.
		correctAnsSelect.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		correctAnsSelect.setItemSelected(0, true);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
	
	@Override
	public void setCategories(String categories) {
		//Set first ListBox.listItem disabled so it acts as a placeholder.
		categoryListBox.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		categoryListBox.addItem(categories);
	}
}