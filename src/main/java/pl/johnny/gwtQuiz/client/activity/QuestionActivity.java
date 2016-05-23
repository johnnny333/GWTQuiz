package pl.johnny.gwtQuiz.client.activity;

import java.util.ArrayList;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.QuestionServiceAsync;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.shared.Question;

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
				
		handler = new NewQuestionEvent.Handler() {
			
			@Override
			public void onNewQuestion(NewQuestionEvent event) {
				questionView.setName(event.getString());
				GWT.log("on handler!!!" + event);
				
				//RPC 
				QuestionServiceAsync questionService = clientFactory.getContactService();
				questionService.getQuestion(new AsyncCallback<ArrayList<Question>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("fail RPC! " + caught.getMessage());
						
					}

					@Override
					public void onSuccess(ArrayList<Question> result) {
						GWT.log("Question " + result.get(0).getQuestion());
						GWT.log("answer " + result.get(0).getAnswers());
						String[] answersArr = result.get(0).getAnswers();
						
					}
					
				});
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