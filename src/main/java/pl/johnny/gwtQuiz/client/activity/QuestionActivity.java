package pl.johnny.gwtQuiz.client.activity;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.QuestionView;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class QuestionActivity extends AbstractActivity implements QuestionView.Presenter {
	private final ClientFactory clientFactory;
	private QuestionView questionView;
	private EventBus eventBus;
	private NewQuestionEvent.Handler handler;
	private final String token;
	private final Place place;

	public QuestionActivity(QuestionPlace place, ClientFactory clientFactory) {
		this.clientFactory = clientFactory;
		token = place.getGoodbyeName();
		this.place = place;
	}

	@Override
	public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
		questionView = clientFactory.getQuestionView();
		this.eventBus = eventBus;
//		questionView.setPresenter(this);
//		questionView.setName("eloooo");
	
//		GWT.log("" + clientFactory.getMainMenuView());
		
		handler = new NewQuestionEvent.Handler() {
			
			@Override
			public void onNewQuestion(NewQuestionEvent event) {
				questionView.setName(event.getString());
				GWT.log("on handler!!!" + event);
			}
		};
		
		this.eventBus.addHandler(NewQuestionEvent.TYPE, handler);
		containerWidget.setWidget(questionView.asWidget());
	}

	@Override
	public void goTo(Place place) {
		clientFactory.getPlaceController().goTo(place);
	}
}