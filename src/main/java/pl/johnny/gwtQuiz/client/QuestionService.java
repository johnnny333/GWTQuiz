package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import javax.validation.ConstraintViolationException;

import org.hibernate.validator.engine.ValidationSupport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.SQLConstraintException;
import pl.johnny.gwtQuiz.shared.User;
import pl.johnny.gwtQuiz.shared.UserScore;

@RemoteServiceRelativePath("questionService")
public interface QuestionService extends RemoteService {
	
	ArrayList<Question> getQuestions() throws Exception;

	ArrayList<Question> getShuffledQuestions() throws Exception;

	ArrayList<UserScore> getUserScores() throws Exception;

	ArrayList<UserScore> insertUserScore(UserScore userScore) throws Exception;

	void updateUserScore(UserScore userScore) throws Exception;

	void deleteUserScore(UserScore userScore) throws Exception;
	
	/**
	 * {"1", "Geografia},
	 * {"2", "Muzyka"},
	 * {"3", "Polityka"}
	 * @return String[][]
	 * @throws Exception 
	 */
	public String[][] getCategories() throws Exception;
	
	public String[][] getCategory(String category) throws Exception;

	public void insertUserTmpQuestion(Question userQuestion)
			throws IllegalArgumentException, ConstraintViolationException, Exception;

	ArrayList<Question> getTmpQuestions() throws Exception;

	void deleteUserTmpQuestion(String questionID) throws Exception;

	void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID) throws IllegalArgumentException,
    ConstraintViolationException, Exception;

	/**
	 * Force Hibernate validator implementations to be available for
	 * serialization.
	 */
	ValidationSupport dummy();
	
	/**
	 * Check for user in database and if exist, check for password validity.
	 * 
	 * @param user model which contains user mail and password.
	 * @throws IllegalArgumentException thrown if user email field is blank.
	 * @throws pl.johnny.gwtQuiz.shared.FailedLoginException thrown if either provided 
	 * user mail was not found or password was invalid.
	 * @return (is user is valid) String[0] = email, String[1] = user email,
	 * String[2] = type - user type determines user privileges in an app.
	 * @throws Exception 
	 */
	String[][] loginUser(User user) throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException, Exception;
	
	/**
	 * Check for session validity.
	 * 
	 * @param sessionID
	 * @return User Email and Type if session is valid, null otherwise.
	 * @throws Exception 
	 */
	String[][] validateSession(String sessionID, String userEmail) throws Exception;

	boolean logOutUser(String sessionID);

	void insertNewCategory(String newCategory) throws Exception;

	void deleteCategory(String categoryToDelete) throws SQLConstraintException;

	void updateCategory(String updatedCategory, int categoryID) throws Exception;

	void insertNewUser(User newUser) throws IllegalArgumentException, SQLConstraintException;

}