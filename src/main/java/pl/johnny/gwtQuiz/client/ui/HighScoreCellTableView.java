package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.shared.UserScore;

public interface HighScoreCellTableView extends IsWidget {

	/** 
	 * Function used by corresponding Activity to establish communication
	 * between said Activity and this View.
	 * 
	 * @param listener
	 */
	void setPresenter(QuestionView.Presenter listener);
	
	/** Fill empty high score cell table with data from RPC call */
	void fillHighScoreCellTable(ArrayList<UserScore> result);
	
	Boolean getIsNameFieldFilled();

	/** 
	 * Creates generic record for situations when user don't provide its name to an empty name field
	 * in HighScoreCellTableView. (either click close on records modal or close,navigates away from records view). 
	 * Said record is then updated in database with updateUserScore(userScore);
	 */
	void deleteEmptyRecord();
	
	public interface Presenter {
		void goTo(Place place);
	}		
}