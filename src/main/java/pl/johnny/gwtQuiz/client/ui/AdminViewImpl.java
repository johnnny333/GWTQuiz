/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.PanelGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.thirdparty.javascript.rhino.head.Undefined;
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

		PanelWidget[] panelWidget = new PanelWidget[tmpQuestion.size()];

		for (int i = 0; i < panelWidget.length; i++) {
			panelWidget[i] = new PanelWidget();
			panelWidget[i].setUserCategoryListBox(categories, tmpQuestion.get(i).getCategory());
			panelWidget[i].setHeaderAndIDs(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getQuestion());
			panelWidget[i].setUserImage(tmpQuestion.get(i).getID(), tmpQuestion.get(i).getImageURL());
			panelWidget[i].setUserQuestionField(tmpQuestion.get(i).getQuestion());

			for (int j = 0; j < tmpQuestion.get(i).getAnswers().length; j++) {
				panelWidget[i].setUserAnswer1Field(tmpQuestion.get(i).getAnswer(j));

				switch (j) {
				case 0:
					panelWidget[i].setUserAnswer1Field(tmpQuestion.get(i).getAnswer(j));
					break;
				case 1:
					panelWidget[i].setUserAnswer2Field(tmpQuestion.get(i).getAnswer(j));
					break;
				case 2:
					panelWidget[i].setUserAnswer3Field(tmpQuestion.get(i).getAnswer(j));
					break;
				case 3:
					panelWidget[i].setUserAnswer4Field(tmpQuestion.get(i).getAnswer(j));
					break;
				}
			}
			
			panelWidget[i].setUserCorrectAnsListBox(tmpQuestion.get(i).getCorrectAnswersInt());
			panelWidget[i].setUserAuthorField(tmpQuestion.get(i).getAuthor());

			GWT.log("panel group count: " + panelGroup.getWidgetCount());

			// Avoid doubling widgets...
			if (panelGroup.getWidgetCount() < panelWidget.length)
				panelGroup.add(panelWidget[i]);
		}
	}
	
	@UiHandler("refreshIcon")
	void onRefreshIconClicked(ClickEvent e) {
		panelGroup.clear();
		listener.fetchAndBuildPanelWithTmpQuestion();
	}
}