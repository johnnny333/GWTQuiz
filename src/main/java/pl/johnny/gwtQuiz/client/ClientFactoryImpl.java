package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.client.ui.GoodbyeView;
import pl.johnny.gwtQuiz.client.ui.GoodbyeViewImpl;
import pl.johnny.gwtQuiz.client.ui.HelloView;
import pl.johnny.gwtQuiz.client.ui.HelloViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory
{
	private static final EventBus eventBus = new SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final HelloView helloView = new HelloViewImpl();
	private static final GoodbyeView goodbyeView = new GoodbyeViewImpl();

	@Override
	public EventBus getEventBus()
	{
		return eventBus;
	}

	@Override
	public HelloView getHelloView()
	{
		return helloView;
	}

	@Override
	public PlaceController getPlaceController()
	{
		return placeController;
	}

	@Override
	public GoodbyeView getGoodbyeView()
	{
		return goodbyeView;
	}

}
