package pl.johnny.gwtQuiz.client;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

import pl.johnny.gwtQuiz.client.ui.HighScoreCellTableView;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.QuestionView;

public interface ClientFactory
{
	EventBus getEventBus();
	PlaceController getPlaceController();
	MainMenuView getMainMenuView();
	QuestionView getQuestionView();
	QuestionServiceAsync getContactService();
	HighScoreCellTableView getHighScoreCellTableView();
}
