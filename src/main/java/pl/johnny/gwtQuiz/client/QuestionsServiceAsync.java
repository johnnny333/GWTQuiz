package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.shared.Question;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuestionsServiceAsync {

	public void getQuestion(Integer id, AsyncCallback<Question> callback);
	
}
