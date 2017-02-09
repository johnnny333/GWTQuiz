package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Alert;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ProgressBar;
import org.gwtbootstrap3.client.ui.constants.AlertType;
import org.gwtbootstrap3.client.ui.html.Strong;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;
import pl.johnny.gwtQuiz.shared.Question;

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
	private boolean isShowModal;
	
	@UiField Heading qstnLbl;
	@UiField Image questionImage;
	@UiField org.gwtbootstrap3.client.ui.Button btn0;
	@UiField org.gwtbootstrap3.client.ui.Button btn1;
	@UiField org.gwtbootstrap3.client.ui.Button btn2;
	@UiField org.gwtbootstrap3.client.ui.Button btn3;
	@UiField Modal modal;
	@UiField Modal modalLoading;
	@UiField ModalBody modalBody;
	@UiField Alert modalAlert;
	@UiField Strong modalPointsLabel;
	@UiField Strong actualRecordPositionLabel;
	@UiField org.gwtbootstrap3.client.ui.Button modalCloseBtn;
	@UiField Heading questionCounter;
	@UiField Heading pointsCounter;
	@UiField Heading categoryField;
	@UiField Heading authorField;
	@UiField ProgressBar progressBar;	
	
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
	
	@Override
	public void buildHighScoreCellTableView(HighScoreCellTableView highScoreCellTableView){
		this.highScoreCellTableView = highScoreCellTableView;
		modalBody.add(highScoreCellTableView);
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
	
	@Override
	public void showModal(int userPoints) {
		isShowModal = true;
		
		//Download user scores from server and populate user scores cell table
		listener.insertDataIntoUserScoresTable();
		
		//After the list is populated, get position of actual record and show it to user
		modalPointsLabel.setText("You scored " + userPoints + " point/s and ");
		//...continued at 'setActualRecordPositionLabel()'
		
		modal.show();
	}
	
	@Override
	public void setShowModal(boolean isShowModal) {
		this.isShowModal = isShowModal;
	}

	@Override
	public boolean isShowModal() {
		return isShowModal;
	}
	
	@UiHandler("modalCloseBtn")
	void onModalCloseBtnClicked(ClickEvent e) {
		if(!highScoreCellTableView.getIsNameFieldFilled()){

			highScoreCellTableView.deleteEmptyRecord();
		}
		if (listener != null) {
			listener.goTo(new MainMenuPlace("MainMenu"));
		}
	}
	
	@Override
	public void setQuestionCounter(int questionNumber, int questionsNumber ) {
		questionCounter.setText("" + questionNumber + " / " + questionsNumber);
	}
	
	@Override
	public void setActualRecordPositionLabel(int actualRecordPosition) {
		if(actualRecordPosition == 1){modalAlert.setType(AlertType.SUCCESS);}
		else if (actualRecordPosition < 4){modalAlert.setType(AlertType.INFO);}
		else {modalAlert.setType(AlertType.WARNING);}
		
		actualRecordPositionLabel.setText(" took  " + actualRecordPosition + " place!" );
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

	@Override
	public Modal getHighScoreModal() {
		return modal;
	}
	
	@Override
	public Modal getLoadingModal() {
		return modalLoading;
	}
}