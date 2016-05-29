package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

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
	@UiField Button btn0;
	@UiField Button btn1;
	@UiField Button btn2;
	@UiField Button btn3;
	@UiField FocusPanel focusPanel;

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
		ArrayList<Button> ansBtns = new ArrayList<>(Arrays.asList(btn0, btn1, btn2, btn3));
		for (int i = 0; i < answers.getAnswers().length; i++) {
			ansBtns.get(i).setWidth("6em");
			ansBtns.get(i).setText(answers.getAnswer(i));
			GWT.log("" + answers.getAnswers().length);
		}
	}
	
	@UiHandler(value={"btn0", "btn1", "btn2","btn3"})
	void onAnswerBtnClicked(ClickEvent e) {	
		Button btn = (Button) e.getSource();
		GWT.log("Button text on View " + btn.getText());
		
//		if (listener != null) {
			listener.onAnswerBtnClicked(btn.getText());
//		}
	}
}