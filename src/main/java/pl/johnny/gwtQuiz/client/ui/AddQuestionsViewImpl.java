package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.ui.widgets.UploadWidget;
import pl.johnny.gwtQuiz.shared.Question;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;

	@UiField
	HTMLPanel imageWidget;
	@UiField
	FormGroup categorySelectFormGroup;
	@UiField
	ListBox categoryListBox;
	@UiField
	FormGroup correctAnsFormGroup;
	@UiField
	ListBox correctAnsListBox;
	@UiField
	Form form;
	@UiField
	Button formValidateButton;
	@UiField
	Button formResetButton;
	@UiField
	TextBox questionField;
	@UiField
	TextBox answer1Field;
	@UiField
	TextBox answer2Field;
	@UiField
	TextBox answer3Field;
	@UiField
	TextBox answer4Field;

	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		// Set first ListBox.listItem disabled so it acts as a placeholder.
		correctAnsListBox.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		correctAnsListBox.setItemSelected(0, true);
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;

		/*
		 * Add image widget to this view and hand instance of listener so it communicate 
		 * with this view. 
		 */
		GWT.log("Image widget count before: " + imageWidget.getWidgetCount());
		if(imageWidget.getWidgetCount() < 1) {
			imageWidget.add(new UploadWidget(listener));
			GWT.log("In add()");
		} else {
			imageWidget.remove(0);
			imageWidget.add(new UploadWidget(listener));
		}
		GWT.log("Image widget count after: " + imageWidget.getWidgetCount());
	}

	@Override
	public void setCategories(String[] categories) {
		// Set first ListBox.listItem disabled so it acts as a placeholder.
		categoryListBox.getElement().getFirstChildElement().setAttribute("disabled", "disabled");

		int listBoxLength = categoryListBox.getItemCount();
		/*
		 * Removing list items here is tricky because we need to update total
		 * items count each time we remove each specific item. Otherwise, if we
		 * e.g remove first[0] item, our total items count has in fact been
		 * decreased by 1, but our non-updated condition in for loop would
		 * remain the same, hence, we end up with IndexOutOfBounsException;
		 */
		while(listBoxLength != 1) {
			int i = listBoxLength;
			categoryListBox.removeItem(i - 1);
			// Important - update total items count after remove()
			listBoxLength = categoryListBox.getItemCount();
		}

		for(int i = 0; i < categories.length; i++) {
			categoryListBox.addItem(categories[i]);
		}
	}

	@UiHandler("formValidateButton")
	public void onFormValidateClick(ClickEvent event) {
		//		Check is form is properly filled. If yes, send new question model.Else, highlight unfilled fields.
		if(form.validate() == true && categoryListBox.getSelectedValue() != "Choose your question category..."
				&& correctAnsListBox.getSelectedValue() != "Choose your question category...") {
			GWT.log("Form validated!");

			//Create user question model from filled fields
			String[] userAnswers = new String[] { answer1Field.getValue(), answer2Field.getValue(), answer3Field.getValue(), answer4Field.getValue() };
			Question userQuestion = new Question(questionField.getValue(), listener.getUploadedImageName(), userAnswers,
					correctAnsListBox.getSelectedValue(), "Janek", categoryListBox.getSelectedValue());
			// There goes RPC logic over activity...
			listener.insertUserQuestion(userQuestion);
			//After sending user question either with or without image, reset uploadedImageName to null (no image). 
			listener.setUploadedImageName(null);

		} else {
			/*
			 * GWTBootstrap3 already red-highlighted empty text fields, so here we only red-highlight selects
			 * which are not validated natively.
			 */
			categorySelectFormGroup.setValidationState(ValidationState.ERROR);
			correctAnsFormGroup.setValidationState(ValidationState.ERROR);
		}
	}

	@UiHandler("formResetButton")
	public void onFormResetClick(ClickEvent event) {
		/*
		 * Reset removes all text-fields native error-highlight so we must manually remove them from non-native
		 * selects(TextBox) 
		 */
		form.reset();
		categorySelectFormGroup.setValidationState(ValidationState.NONE);
		categoryListBox.setItemSelected(0, true);
		correctAnsFormGroup.setValidationState(ValidationState.NONE);
		correctAnsListBox.setItemSelected(0, true);
		
		//Handles image removing after user resets forms
		listener.setUploadedImageName(null);
		
		if(DOM.getElementById("recivedImage") != null)
		DOM.getElementById("recivedImage").removeFromParent();
	}
}