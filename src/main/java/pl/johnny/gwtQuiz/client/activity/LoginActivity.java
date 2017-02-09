/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.client.activity;

import java.util.Date;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.ui.LoginView;
import pl.johnny.gwtQuiz.client.ui.LoginView.LoginForm;
import pl.johnny.gwtQuiz.shared.FailedLoginException;
import pl.johnny.gwtQuiz.shared.SQLConstraintException;
import pl.johnny.gwtQuiz.shared.User;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	private LoginView loginView;
	private LoginPlace place;

	public LoginActivity(LoginPlace place, final ClientFactory clientFactory) {
		this.place = place;
		this.clientFactory = clientFactory;
	}

	/**
	 * Invoked by the ActivityManager to start a new Activity
	 */
	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		loginView = clientFactory.getLoginView();
		loginView.setPresenter(this);
		containerWidget.setWidget(loginView.asWidget());
		loginView.selectTab(place.getTokenName());
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

	@Override
	public void loginUser(User user) {

		clientFactory.getQuestionsService().loginUser(user, new AsyncCallback<String[][]>() {

			@Override
			public void onSuccess(String[][] result) {

				GWT.log("LoginActivity.loginUser() session id " + result);

				// Set cookie for 7 day expiry.
				final long DURATION = 1000 * 60 * 60 * 24 * 7;
				Date expires = new Date(System.currentTimeMillis() + DURATION);
				
				//Five minute cookie.
//				final long DURATION = 1000 * 5 * 60;
//				Date expires = new Date(System.currentTimeMillis() + DURATION);
				
				String cookieInBase64 = clientFactory.base64Encode( (result[0][0] + "," + result[0][1] + "," + result[0][2] + "," + result[0][3]) );
				Cookies.setCookie("gwtQuiz", cookieInBase64 ,expires);
				
				loginView.resetLoginForms(LoginForm.FORM_LOGIN);
				goTo(new AddQuestionsPlace(""));
			}

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof FailedLoginException) {
					loginView.setLoginServerErrorMessage(caught.getMessage());
					return;
				}
				GWT.log("LoginActivity.loginUser() failed", caught);
			}
		});
	}

	@Override
	public void registerUser(final User newUser) {
		clientFactory.getQuestionsService().insertNewUser(newUser, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				GWT.log("registerUser succeded in LoginActivity.registerUser()");
				//After successful register, take new user to AdminPlace.
				loginView.resetLoginForms(LoginForm.FORM_REGISTER);
				loginUser(newUser);
			}

			@Override
			public void onFailure(Throwable caught) {

				if (caught instanceof SQLConstraintException) {
					loginView.setLoginServerErrorMessage(caught.getMessage());
					GWT.log("SQLConstraintException in LoginActivity.registerUSer() " + caught.getMessage());
					return;
				}
				GWT.log("LoginActivity.registerUser() failed", caught);
			}
		});
	}
}
