package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface HighScoresView extends IsWidget
{
	void setPresenter(Presenter listener);
	void buildAndFillHighScoreCellTableView(HighScoreCellTableView highScoreCellTableView);

	public interface Presenter {
		void goTo(Place place);

		void getUserScores();
	}
}