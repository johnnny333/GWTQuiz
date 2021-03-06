package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InlineHelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.PanelBody;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.form.validator.HasValidators;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.ErrorHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.shared.Question;

public class AddQuestionsViewImpl extends Composite implements AddQuestionsView {

	private static AddQuestionViewImplUiBinder uiBinder = GWT.create(AddQuestionViewImplUiBinder.class);

	interface AddQuestionViewImplUiBinder extends UiBinder<Widget, AddQuestionsViewImpl> {
	}

	private Presenter listener;

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
	@UiField
	TextBox authorField;
	@UiField
	InlineHelpBlock authorInlineHelpBlock;
	@UiField
	FormGroup authorFormGroup;
	@UiField
	InlineHelpBlock questionInlineHelpBlock;
	@UiField
	FormGroup questionFormGroup;
	@UiField
	Modal confirmationModal;
	@UiField
	Modal modalLoading;

	@UiField
	TextBox imageField;
	@UiField
	Image imageURL;
	@UiField
	FormGroup imageFormGroup;
	@UiField
	InlineHelpBlock imageInlineHelpBlock;
	
	String imageUrlField;

	@UiField
	PanelBody panelBodyInsideForm;

	public AddQuestionsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		// Set first ListBox.listItem disabled so it acts as a placeholder.
		correctAnsListBox.getElement().getFirstChildElement().setAttribute("disabled", "disabled");
		correctAnsListBox.setItemSelected(0, true);

		imageField.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {

				imageURL.setUrl(SafeHtmlUtils.htmlEscape(event.getValue()));
				imageURL.getElement().getStyle().setProperty("maxHeight", "350px");
				imageFormGroup.setValidationState(ValidationState.NONE);
				imageInlineHelpBlock.setText("");
				
				imageUrlField = imageField.getValue();
			}
		});

		imageURL.addDoubleClickHandler(new DoubleClickHandler() {

			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				imageField.setValue("");
				imageURL.setUrl("");
				imageFormGroup.setValidationState(ValidationState.NONE);
				imageInlineHelpBlock.setText("");
				
				imageUrlField = null;

			}
		});

		imageURL.addErrorHandler(new ErrorHandler() {

			@Override
			public void onError(ErrorEvent event) {

				if (!imageField.getValue().isEmpty()) {
					imageFormGroup.setValidationState(ValidationState.ERROR);
					imageInlineHelpBlock.setText("Provided URL is not valid!");
					imageURL.setUrl("");
					
					imageUrlField = null;
				}
			}
		});
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setCategories(String[][] categories) {
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
		while (listBoxLength != 1) {
			int i = listBoxLength;
			categoryListBox.removeItem(i - 1);
			// Important - update total items count after remove()
			listBoxLength = categoryListBox.getItemCount();
		}

		for (int i = 0; i < categories.length; i++) {
			categoryListBox.addItem(categories[i][1], categories[i][0]);
		}
	}

	@UiHandler("formValidateButton")
	public void onFormSubmitClick(ClickEvent event) {

		/*
		 * Check is form is properly filled. If yes, send new question
		 * model.Else, highlight unfilled fields.
		 */
		if (form.validate() && categoryListBox.getSelectedValue() != "Choose your question category..."
				&& correctAnsListBox.getSelectedValue() != "Which answer is correct?") {

			// Trim and escape HTML on TextBox Fields.
			for (HasValidators<?> child : getChildrenWithValidators(form)) {
				((TextBox) child).setText(SafeHtmlUtils.htmlEscape(((TextBox) child).getValue().trim()));
			}

			// Create user question model from filled fields
			String[] userAnswers = new String[] { answer1Field.getValue(), answer2Field.getValue(),
					answer3Field.getValue(), answer4Field.getValue() };

			Question userQuestion = new Question(questionField.getValue(), imageUrlField != null ? imageUrlField : null, userAnswers,
					correctAnsListBox.getSelectedValue(), authorField.getValue(), categoryListBox.getSelectedValue());
			// There goes RPC logic over activity...
			listener.insertUserQuestion(userQuestion);

		} else {
			/*
			 * GWTBootstrap3 already red-highlighted empty text fields, so here
			 * we only red-highlight selects which are not validated natively.
			 */
			if (categoryListBox.getSelectedValue() == "Choose your question category...") {
				categorySelectFormGroup.setValidationState(ValidationState.ERROR);
			} else {
				categorySelectFormGroup.setValidationState(ValidationState.NONE);
			}

			if (correctAnsListBox.getSelectedValue() == "Which answer is correct?") {
				correctAnsFormGroup.setValidationState(ValidationState.ERROR);
			} else {
				correctAnsFormGroup.setValidationState(ValidationState.NONE);
			}
		}
	}

	@UiHandler("formResetButton")
	public void onFormResetClick(ClickEvent event) {
		formReset();
	}

	/** Reset all fields in addQuestionView */
	@Override
	public void formReset() {
		/*
		 * Reset removes all text-fields native error-highlight so we must
		 * manually remove them from non-native selects(TextBox). Author field
		 * is saved to avoid users hassle of retyping their names on every
		 * another question submits.
		 */
		String authorValue = authorField.getValue();
		form.reset();
		authorField.setValue(authorValue);
		categorySelectFormGroup.setValidationState(ValidationState.NONE);
		categoryListBox.setItemSelected(0, true);
		correctAnsFormGroup.setValidationState(ValidationState.NONE);
		correctAnsListBox.setItemSelected(0, true);

		imageURL.setUrl("");
	}

	@Override
	public void setServerErrorMessage(String propertyPath, String errorMessage) {

		switch (propertyPath) {
		case "authorData":
			authorFormGroup.setValidationState(ValidationState.ERROR);
			authorInlineHelpBlock.setText(errorMessage);
			break;
		case "question":
			questionFormGroup.setValidationState(ValidationState.ERROR);
			questionInlineHelpBlock.setText(errorMessage);
			break;

		default:
			GWT.log("Question Validation server error " + propertyPath + ": " + errorMessage);
			break;
		}
	}

	@Override
	public void showConfirmationModal() {
		confirmationModal.show();
	}

	/**
	 * Get this forms child input elements with validators.
	 *
	 * @param widget
	 *            the widget
	 * @return the children with validators
	 */
	protected List<HasValidators<?>> getChildrenWithValidators(Widget widget) {
		List<HasValidators<?>> result = new ArrayList<HasValidators<?>>();
		if (widget != null) {
			if (widget instanceof HasValidators<?>) {
				result.add((HasValidators<?>) widget);
			}
			if (widget instanceof HasOneWidget) {
				result.addAll(getChildrenWithValidators(((HasOneWidget) widget).getWidget()));
			}
			if (widget instanceof HasWidgets) {
				for (Widget child : (HasWidgets) widget) {
					result.addAll(getChildrenWithValidators(child));
				}
			}
		}
		return result;
	}

	@Override
	public Modal getModalLoading() {
		return modalLoading;
	}
}