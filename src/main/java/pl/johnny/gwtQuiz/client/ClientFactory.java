package pl.johnny.gwtQuiz.client;

import com.google.gwt.place.shared.PlaceController;

import pl.johnny.gwtQuiz.client.ui.AddQuestionsView;
import pl.johnny.gwtQuiz.client.ui.AdminView;
import pl.johnny.gwtQuiz.client.ui.HighScoresView;
import pl.johnny.gwtQuiz.client.ui.LoginView;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.NavBarView;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;

public interface ClientFactory
{
	com.google.web.bindery.event.shared.EventBus getEventBus();
	PlaceController getPlaceController();
	MainMenuView getMainMenuView();
	QuestionView getQuestionView();
	QuestionServiceAsync getQuestionsService();
	HighScoreCellTableView getHighScoreCellTableView();
	HighScoresView getHighScoreView();
	NavBarView getNavBarView();
	AddQuestionsView getAddQuestionsView();
	AdminView getAdminView();
	LoginView getLoginView();
	String getCookie();
}