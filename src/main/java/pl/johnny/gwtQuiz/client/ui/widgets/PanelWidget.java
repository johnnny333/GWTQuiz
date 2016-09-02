/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui.widgets;

import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.Form;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.TextBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
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
	public void setUserCategoryListBox(String[] categories, String selectedItem) {
		for (int i = 0; i < categories.length; i++) {
			userCategoryListBox.addItem(categories[i], categories[i]);

			if (categories[i] == selectedItem) {
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
	public void setUserImage(String questionID , String userImageURL) {

		if (userImageURL != null) {
			imgPanelHeader.setDataTarget("#" + questionID + "userQuestionImage");
			imgPanelCollapse.setId(questionID + "userQuestionImage");
			userImage.setUrl("quiz_resources/question_images_tmp/" + questionID + "/" + userImageURL);
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
	void onAcceptQuestionButtonClicked(ClickEvent e){
		
		//Check for empty fields.
		if(form.validate() == true ) {
			GWT.log("Question validated!");
			
			//Check for the existence of image in form. 
			String userImageVar = null;
			if(userImage.getUrl() != ""){userImageVar = userImage.getUrl().substring(userImage.getUrl().lastIndexOf("/") + 1);;};
			//Fill question model with data from form.
			String[] userAnswers = new String[] { userAnswer1Field.getValue(), userAnswer2Field.getValue(), userAnswer3Field.getValue(), userAnswer4Field.getValue() };
			Question userQuestion = new Question(userQuestionField.getValue(), userImageVar, userAnswers,
					userCorrectAnsListBox.getSelectedValue(), userAuthorField.getValue(), userCategoryListBox.getSelectedValue());
			
			GWT.log(userImageVar);
			//Send filled question model and its question id through RPC. 
			listener.acceptUserTmpQuestion(userQuestion, panelCollapse.getId());
		}
	}
	
	@UiHandler("deleteQuestionButton")
	void onDeleteQuestionButtonClicked(ClickEvent e){
		
			String tmpQuestionID = panelCollapse.getId();
			
			GWT.log("Question is about to be deleted ID! " + tmpQuestionID);
			
			//Send filled question model through RPC. 
			listener.deleteUserTmpQuestion(tmpQuestionID);
	}
}