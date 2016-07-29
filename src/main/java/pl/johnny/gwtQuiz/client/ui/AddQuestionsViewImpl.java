package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.ListBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

import pl.johnny.gwtQuiz.client.ui.widgets.UploadWidget;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;

	@UiField HTMLPanel imageWidget;
	@UiField ListBox categoryListBox;
	@UiField ListBox correctAnsSelect;
	@UiField Form form;
	@UiField Button formValidateButton;
	@UiField Button formResetButton;

	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		// Add image widget to this view
		imageWidget.add(new UploadWidget());

		// Set first ListBox.listItem disabled so it acts as a placeholder.
		correctAnsSelect.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		correctAnsSelect.setItemSelected(0, true);
		
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setCategories(String[] categories) {
		// Set first ListBox.listItem disabled so it acts as a placeholder.
		categoryListBox.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		
		int listBoxLength = categoryListBox.getItemCount();
		/* 
		 * Removing list items here is tricky because we need to update total items count each time we remove each specific item.
		 * Otherwise, if we e.g remove first[0] item, our total items count has in fact been decreased by 1,
		 * but our non-updated condition in for loop would remain the same, hence, we end up with IndexOutOfBounsException;
		 */
		while(listBoxLength != 1){
			int i = listBoxLength;
			categoryListBox.removeItem(i - 1);
			//Important - update total items count after remove()
			listBoxLength = categoryListBox.getItemCount();
		}
			
		for (int i = 0; i < categories.length; i++) {
			categoryListBox.addItem(categories[i]);
		}
	}
	
	@UiHandler("formValidateButton")
	public void onFormValidateClick(ClickEvent event) {
	  form.validate();
	  form.submit();
	}
	
	@UiHandler("formResetButton")
	public void onFormResetClick(ClickEvent event) {
	  form.reset();
	}
	
}