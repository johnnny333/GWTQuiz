package pl.johnny.gwtQuiz.client.activity;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.ClientFactory.CookieType;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.ui.AddQuestionsView;
import pl.johnny.gwtQuiz.shared.Question;

public class AddQuestionsActivity extends AbstractActivity implements AddQuestionsView.Presenter {
	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	private AddQuestionsView addQuestionView;
	private AddQuestionsView.Presenter addQuestionViewPresenter = this;
	
	/**
	 * Field representing uploaded image name. If its null it means no image was
	 * uploaded.
	 */
	private String uploadedImagePath = null;

	public AddQuestionsActivity(AddQuestionsPlace place, final ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		addQuestionView = clientFactory.getAddQuestionsView();
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(final AcceptsOneWidget containerWidget, EventBus eventBus) {
		
		/**
		 * Check for session cookie and if exist, validate it on server. If
		 * validation passed, let user stay into AddQuestionPlace. Otherwise,
		 * redirect him into LoginPlace.
		 */
		String cookieSessionID = clientFactory.getCookie(CookieType.SESSION_ID);
		if (cookieSessionID == null) {
			goTo(new LoginPlace(""));
			return;
		}else {
			clientFactory.getQuestionsService().validateSession(cookieSessionID, new AsyncCallback<String[][]>() {

				@Override
				public void onFailure(Throwable caught) {
					GWT.log("AddQuestionsActivity.validateSession() failed", caught);
					return;
				}

				@Override
				public void onSuccess(String[][] result) {
					/** 
					 * If user is not logged (IOW don't have his user cookie but other user cookie exist
					 * in browser e.g user spoofed cookie) restrict access to AdminActicity.
					 */
					if (result == null) {
						goTo(new LoginPlace(""));
					} else {
						/**
						 * If proper user is logged in - initialize view.
						 * .......
						 * Put categories from database into ListBox in this activity view
						 */
						clientFactory.getQuestionsService().getCategories(new AsyncCallback<String[][]>() {

							@Override
							public void onSuccess(String[][] result) {
								AddQuestionsView addQuestionView = clientFactory.getAddQuestionsView();
								addQuestionView.setPresenter(addQuestionViewPresenter);
								containerWidget.setWidget(addQuestionView.asWidget());
								addQuestionView.setCategories(result);
							}

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Failed AddQuestionsActivity.getCategories() RPC! ", caught);
							}
						});
					}
				}
			});
		}
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

	@Override
	public void insertUserQuestion(Question userQuestion) {
		clientFactory.getQuestionsService().insertUserTmpQuestion(userQuestion, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				
				//Catch Hibernate validation exception message.
				if (caught instanceof ConstraintViolationException) {

					ConstraintViolationException violationException = (ConstraintViolationException) caught;
					Set<ConstraintViolation<?>> violations = violationException.getConstraintViolations();

					StringBuffer sb = new StringBuffer();
					for (ConstraintViolation<?> constraintViolation : violations) {
						sb.append(constraintViolation.getPropertyPath().toString())
								.append(":") //
								.append(constraintViolation.getMessage())
								.append("\n");
					
					addQuestionView.setServerErrorMessage(constraintViolation.getPropertyPath().toString(),  
							constraintViolation.getMessage());

					}
					GWT.log("insertUserQuestion On Failure hibernate " + sb);
					return;
				}

				GWT.log("insertUserQuestion failed", caught);
			}

			@Override
			public void onSuccess(Void result) {
				GWT.log("Question submitted");
				addQuestionView.showConfirmationModal();
				addQuestionView.formReset();
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