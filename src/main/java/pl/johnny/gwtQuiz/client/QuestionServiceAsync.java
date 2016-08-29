package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.UserScore;

public interface QuestionServiceAsync {
	void getQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getShuffledQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getUserScores(AsyncCallback<ArrayList<UserScore>> asyncCallback);

	void insertUserScore(UserScore userScore, AsyncCallback<ArrayList<UserScore>> asyncCallback);

	void updateUserScore(UserScore userScore, AsyncCallback<Void> asyncCallback);

	void deleteUserScore(UserScore userScore, AsyncCallback<Void> asyncCallback);

	void getCategories(AsyncCallback<String[]> asyncCallback);

	void insertUserTmpQuestion(Question userQuestion, AsyncCallback<Void> asyncCallback);

	void getTmpQuestions(AsyncCallback<ArrayList<Question>> asynchCallback);	
}