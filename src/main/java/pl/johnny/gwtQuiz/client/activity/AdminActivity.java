/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.ui.AdminView;
import pl.johnny.gwtQuiz.shared.Question;

public class AdminActivity extends AbstractActivity implements AdminView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;

	public AdminActivity(AdminPlace place, final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		final AdminView adminView = clientFactory.getAdminView();
		adminView.setPresenter(this);
		containerWidget.setWidget(adminView.asWidget());
		
		// Put categories from database into ListBox in this activity view.
		clientFactory.getQuestionsService().getCategories(new AsyncCallback<String[]>() {

			@Override
			public void onSuccess(String[] result) {
				adminView.setCategories(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed AdminActivity.getCategories() RPC! ", caught);
			}
		});

		//Get tmp questions
		clientFactory.getQuestionsService().getTmpQuestions(new AsyncCallback<ArrayList<Question>>() {
			
			@Override
			public void onSuccess(ArrayList<Question> result) {
				GWT.log("getTmpQuestions: " + result.get(0).getID());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed AdminActivity.getTmpQuestions() RPC! ", caught);
			}
		});
	}

	/**
	 * Ask user before stopping this activity
	 */
	@Override
	public String mayStop() {
		// return "The quiz is about to start!";
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