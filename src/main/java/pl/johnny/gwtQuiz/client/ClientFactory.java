package pl.johnny.gwtQuiz.client;

import com.google.gwt.place.shared.PlaceController;

import pl.johnny.gwtQuiz.client.ui.AddQuestionsView;
import pl.johnny.gwtQuiz.client.ui.HighScoreCellTableView;
import pl.johnny.gwtQuiz.client.ui.HighScoresView;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.NavBarView;
import pl.johnny.gwtQuiz.client.ui.QuestionView;

public interface ClientFactory
{
	com.google.web.bindery.event.shared.EventBus getEventBus();
	PlaceController getPlaceController();
	MainMenuView getMainMenuView();
	QuestionView getQuestionView();
	QuestionServiceAsync getContactService();
	HighScoreCellTableView getHighScoreCellTableView();
	HighScoresView getHighScoreView();
	NavBarView getNavBarView();
	AddQuestionsView getAddQuestionsView();
}
