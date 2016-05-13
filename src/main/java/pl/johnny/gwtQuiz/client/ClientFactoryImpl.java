package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.client.ui.QuestionViewImpl;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.MainMenuViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final MainMenuView mainMenuView = new MainMenuViewImpl();
	private static final QuestionView questionView = new QuestionViewImpl();

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public MainMenuView getMainMenuView()
	{
		return mainMenuView;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public QuestionView getQuestionView()
	{
		return questionView;
	}

}
