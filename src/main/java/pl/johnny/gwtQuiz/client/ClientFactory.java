package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.client.ui.GoodbyeView;
import pl.johnny.gwtQuiz.client.ui.HelloView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory
{
	EventBus getEventBus();
	PlaceController getPlaceController();
	HelloView getHelloView();
	GoodbyeView getGoodbyeView();
}
