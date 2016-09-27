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
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.ui.LoginView;
import pl.johnny.gwtQuiz.shared.FailedLoginException;
import pl.johnny.gwtQuiz.shared.User;

public class LoginActivity extends AbstractActivity implements LoginView.Presenter {

	// Used to obtain views, eventBus, placeController
	// Alternatively, could be injected via GIN
	private ClientFactory clientFactory;
	private LoginView loginView;

	public LoginActivity(LoginPlace place, final ClientFactory clientFactory) {
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

		clientFactory.getQuestionsService().loginUser(user, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				
				GWT.log("LoginActivity.loginUser() session id " + result);
				
				//Set cookie for 1 day expiry.
                final long DURATION = 1000 * 60 * 60 * 24 * 1;
                Date expires = new Date(System.currentTimeMillis() + DURATION);
                Cookies.setCookie("gwtQuizCookie", result, expires, null, "/", false);
               				
				goTo(new AdminPlace(""));
			}

			@Override
			public void onFailure(Throwable caught) {
				
				if (caught instanceof FailedLoginException) {
					
					loginView.setServerErrorMessage( ((FailedLoginException)caught).getSymbol() );
					
					return;
				}
				GWT.log("LoginActivity.loginUser() failed",caught);
			}
		});
	}
}