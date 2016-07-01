package pl.johnny.gwtQuiz.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.NavBarView;

public class NavBarActivity extends AbstractActivity implements
		NavBarView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	// Name that will be appended to "Hello,"
	private String name;
	private EventBus eventBus;
	private MainMenuView mainMenuView;
	private Place place;
	private NavBarView navBarView;

	public NavBarActivity(MainMenuPlace place, ClientFactory clientFactory) {
		this.name = place.getHelloName();
		this.clientFactory = clientFactory;
		navBarView = clientFactory.getNavBarView();
	}

	public NavBarActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	public NavBarActivity(HighScoresPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		NavBarView navBarView = clientFactory.getNavBarView();
		navBarView.setPresenter(this);
		containerWidget.setWidget(navBarView.asWidget());
		this.eventBus = eventBus;
		
		//Detect what activity on main panel is on and adjust view accordingly.
		if(place instanceof QuestionPlace )navBarView.setAnchorListItem(0);
		else if(place instanceof HighScoresPlace )navBarView.setAnchorListItem(1);
		//For some unknown reason GWT don't 'see' MainMenuPlace despite being on it, hence this else
		else navBarView.setAnchorListItem(2);
		
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