package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pl.johnny.gwtQuiz.shared.Question;

public interface QuestionServiceAsync {

	void getQuestion(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getQuestionId(Integer i, AsyncCallback<Question> callback);
	
}
