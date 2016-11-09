package pl.johnny.gwtQuiz.client;

import java.sql.SQLException;
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
	ArrayList<Question> getQuestions();

	ArrayList<Question> getShuffledQuestions();

	ArrayList<UserScore> getUserScores();

	ArrayList<UserScore> insertUserScore(UserScore userScore);

	void updateUserScore(UserScore userScore);

	void deleteUserScore(UserScore userScore);
	
	/**
	 * {"1", "Geografia},
	 * {"2", "Muzyka"},
	 * {"3", "Polityka"}
	 * @return String[][]
	 */
	public String[][] getCategories();
	
	public String[][] getCategory(String category);

	public void insertUserTmpQuestion(Question userQuestion)
			throws IllegalArgumentException, ConstraintViolationException;

	ArrayList<Question> getTmpQuestions();

	void deleteUserTmpQuestion(String questionID);

	void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID) throws IllegalArgumentException,
    ConstraintViolationException;

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
	 */
	String loginUser(User user) throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException;
	
	/**
	 * Check for session validity.
	 * 
	 * @param sessionID
	 * @return User Email and Type if session is valid, null otherwise.
	 */
	String[][] validateSession(String sessionID);

	boolean logOutUser(String sessionID);

	void insertNewCategory(String newCategory);

	void deleteCategory(String categoryToDelete) throws SQLConstraintException;

	void updateCategory(String updatedCategory, int categoryID);

	void insertNewUser(User newUser) throws IllegalArgumentException, SQLConstraintException;

}