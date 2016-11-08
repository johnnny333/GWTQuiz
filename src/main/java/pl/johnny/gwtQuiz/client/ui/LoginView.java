package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.shared.User;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface LoginView extends IsWidget
{
	void setPresenter(Presenter listener);
	
	public interface Presenter {
		void goTo(Place place);
		
		/**
		 * Check provided credentials with server.
		 */
		void loginUser(User user);

		void registerUser(User newUser);

	}

	void setLoginServerErrorMessage(String errorMessage);
	
	/**
	 * Displays appropriate tab on LoginView taken from place tokenName.
	 * @param tabToSelect
	 */
	void selectTab(String tabToSelect);
	
	/**
	 * Clears form input values.
	 * @param types from LoginForm Enum;
	 */
	void resetLoginForms(LoginForm formToReset);
	
	/**
	 * @see resetLoginForms(LoginForm formToReset); 
	 * @author jzarewicz
	 */
	public enum LoginForm {
		FORM_LOGIN, FORM_REGISTER
	}
}