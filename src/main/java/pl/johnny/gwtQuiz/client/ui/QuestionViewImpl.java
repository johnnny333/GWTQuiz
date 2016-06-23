package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ProgressBar;

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
import pl.johnny.gwtQuiz.shared.UserScore;

/**
 * View with multiple methods to set and display question view.
 * @author jzarewicz
 */
public class QuestionViewImpl extends Composite implements QuestionView {

	private static QuestionViewImplUiBinder uiBinder = GWT.create(QuestionViewImplUiBinder.class);

	interface QuestionViewImplUiBinder extends UiBinder<Widget, QuestionViewImpl> {
	}

	public QuestionViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

	}

	private Presenter listener;
	private HighScoreCellTableView highScoreCellTableView;
	
	@UiField Heading qstnLbl;
	@UiField Image questionImage;
	@UiField org.gwtbootstrap3.client.ui.Button btn0;
	@UiField org.gwtbootstrap3.client.ui.Button btn1;
	@UiField org.gwtbootstrap3.client.ui.Button btn2;
	@UiField org.gwtbootstrap3.client.ui.Button btn3;
	@UiField Modal modal;
	@UiField ModalBody modalBody;
	@UiField Label modalPointsLabel;
	@UiField Button prvsQstBtn;
	@UiField org.gwtbootstrap3.client.ui.Button modalCloseBtn;
	@UiField Heading questionCounter;
	@UiField Heading pointsCounter;
	@UiField Heading categoryField;
	@UiField Heading authorField;
	@UiField ProgressBar progressBar;
//	@UiField CellTable<Contact> cellTableHighScores;
	
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}

	@Override
	public void setQuestion(String question) {
		qstnLbl.setText(question);
	}
	
	@Override
	public void setQuestionImage(String questionImageURL,boolean isVisible) {
		questionImage.setUrl(questionImageURL);
		questionImage.setVisible(isVisible);
	}

	@Override
	public void setAnswers(Question answers) {
		org.gwtbootstrap3.client.ui.Button[] ansBtns = new org.gwtbootstrap3.client.ui.Button[]{btn0, btn1, btn2, btn3};
		for (int i = 0; i < answers.getAnswers().length; i++) {
			ansBtns[i].setText(answers.getAnswer(i));
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
		if(highScoreCellTableView.valueChanged == false){
			UserScore userScore = new UserScore(highScoreCellTableView.userScoreLastID, "Player1", 
					highScoreCellTableView.lastUserScore, false);
			listener.updateUserScore(userScore);
		}
		if (listener != null) {
			listener.goTo(new MainMenuPlace("MainMenu"));
		}
	}
	
	@Override
	public void showModal(int userPoints) {
		modalPointsLabel.setText("Points " + userPoints);
		//remove any previously added high score cell table widget to avoid doubling them
		modalBody.remove(0);
		highScoreCellTableView = new HighScoreCellTableView(listener);
		modalBody.add(highScoreCellTableView);
		modal.show();
	}
	
	@Override
	public void setPrvBtnVsbl(boolean bool){
		prvsQstBtn.setVisible(bool);
	}
	
	@Override
	public void setQuestionCounter(int questionNumber, int questionsNumber ) {
		questionCounter.setText("" + questionNumber + " / " + questionsNumber);
	}
	
	@Override
	public void setPointsCounter(int userPoints) {
		pointsCounter.setText("" + userPoints);
	}
	
	@Override
	public void setCategoryField(String category) {
		categoryField.setText(category);
	}

	@Override
	public void setAuthorField(String author) {
		authorField.setText(author);
	}

	@Override
	public void setProgressBar(Double percent) {
		progressBar.setPercent(percent);	
	}

}