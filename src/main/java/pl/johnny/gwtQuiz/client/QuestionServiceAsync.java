package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pl.johnny.gwtQuiz.shared.Question;

public interface QuestionServiceAsync {
	void getQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getShuffledQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);	
}
