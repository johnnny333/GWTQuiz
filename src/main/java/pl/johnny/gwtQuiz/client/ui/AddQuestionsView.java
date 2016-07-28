package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface AddQuestionsView extends IsWidget
{
	void setPresenter(Presenter listener);
	
	/**
	 * Sets categories in ListBox.
	 */
	void setCategories(String categories);

	public interface Presenter {
		void goTo(Place place);
	}
}