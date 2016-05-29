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
		questionView.setPresenter(this);
		this.eventBus = eventBus;
				
		handler = new NewQuestionEvent.Handler() {
			
			@Override
			public void onNewQuestion(NewQuestionEvent event) {
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
						GWT.log("answer " + result.get(0).getAnswer(0));
						questionView.setQuestion(result.get(0).getQuestion());
						questionView.setAnswers(result.get(0));
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

	@Override
	public void onAnswerBtnClicked(final String clkdBtnTxt) {
		GWT.log("value of button on presenter " + clkdBtnTxt);
		/* RPC TODO try to reuse same RPC 1.Maybe save result from initial RPC call
		 * in a client
		 */
		QuestionServiceAsync questionService = clientFactory.getContactService();
		questionService.getQuestion(new AsyncCallback<ArrayList<Question>>() {

			@Override
			public void onFailure(Throwable caught) {
				GWT.log("fail RPC! " + caught.getMessage());
			}

			@Override
			public void onSuccess(ArrayList<Question> result) {
				GWT.log("Result from click on Presenter " + result + "clicked btn= " + clkdBtnTxt);
				if( clkdBtnTxt.equals(result.get(0).getCorrectAnsw()) ){
					GWT.log("good answer!");
				}else{
					GWT.log("bad answer!");
				}
			}
		});
		
	}
}