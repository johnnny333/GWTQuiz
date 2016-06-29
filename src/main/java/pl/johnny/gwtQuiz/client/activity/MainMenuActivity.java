package pl.johnny.gwtQuiz.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;

public class MainMenuActivity extends AbstractActivity implements
		MainMenuView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "Hello,"
	private String name;
	private EventBus eventBus;
	private MainMenuView mainMenuView;

	public MainMenuActivity(MainMenuPlace place, ClientFactory clientFactory) {
		this.name = place.getHelloName();
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		MainMenuView helloView = clientFactory.getMainMenuView();
		helloView.setPresenter(this);
		containerWidget.setWidget(helloView.asWidget());
		this.eventBus = eventBus;
		
		GWT.log(GWT.getModuleBaseURL());
	}
	
	@Override
	public void onNewGameButtonClicked(int currentQuestion) {
		goTo(new QuestionPlace("Quiz!"));
		eventBus.fireEvent(new NewQuestionEvent(currentQuestion));
	}
	
	@Override
	public void onHighScoreButtonClicked() {
		goTo(new HighScoresPlace("HighScores"));
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
//		return "The quiz is about to start!";
		return null;
	}

	/**
	 * Navigate to a new Place in the browser
	 */
	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
}