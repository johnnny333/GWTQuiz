package pl.johnny.gwtQuiz.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface QuestionServiceAsync {

//	public void getQuestion(Integer id, AsyncCallback<String> asyncCallback);
	public void getQuestion(AsyncCallback<String> asyncCallback);
	
}
