package pl.johnny.gwtQuiz.client.mvp;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.activity.NavBarActivity;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;

public class NavActivityMapper implements ActivityMapper {

	private ClientFactory clientFactory;

	/**
	 * AppActivityMapper associates each Place with its corresponding
	 * {@link Activity}
	 * 
	 * @param clientFactory
	 *            Factory to be passed to activities
	 */
	public NavActivityMapper(ClientFactory clientFactory) {
		super();
		this.clientFactory = clientFactory;
	}

	/**
	 * Map each Place to its corresponding Activity. This would be a great use
	 * for GIN.
	 */
	@Override
	public Activity getActivity(Place place) {
		// This is begging for GIN
		if(place instanceof MainMenuPlace)
			return new NavBarActivity(place, clientFactory);

		else if(place instanceof QuestionPlace)
			return new NavBarActivity((QuestionPlace) place, clientFactory);

		else if(place instanceof HighScoresPlace)
			return new NavBarActivity((HighScoresPlace) place, clientFactory);

		else if(place instanceof AddQuestionsPlace)
			return new NavBarActivity((AddQuestionsPlace) place, clientFactory);

		else if(place instanceof AdminPlace)
			return new NavBarActivity((AdminPlace) place, clientFactory);

		return null;
	}
}