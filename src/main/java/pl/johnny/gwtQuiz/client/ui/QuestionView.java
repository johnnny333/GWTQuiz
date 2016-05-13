package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.client.ui.MainMenuView.Presenter;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 * 
 * @author drfibonacci
 */
public interface QuestionView extends IsWidget
{
	void setPresenter(Presenter listener);
	void setName(String helloName);
	Boolean getName();
	
	
	public interface Presenter {
		void goTo(Place place);
	}
}