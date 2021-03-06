package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;

import org.gwtbootstrap3.client.ui.Icon;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.constants.IconSize;
import org.gwtbootstrap3.client.ui.constants.IconType;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.QuestionServiceAsync;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;
import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.UserScore;

/**
 * All quiz logic: RPC calling, questions showing, setting view,points counting, quiz finish... 
 * @author jzarewicz
 *
 */
public class QuestionActivity extends AbstractActivity implements QuestionView.Presenter {
	public ClientFactory clientFactory;
	private QuestionView questionView;
	private QuestionServiceAsync questionService;
	private EventBus eventBus;
	private NewQuestionEvent.Handler handler;
	private final String token;
	private final Place place;
	/** Keeps current question int given from event, so other methods could use it to keep state. */
	private int currentQuestionInt;
	/** Keeps whole ArrayList&lt;Question> from server in client to avoid RPC calling. */
	private ArrayList<Question> questionsArrayList;
	/** Keeps user points on instance of the quiz */
	private int userPoints;
	/** Global Timer variable to enable canceling it from this whole class */
	private Timer questionTimer;
	private HighScoreCellTableView highScoreCellTableView;

	public QuestionActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		questionView = clientFactory.getQuestionView();
		questionView.setPresenter(this);
		
		highScoreCellTableView = clientFactory.getHighScoreCellTableView();
		highScoreCellTableView.setPresenter(this);
		questionView.buildHighScoreCellTableView(highScoreCellTableView);

		token = place.getGoodbyeName();
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, final EventBus eventBus) {
		this.eventBus = eventBus;
		containerWidget.setWidget(questionView.asWidget());
		
		//Show loading modal before all questions will be downloaded...
		questionView.getLoadingModal().show();
		
		/* Download questions from server,save it in a client and show 1st question */
		questionService = clientFactory.getQuestionsService();
		questionService.getShuffledQuestions(new AsyncCallback<ArrayList<Question>>() {

			@Override
			public void onFailure(Throwable caught) {
				questionView.getLoadingModal().add(new HTML("Problem downloading questions!"));
				GWT.log("Failed getShuffledQuestions() RPC! ", caught);
			}

			@Override
			public void onSuccess(ArrayList<Question> result) {
				questionsArrayList = result;
				questionView.getLoadingModal().hide();
				eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt));
			}
		});

		/* Global handler for question showing. Event argument holds question int */
		handler = new NewQuestionEvent.Handler() {

			@Override
			public void onNewQuestion(NewQuestionEvent event) {
				//Start a new instance of timer on every question.
				currentQuestionInt = event.getCurrentQuestionInt();
				if(questionsArrayList != null) {
					questionView.setShowModal(false);
					questionView.setQuestion(questionsArrayList.get(currentQuestionInt).getQuestion());
					timerForProgressBar(25);

					//Question image logic
					if(questionsArrayList.get(currentQuestionInt).getImageURL() != null) {
						questionView.setQuestionImage(questionsArrayList.get(currentQuestionInt).getImageURL(), true);
					} else {
						questionView.setQuestionImage("", false);
						GWT.log("No picture " + questionsArrayList.get(currentQuestionInt).getImageURL());
					}

					questionView.setAnswers(questionsArrayList.get(currentQuestionInt));
					
					questionView.setQuestionCounter(currentQuestionInt + 1, questionsArrayList.size());
					questionView.setPointsCounter(userPoints);
					questionView.setCategoryField(questionsArrayList.get(currentQuestionInt).getCategory());
					questionView.setAuthorField(questionsArrayList.get(currentQuestionInt).getAuthor());
				}
			}
		};
		this.eventBus.addHandler(NewQuestionEvent.TYPE, handler);
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}

	/**
	 * If user navigates back or forward from browser - 
	 * stop current timer.
	 */
	@Override
	public String mayStop() {
		/*
		 * Keeps user records in db clean when user navigates away from user scores 
		 * with blank name field (fill in name with generic name and set is_editable flag  
		 * to false.
		 */
		if(!highScoreCellTableView.getIsNameFieldFilled() && questionView.isShowModal()) {
			highScoreCellTableView.deleteEmptyRecord();
		}

		//cancel current timer
		if(questionTimer != null) {
			questionTimer.cancel();
		}
		
		/*
		 * When on modal High Score list user navigates away from there,
		 * close modals and its glass override 
		 */
		questionView.getHighScoreModal().hide();
		questionView.getLoadingModal().hide();
		
		return null;
	}

	@Override
	public void onAnswerBtnClicked(final String clkdBtnTxt) {
		//cancel previous timer
		if(questionTimer != null) {
			questionTimer.cancel();
		} ;
		//Check for correct answer
		if(clkdBtnTxt.equals(questionsArrayList.get(currentQuestionInt).getCorrectAnsw())) {
			GWT.log("good answer!");
			userPoints++; //TODO ++ or +1 in JAVA?
		} else {
			GWT.log("bad answer!");
		}
		//If we got last question, show modal with user points and return from this function
		if(questionsArrayList.size() == currentQuestionInt + 1) {
			GWT.log("end of quiz!");
			//TODO !DRY
			questionView.setPointsCounter(userPoints);
			questionView.showModal(userPoints);
			return;
		}
		//Show next question
		eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt + 1));
	}

	//TODO Whole previous question logic
	@Override
	public void onPreviousBtnClicked() {
		if(currentQuestionInt > 0) {
			eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt - 1));
		}
	}

	/** 
	 * Timer to hurry up user answering ;) 
	 * @param timerTime user specified count-down time
	 * */
	private void timerForProgressBar(final int timerTime) {
		//cancel previous timer
		if(questionTimer != null) {
			questionTimer.cancel();
		} ;
		// Create a new timer that updates the countdown every second.
		questionTimer = new Timer() {
			int count = 0;

			@Override
			public void run() {
				GWT.log("Time remaining: " + Integer.toString(count) + "s.");
				questionView.setProgressBar(Math.floor(100 * count / timerTime));
				count++;
				if(count > timerTime) {
					GWT.log("Time is up!");
					this.cancel(); //cancel the timer -- important!
					//When time for answer has elapsed, fire up new question.
					onAnswerBtnClicked("");
				}
			}
		};
		// Schedule the timer to run once every second, 1000 ms.
		questionTimer.scheduleRepeating(1000); //scheduleRepeating(), not just schedule().
	}

	//TODO Think about making separate class with all RPCs
	@Override
	public void insertDataIntoUserScoresTable() {
		
		//Temporary record with user points and isEditable set to true
		UserScore userScore = new UserScore("", userPoints, true);

		questionService.insertUserScore(userScore, new AsyncCallback<ArrayList<UserScore>>() {
			@Override
			public void onSuccess(ArrayList<UserScore> result) {
				highScoreCellTableView.fillHighScoreCellTable(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed insertUserScore() RPC!", caught);
			}
		});
	}

	@Override
	public void updateUserScore(UserScore userScore) {
		questionService.updateUserScore(userScore, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed updateUserScore() RPC!", caught);
			}

			@Override
			public void onSuccess(Void result) {
				//Record with new name was updated successfully
			}
		});
	}
	
	@Override
	public void deleteUserScore(UserScore userScore) {
		questionService.deleteUserScore(userScore, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed updateUserScore() RPC!", caught);
			}

			@Override
			public void onSuccess(Void result) {
				//Record without new name was deleted successfully
			}
		});
	}

	@Override
	public void setActualRecordPosition(int actualRecordPosition) {
		questionView.setActualRecordPositionLabel(actualRecordPosition);
	}
	
	public ArrayList<Question> getQuestionsArrayList() {
		return questionsArrayList;
	}
}