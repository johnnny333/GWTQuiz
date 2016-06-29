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
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.ui.HighScoreCellTableView;
import pl.johnny.gwtQuiz.client.ui.HighScoresView;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.shared.UserScore;

public class HighScoresActivity extends AbstractActivity implements
		HighScoresView.Presenter{
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "Hello,"
	private String name;
	private EventBus eventBus;
	private MainMenuView mainMenuView;
	private QuestionServiceAsync questionService;
	private HighScoreCellTableView highScoreCellTableView;

	public HighScoresActivity(HighScoresPlace place, ClientFactory clientFactory) {
		this.name = place.getHelloName();
		this.clientFactory = clientFactory;
		questionService = clientFactory.getContactService();
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		HighScoresView highScoresView = clientFactory.getHighScoreView();
		highScoresView.setPresenter(this);
		containerWidget.setWidget(highScoresView.asWidget());
		
		highScoreCellTableView = clientFactory.getHighScoreCellTableView();
		highScoresView.buildAndFillHighScoreCellTableView(highScoreCellTableView);
		
		this.eventBus = eventBus;
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
	
	@Override
	public void getUserScores() {
		
		questionService.getUserScores(new AsyncCallback<ArrayList<UserScore>>() {
			
			@Override
			public void onSuccess(ArrayList<UserScore> result) {
				highScoreCellTableView.fillHighScoreCellTable(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed getUserScores() RPC! " + caught.getMessage(), caught);
			}
		});
	}
}