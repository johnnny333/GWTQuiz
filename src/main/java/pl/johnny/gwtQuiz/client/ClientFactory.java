package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.QuestionView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory
{
	EventBus getEventBus();
	PlaceController getPlaceController();
	MainMenuView getMainMenuView();
	QuestionView getQuestionView();
	QuestionsServiceAsync getContactService();
}
