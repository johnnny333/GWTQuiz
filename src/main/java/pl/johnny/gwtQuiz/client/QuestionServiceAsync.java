package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.hibernate.validator.engine.ValidationSupport;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.User;
import pl.johnny.gwtQuiz.shared.UserScore;

public interface QuestionServiceAsync {
	void getQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getShuffledQuestions(AsyncCallback<ArrayList<Question>> asyncCallback);

	void getUserScores(AsyncCallback<ArrayList<UserScore>> asyncCallback);

	void insertUserScore(UserScore userScore, AsyncCallback<ArrayList<UserScore>> asyncCallback);

	void updateUserScore(UserScore userScore, AsyncCallback<Void> asyncCallback);

	void deleteUserScore(UserScore userScore, AsyncCallback<Void> asyncCallback);
	
	/**
	 * {"1", "Geografia},
	 * {"2", "Muzyka"},
	 * {"3", "Polityka"}
	 * @return String[][]
	 */
	void getCategories(AsyncCallback<String[][]> asyncCallback);
	
	/**
	 * Returns single entity containing category and id. 
	 * @param category e.g {"3", "Polityka"}
	 * @param callback
	 */
	void getCategory(String category, AsyncCallback<String[][]> callback);	

	void insertUserTmpQuestion(Question userQuestion, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException,
    ConstraintViolationException;

	void getTmpQuestions(AsyncCallback<ArrayList<Question>> asynchCallback);

	void deleteUserTmpQuestion(String questionID, AsyncCallback<Void> asyncCallback);

	void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException,
    ConstraintViolationException;
	
	 /**
	   * Force hibernate validator implementations to be available for
	   * serialization.
	   */
	void dummy(AsyncCallback<ValidationSupport> callback);

	void loginUser(User user, AsyncCallback<String[][]> asyncCallback);
	
	/**
	 * Return session assigned to user and his type.
	 * @param sessionID 
	 * @param asyncCallback
	 * @return [0][0] - user email, [0][1] user type. Result is Null if there is no user assigned to session.
	 */
	void validateSession(String sessionID, AsyncCallback<String[][]> asyncCallback);

	void logOutUser(String sessionID, AsyncCallback<Boolean> asyncCallback);

	void insertNewCategory(String newCategory, AsyncCallback<Void> asyncCallback);

	void deleteCategory(String categoryToDelete, AsyncCallback<Void> asyncCallback);

	void updateCategory(String updatedCategory, int categoryID, AsyncCallback<Void> asyncCallback);

	void insertNewUser(User newUser, AsyncCallback<Void> asyncCallback);

}