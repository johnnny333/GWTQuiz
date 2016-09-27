package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.hibernate.validator.engine.ValidationSupport;
import org.mindrot.jbcrypt.BCrypt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.ServerGroup;
import pl.johnny.gwtQuiz.shared.User;
import pl.johnny.gwtQuiz.shared.UserScore;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements QuestionService {

	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Override
	public ValidationSupport dummy() {
		return null;
	}

	private QuestionServiceDatabaseConn questionServiceDBConn;

	public QuestionServiceImpl() {
		questionServiceDBConn = new QuestionServiceDatabaseConn();
	}

	@Override
	public ArrayList<Question> getQuestions() {
		return questionServiceDBConn.getQuestions();
	}

	@Override
	public ArrayList<Question> getShuffledQuestions() {
		ArrayList<Question> shuffledQuestions = questionServiceDBConn.getQuestions();
		Collections.shuffle(shuffledQuestions);
		return shuffledQuestions;
	}

	@Override
	public ArrayList<UserScore> getUserScores() {
		return questionServiceDBConn.getUserScores();
	}

	@Override
	public ArrayList<UserScore> insertUserScore(UserScore userScore) {
		questionServiceDBConn.insertUserScore(userScore);
		return questionServiceDBConn.getUserScores();
	}

	@Override
	public void updateUserScore(UserScore userScore) {
		questionServiceDBConn.updateUserScore(userScore);
	}

	@Override
	public void deleteUserScore(UserScore userScore) {
		questionServiceDBConn.deleteUserScore(userScore);
	}

	@Override
	public String[] getCategories() {
		return questionServiceDBConn.getCategories();
	}

	@Override
	public void insertUserTmpQuestion(Question userQuestion)
			throws IllegalArgumentException, ConstraintViolationException {

		// Verify that the inputs is valid.
		Set<ConstraintViolation<Question>> violations = validator.validate(userQuestion, Default.class,
				ServerGroup.class);

		if (!violations.isEmpty()) {
			Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(violations);
			throw new ConstraintViolationException(temp);
		}

		questionServiceDBConn.insertUserQuestion(userQuestion);
	}

	@Override
	public ArrayList<Question> getTmpQuestions() {
		return questionServiceDBConn.getTmpQuestions();
	}

	@Override
	public void deleteUserTmpQuestion(String questionID) {
		questionServiceDBConn.deleteUserTmpQuestion(questionID);
	}

	@Override
	public void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID)
			throws IllegalArgumentException, ConstraintViolationException {

		// Verify that the inputs is valid.
		Set<ConstraintViolation<Question>> violations = validator.validate(acceptedQuestion, Default.class,
				ServerGroup.class);

		if (!violations.isEmpty()) {
			Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(violations);
			throw new ConstraintViolationException(temp);
		}
		questionServiceDBConn.acceptUserTmpQuestion(acceptedQuestion, tmpQuestionID);
	}

	@Override
	public String loginUser(User user) throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException {

		if (user.email.trim() == null) {
			throw new IllegalArgumentException("No given mail in QuestionServiceImpl.loginUser()");
		}

		// Saved in a variable to avoid duplicated database calling.
		String[] userAndhashedPasswordFromDB = questionServiceDBConn.getUser(user);

		if (userAndhashedPasswordFromDB[0] == "No such user") {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException("No such user");
		}

		if (BCrypt.checkpw(user.password, userAndhashedPasswordFromDB[1])) {
			// Save user in a session.
			this.getThreadLocalRequest().getSession(true).setAttribute("userEmail", user.email);
			// Send session id and email as response.
			return this.getThreadLocalRequest().getSession(true).getId();
		} else {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException("Bad password");
		}
	};

	@Override
	public String validateSession(String sessionID) {

		String userEmail = (String) this.getThreadLocalRequest().getSession().getAttribute("userEmail");

		if (this.getThreadLocalRequest().getSession().getId().equals(sessionID) && userEmail != null) {
			return userEmail;
		} else {
			return null;
		}
	}

	@Override
	public boolean logOutUser(String sessionID) {

		if (this.getThreadLocalRequest().getSession().getId().equals(sessionID)) {
			this.getThreadLocalRequest().getSession().invalidate();
			return true;
		} else {
			return false;
		}
	}
}