/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.constants.AlertType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

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

	private Presenter listener;
	private String[] categories;

	private PanelWidget[] panelWidgets;

	public AdminViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AdminViewImpl(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
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
		if(tmpQuestion.size() == 0){panelGroup.clear();panelGroup.add(noQuestionAlert);return;};
		
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
}