package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.QuestionServiceAsync;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.shared.Question;

public class QuestionActivity extends AbstractActivity implements QuestionView.Presenter {
	public ClientFactory clientFactory;
	private QuestionView questionView;
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
	/** Global Timer variable to enable canceling it from whole this class */
	private Timer questionTimer;

	public QuestionActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		questionView = clientFactory.getQuestionView();
		questionView.setPresenter(this);
		token = place.getGoodbyeName();
		this.place = place;
		/* Download questions from server,save it in a client and show 1st question */
		QuestionServiceAsync questionService = clientFactory.getContactService();
		questionService.getShuffledQuestions(new AsyncCallback<ArrayList<Question>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("fail RPC! " + caught.getMessage());
			}

			@Override
			public void onSuccess(ArrayList<Question> result) {
				questionsArrayList = result;
				eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt));
			}
		});
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, final EventBus eventBus) {
		this.eventBus = eventBus;
		containerWidget.setWidget(questionView.asWidget());

		/* Global handler for question showing. Event argument holds question int */
		handler = new NewQuestionEvent.Handler() {
			
			@Override
			public void onNewQuestion(NewQuestionEvent event) {
				//Start a new instance of timer on every question.
				timerForProgressBar(25);
				currentQuestionInt = event.getCurrentQuestionInt();
				if(questionsArrayList != null){
				questionView.setQuestion(questionsArrayList.get(currentQuestionInt).getQuestion());
				questionView.setAnswers(questionsArrayList.get(currentQuestionInt));
				/* Display previous button only on > 0 questions */
				if(currentQuestionInt < 1) questionView.setPrvBtnVsbl(false); else questionView.setPrvBtnVsbl(true);
				questionView.setQuestionCounter(currentQuestionInt + 1);
				questionView.setPointsCounter(userPoints);

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
		//cancel current timer
		if(questionTimer != null){questionTimer.cancel();};
		return null;
	}
		
	@Override
	public void onAnswerBtnClicked(final String clkdBtnTxt) {
		//cancel previous timer
		if(questionTimer != null){questionTimer.cancel();};
		//Check for correct answer
		if( clkdBtnTxt.equals(questionsArrayList.get(currentQuestionInt).getCorrectAnsw()) ){
			GWT.log("good answer!");
			userPoints++; //TODO ++ or +1 in JAVA?
		}else{
			GWT.log("bad answer!");
		}
		//If we got last question, show modal with user points and return from this function
		if(questionsArrayList.size()  == currentQuestionInt + 1){
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
		if(currentQuestionInt > 0 ){
			eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt - 1));
		}
	}
	
	/** 
	 * Timer to hurry up user answering ;) 
	 * 
	 * @param timerTime user specified countdown time
	 * */
	private void timerForProgressBar(final int timerTime) {
		//cancel previous timer
		if(questionTimer != null){questionTimer.cancel();};
		// Create a new timer that updates the countdown every second.
	    questionTimer = new Timer() {
	    	int count = 0;
	      @Override
		public void run() {
	        GWT.log("Time remaining: " + Integer.toString(count) + "s.");
	        questionView.setTimerCounter(Integer.toString(count));
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
}