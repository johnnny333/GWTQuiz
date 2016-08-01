package pl.johnny.gwtQuiz.client.activity;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.QuestionServiceAsync;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.ui.AddQuestionsView;
import pl.johnny.gwtQuiz.shared.Question;

public class AddQuestionsActivity extends AbstractActivity implements
	AddQuestionsView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	/**Field representing uploaded image name. If its null it means no image was uploaded. */
	private String uploadedImagePath = null;

	public AddQuestionsActivity(AddQuestionsPlace place, final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		//Put categories from database into ListBox in this activity view.
		clientFactory.getQuestionsService().getCategories(new AsyncCallback<String[]>() {
			
			@Override
			public void onSuccess(String[] result) {
				clientFactory.getAddQuestionsView().setCategories(result);	
			}
			
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Failed getCategories() RPC! ", caught);
			}
		});
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		AddQuestionsView addQuestionView = clientFactory.getAddQuestionsView();
		addQuestionView.setPresenter(this);
		containerWidget.setWidget(addQuestionView.asWidget());
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
	public void insertUserQuestion(Question userQuestion) {
		clientFactory.getQuestionsService().insertUserQuestion(userQuestion, new AsyncCallback<Void>() {

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
	public void setUploadedImageName(String uploadedImageName) {
		this.uploadedImagePath = uploadedImageName;
	}
	
	@Override
	public String getUploadedImageName() {
		return uploadedImagePath;
	}
}