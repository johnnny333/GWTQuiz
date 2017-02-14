/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.FormGroup;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.InlineHelpBlock;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;
import org.gwtbootstrap3.client.ui.constants.ValidationState;
import org.gwtbootstrap3.client.ui.form.validator.HasValidators;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.ui.AdminView.Presenter;
import pl.johnny.gwtQuiz.shared.Question;

public class PanelWidget extends Composite {

	private static PanelWidgetUiBinder uiBinder = GWT.create(PanelWidgetUiBinder.class);

	interface PanelWidgetUiBinder extends UiBinder<Widget, PanelWidget> {
	}

	@UiField
	PanelHeader header;

	@UiField
	PanelCollapse panelCollapse;

	@UiField
	ListBox userCategoryListBox;

	@UiField
	PanelGroup userImagePanel;

	@UiField
	Image userImage;

	@UiField
	TextBox userQuestionField;

	@UiField
	TextBox userAnswer1Field;

	@UiField
	TextBox userAnswer2Field;

	@UiField
	TextBox userAnswer3Field;

	@UiField
	TextBox userAnswer4Field;

	@UiField
	ListBox userCorrectAnsListBox;

	@UiField
	TextBox userAuthorField;

	@UiField
	PanelHeader imgPanelHeader;

	@UiField
	PanelCollapse imgPanelCollapse;

	@UiField
	Form form;

	@UiField
	Button acceptQuestionButton;

	@UiField
	Button deleteQuestionButton;
	
	@UiField
	InlineHelpBlock questionInlineHelpBlock;
	
	@UiField
	FormGroup questionFormGroup;
	
	@UiField
	InlineHelpBlock authorInlineHelpBlock;
	
	@UiField
	FormGroup authorFormGroup;

	private Presenter listener;

	public PanelWidget(Presenter listener) {
		initWidget(uiBinder.createAndBindUi(this));
		this.listener = listener;
	}

	/**
	 * Set text on a header and IDs on a header and collapse to wire them.
	 * 
	 * @param headerText
	 */
	public void setHeaderAndIDs(String questionID, String headerText) {
		header.setText(questionID + ": " + headerText);

		header.setDataTarget("#" + questionID);
		panelCollapse.setId(questionID);
	}

	/**
	 * Fill in question categories with data from database and select user
	 * choice.
	 * 
	 * @param categories
	 *            String[]
	 * @param selectedItem
	 *            String
	 */
	public void setUserCategoryListBox(String[][] categories, String selectedItem) {
		for (int i = 0; i < categories.length; i++) {
			userCategoryListBox.addItem(categories[i][1], categories[i][0]);

			if (categories[i][1] == selectedItem) {
				userCategoryListBox.setItemSelected(i, true);
			}
		}
	}

	/**
	 * Set user image URL and show previously hidden panel group which hold
	 * image widget.
	 * 
	 * @param userImageURL
	 * @param questionID
	 */
	public void setUserImage(String questionID, String userImageURL) {

		if (userImageURL != null) {
			imgPanelHeader.setDataTarget("#" + questionID + "userQuestionImage");
			imgPanelCollapse.setId(questionID + "userQuestionImage");
			userImage.setUrl(userImageURL);
			userImagePanel.setVisible(true);
		}
	}

	public void setUserQuestionField(String userQuestion) {
		userQuestionField.setText(userQuestion);
	}

	public void setUserAnswer1Field(String userAnswer1) {
		userAnswer1Field.setText(userAnswer1);
	}

	public void setUserAnswer2Field(String userAnswer2) {
		userAnswer2Field.setText(userAnswer2);
	}

	public void setUserAnswer3Field(String userAnswer3) {
		userAnswer3Field.setText(userAnswer3);
	}

	public void setUserAnswer4Field(String userAnswer4) {
		userAnswer4Field.setText(userAnswer4);
	}

	public void setUserCorrectAnsListBox(int userCorrectAns) {
		userCorrectAnsListBox.setItemSelected(userCorrectAns, true);
	}

	public void setUserAuthorField(String userAuthor) {
		userAuthorField.setText(userAuthor);
	}

	@UiHandler("acceptQuestionButton")
	void onAcceptQuestionButtonClicked(ClickEvent e) {

		// Check for empty fields.
		if (form.validate()) {
			
			//Trim and escape HTML on TextBox Fields.
			for (HasValidators<?> child : getChildrenWithValidators(form)) {
				((TextBox) child).setText(SafeHtmlUtils.htmlEscape(((TextBox) child).getValue().trim()));
			}

			// Check for the existence of image in form.
			String userImageVar = null;
			if (userImage.getUrl() != "") {
				//Get only image filename from absolute path.
				userImageVar = userImage.getUrl();
			}
			
			// Fill question model with data from form.
			String[] userAnswers = new String[] { userAnswer1Field.getValue(), userAnswer2Field.getValue(),
					userAnswer3Field.getValue(), userAnswer4Field.getValue() };
			Question userQuestion = new Question(userQuestionField.getValue(), userImageVar, userAnswers,
					userCorrectAnsListBox.getSelectedValue(), userAuthorField.getValue(),
					userCategoryListBox.getSelectedValue());

			// Send filled question model and its question id through RPC.
			listener.acceptUserTmpQuestion(userQuestion, panelCollapse.getId());

		}
	}

	@UiHandler("deleteQuestionButton")
	void onDeleteQuestionButtonClicked(ClickEvent e) {

		String tmpQuestionID = panelCollapse.getId();

		GWT.log("Question is about to be deleted ID! " + tmpQuestionID);

		// Send filled question model through RPC.
		listener.deleteUserTmpQuestion(tmpQuestionID);
	}

	public void setServerErrorMessage(String propertyPath, String errorMessage) {

		if (panelCollapse.isIn()) {

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
				GWT.log("Accept Question Validation server error " + propertyPath + ": " + errorMessage);
				break;
			}
		}
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
}