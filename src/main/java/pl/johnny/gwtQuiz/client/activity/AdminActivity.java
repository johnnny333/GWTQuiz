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
	private AdminView adminView;

	public AdminActivity(AdminPlace place, final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		adminView = clientFactory.getAdminView();
		adminView.setPresenter(this);
		containerWidget.setWidget(adminView.asWidget());
		
		fetchAndBuildPanelWithTmpQuestions();
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
	
	/**
	 * Set categories from database into field in AdminView. After successful upload of categories,
	 * call database for temporary questions and hand them to AdminView.
	 */
	@Override
	public void fetchAndBuildPanelWithTmpQuestions(){
		
		clientFactory.getQuestionsService().getCategories(new AsyncCallback<String[]>() {

			@Override
			public void onSuccess(String[] result) {
				adminView.setCategories(result);
				
				//Get tmp questions
				clientFactory.getQuestionsService().getTmpQuestions(new AsyncCallback<ArrayList<Question>>() {
					
					@Override
					public void onSuccess(ArrayList<Question> result) {
						GWT.log("getTmpQuestions: " + result.get(0).getCategory());
						adminView.buildAndFillPanelsWithTmpQuestions(result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed AdminActivity.getTmpQuestions() RPC! ", caught);
					}
				});
			}

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed AdminActivity.getCategories() RPC! ", caught);
			}
		});
	}
	
	@Override
	public void acceptUserTmpQuestion(Question userQuestion) {
		//TODO make 'acceptUserTmpQuestion' service...
		clientFactory.getQuestionsService().insertUserTmpQuestion(userQuestion, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("insertUserQuestion failed", caught);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Question submitted");
			}
		});
	}
	
	@Override
	public void deleteUserTmpQuestion(String questionID) {
		//TODO make 'deleteUserTmpQuestion' service...
		clientFactory.getQuestionsService().deleteUserTmpQuestion(questionID, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("deleteUserTmpQuestion failed", caught);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("deleteUserTmpQuestion succeded");
			}
		});
	}
}