package pl.johnny.gwtQuiz.client.event;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public class NewQuestionEvent extends GwtEvent<NewQuestionEvent.Handler> {

	public interface Handler extends EventHandler {
		void onNewQuestion(NewQuestionEvent event);
	}

	public static final Type<NewQuestionEvent.Handler> TYPE = new Type<NewQuestionEvent.Handler>();
	private final String string;

	public NewQuestionEvent(String string) {
		this.string = string;	
		}

	@Override
	public final Type<NewQuestionEvent.Handler> getAssociatedType() {
		return TYPE;
	}
	
	public String getString() {
		return string;
	}

	@Override
	protected void dispatch(NewQuestionEvent.Handler handler) {
		handler.onNewQuestion(this);		
	}
}
