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

	boolean loginUser(User user) throws NullPointerException, FailedLoginException;
}
