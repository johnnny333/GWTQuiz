package pl.johnny.gwtQuiz.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class NewQuestionEvent extends GwtEvent<NewQuestionEvent.Handler> {

	public interface Handler extends EventHandler {
		void onNewQuestion(NewQuestionEvent event);
	}

	public static final Type<NewQuestionEvent.Handler> TYPE = new Type<NewQuestionEvent.Handler>();
	private final int currentQuestion;

	public NewQuestionEvent(int currentQuestion) {
		this.currentQuestion = currentQuestion;	
		}

	@Override
	public final Type<NewQuestionEvent.Handler> getAssociatedType() {
		return TYPE;
	}
	
	/**
	 * Get current question.
	 * @return integer which corresponds to the current question,</br>
	 * eg: int 0 = first question in quiz.
	 */
	public int getCurrentQuestionInt() {
		return currentQuestion;
	}

	@Override
	protected void dispatch(NewQuestionEvent.Handler handler) {
		handler.onNewQuestion(this);		
	}
}
