package pl.johnny.gwtQuiz.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.NavBarView;

public class NavBarActivity extends AbstractActivity implements
		NavBarView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	/** On what activity application is */
	private Place place;
	
	/**
	 * Constructor is overloaded because NavActivityMapper inserts strict typed places.
	 * I could upcast it to Place here, but thus disables correct type-checking of 'instanceof'
	 * in start() method. Hence overloaded constructor with all types of Places.
	 */
	public NavBarActivity(Place place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	public NavBarActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}
//
	public NavBarActivity(HighScoresPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}
	
	public NavBarActivity(AddQuestionsPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}
	
	public NavBarActivity(AdminPlace place, ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}
	
	public NavBarActivity(LoginPlace place, ClientFactory clientFactory) {
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
		
		//Detect on what activity application is at and adjust view accordingly.
		if(place instanceof MainMenuPlace )navBarView.setAnchorListItemActive(0);
		else if(place instanceof QuestionPlace )navBarView.setAnchorListItemActive(1);
		else if(place instanceof HighScoresPlace )navBarView.setAnchorListItemActive(2);
		else if(place instanceof AddQuestionsPlace )navBarView.setAnchorListItemActive(3);

	}
	
	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
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