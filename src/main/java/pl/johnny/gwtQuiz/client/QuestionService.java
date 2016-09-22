package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import javax.security.auth.login.FailedLoginException;
import javax.validation.ConstraintViolationException;

import org.hibernate.validator.engine.ValidationSupport;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pl.johnny.gwtQuiz.shared.Question;
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

	public String[] getCategories();

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
	 * @return true if user exist and provided password is valid.
	 * @throws IllegalArgumentException thrown if user email field is blank.
	 * @throws pl.johnny.gwtQuiz.shared.FailedLoginException thrown if either provided 
	 * user mail was not found or password was invalid.
	 */
	String loginUser(User user) throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException;
	
	/**
	 * Check for session validity.
	 * 
	 * @param sessionID
	 * @return true if session is valid, false otherwise.
	 */
	boolean validateSession(String sessionID);
}