package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface NavBarView extends IsWidget
{
	void setPresenter(Presenter listener);

	public interface Presenter {
		void goTo(Place place);

		void logOutUser();
	}

	void setAnchorListItemActive(int whichAnchorToHighlight);
	
	/**
	 * If user is logged in, set his email on navbar anchor and show 
	 * dropdown menu with logout option. Otherwise, display "Log in" and hide
	 * dropdown menu. 
	 * @param userEmail
	 * @param isLoggedIn
	 */
	void setNavBarAnchor(String userEmail, boolean isLoggedIn);

	void toogleVisibilityOfAdminPanelAnchor(boolean toggler);
}