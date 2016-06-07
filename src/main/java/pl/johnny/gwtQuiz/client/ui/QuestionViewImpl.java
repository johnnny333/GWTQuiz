package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.gwtbootstrap3.client.ui.Modal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.shared.Question;

public class QuestionViewImpl extends Composite implements QuestionView {

	private static QuestionViewImplUiBinder uiBinder = GWT.create(QuestionViewImplUiBinder.class);

	interface QuestionViewImplUiBinder extends UiBinder<Widget, QuestionViewImpl> {
	}

	public QuestionViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	private Presenter listener;
	
	@UiField Label qstnLbl;
	@UiField org.gwtbootstrap3.client.ui.Button btn0;
	@UiField org.gwtbootstrap3.client.ui.Button btn1;
	@UiField org.gwtbootstrap3.client.ui.Button btn2;
	@UiField org.gwtbootstrap3.client.ui.Button btn3;
	@UiField Modal modal;
	@UiField Label pointsPanel;
	@UiField Button prvsQstBtn;
	@UiField org.gwtbootstrap3.client.ui.Button modalCloseBtn;
	
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setQuestion(String question) {
		qstnLbl.setText(question);
	}

	@Override
	public void setAnswers(Question answers) {
		ArrayList<org.gwtbootstrap3.client.ui.Button> ansBtns = new ArrayList<>(Arrays.asList(btn0, btn1, btn2, btn3));
		for (int i = 0; i < answers.getAnswers().length; i++) {
//			ansBtns.get(i).setWidth("6em");
			ansBtns.get(i).setText(answers.getAnswer(i));
		}
	}
	
	@UiHandler(value={"btn0", "btn1", "btn2","btn3"})
	void onAnswerBtnClicked(ClickEvent e) {	
		org.gwtbootstrap3.client.ui.Button btn = (org.gwtbootstrap3.client.ui.Button) e.getSource();
		if (listener != null) {
			listener.onAnswerBtnClicked(btn.getText());
		}
	}
	
	@UiHandler("prvsQstBtn")
	void onPreviusBtnClicked(ClickEvent e) {
		if (listener != null) {
			listener.onPreviousBtnClicked();
		}
	}
	
	@UiHandler("modalCloseBtn")
	void onModalCloseBtnClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new MainMenuPlace("MainMenu"));
		}
	}
	
	@Override
	public void showModal() {
		pointsPanel.setText("3 points!");
		modal.show();
	}
	
	@Override
	public void setPrvBtnVsbl(boolean bool){
		prvsQstBtn.setVisible(bool);
	}
}