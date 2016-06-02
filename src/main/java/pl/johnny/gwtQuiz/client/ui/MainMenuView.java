package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author drfibonacci
 */
public interface MainMenuView extends IsWidget
{
	void setName(String helloName);
	void setPresenter(Presenter listener);

	public interface Presenter
	{
		void goTo(Place place);
//		void onNewGameButtonClicked(String string);
		void onNewGameButtonClicked(int currentQuestion);
	}
}