package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
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

	public QuestionActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		questionView = clientFactory.getQuestionView();
		questionView.setPresenter(this);
		token = place.getGoodbyeName();
		this.place = place;
		/** Download questions from server,save it in a client and show 1st question */
		QuestionServiceAsync questionService = clientFactory.getContactService();
		questionService.getQuestion(new AsyncCallback<ArrayList<Question>>() {

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

		/** Global handler for question showing. Event argument holds question int */
		handler = new NewQuestionEvent.Handler() {
			
			@Override
			public void onNewQuestion(NewQuestionEvent event) {
				currentQuestionInt = event.getCurrentQuestionInt();
				if(questionsArrayList != null){
				questionView.setQuestion(questionsArrayList.get(currentQuestionInt).getQuestion());
				questionView.setAnswers(questionsArrayList.get(currentQuestionInt));
				/* Display previous button only on > 0 questions */
				if(currentQuestionInt < 1) questionView.setPrvBtnVsbl(false); else questionView.setPrvBtnVsbl(true);
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
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
//		return "Nie skończyłeś quizu,wyjście spowoduje utrate wyniku!";
		return null;
	}
	
	/**
	 * Check answer and got to next question
	 */
	@Override
	public void onAnswerBtnClicked(final String clkdBtnTxt) {
		if( clkdBtnTxt.equals(questionsArrayList.get(currentQuestionInt).getCorrectAnsw()) ){
			GWT.log("good answer!");
			if(questionsArrayList.size() -1 == currentQuestionInt){
				GWT.log("end of quiz!");
				questionView.showModal();
				return;
			}
			eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt + 1));
		}else{
			GWT.log("bad answer!");
		}		
	}

	@Override
	public void onPreviousBtnClicked() {
		if(currentQuestionInt > 0 ){
			eventBus.fireEvent(new NewQuestionEvent(currentQuestionInt - 1));
		}
	}
}