package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.Cookie;
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
import pl.johnny.gwtQuiz.shared.SQLConstraintException;
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
	public ArrayList<Question> getQuestions() throws Exception  {
		return questionServiceDBConn.getQuestions();
	}

	@Override
	public ArrayList<Question> getShuffledQuestions() throws Exception {
		ArrayList<Question> shuffledQuestions = questionServiceDBConn.getQuestions();
		Collections.shuffle(shuffledQuestions);
		return shuffledQuestions;
	}

	@Override
	public ArrayList<UserScore> getUserScores() throws Exception {
		System.out.println("Hello from Johnny and getUserScores()");
		return questionServiceDBConn.getUserScores();
	}

	@Override
	public ArrayList<UserScore> insertUserScore(UserScore userScore) throws Exception {
		questionServiceDBConn.insertUserScore(userScore);
		return questionServiceDBConn.getUserScores();
	}

	@Override
	public void updateUserScore(UserScore userScore) throws Exception {
		questionServiceDBConn.updateUserScore(userScore);
	}

	@Override
	public void deleteUserScore(UserScore userScore) throws Exception {
		questionServiceDBConn.deleteUserScore(userScore);
	}

	@Override
	public String[][] getCategories() throws Exception {
		return questionServiceDBConn.getCategories();
	}

	@Override
	public String[][] getCategory(String category) throws Exception {
		return questionServiceDBConn.getCategory(category);
	}

	@Override
	public void insertUserTmpQuestion(Question userQuestion)
			throws Exception {

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
	public ArrayList<Question> getTmpQuestions() throws Exception {
		return questionServiceDBConn.getTmpQuestions();
	}

	@Override
	public void deleteUserTmpQuestion(String questionID) throws Exception {
		questionServiceDBConn.deleteUserTmpQuestion(questionID);
	}

	@Override
	public void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID)
			throws Exception {

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
	public String[][] loginUser(User user)
			throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException, Exception {

		// Check if fields are non 0 length.
		if (user.email.trim().isEmpty() || user.password.trim().isEmpty()) {
			throw new IllegalArgumentException("No given mail or password in QuestionServiceImpl.loginUser()");
		}

		/**
		 * [0] - user email, [1] - user password, [2] - user type. Saved in a
		 * variable to avoid duplicated database calling.
		 */
		String[] selectedUser = questionServiceDBConn.getUser(user);

		if (selectedUser[0] == "No such user") {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException(selectedUser[0]);
		}

		if (BCrypt.checkpw(user.password, selectedUser[1])) {
			
			//Save random cookie UUID 
			String cookieUUID = UUID.randomUUID().toString();
			questionServiceDBConn.insertUserUUID(user, cookieUUID);
			
			Cookie cookie = new Cookie("user", cookieUUID);
			// Expire the cookie in five minutes (5 * 60), it's UTC ('Z' at the end!).
			cookie.setMaxAge(60 * 60 * 24 * 7);
			this.getThreadLocalResponse().addCookie(cookie);

			this.getThreadLocalRequest().getSession().setAttribute("userEmailAndType",
					new String[][] { { selectedUser[0], selectedUser[2] } });

			// Return session id.
			return new String[][] {
					{ this.getThreadLocalRequest().getSession().getId(), selectedUser[0], selectedUser[2], cookieUUID } };
		} else {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException("Bad password");
		}
	};

	@Override
	public String[][] validateSession(String sessionID, String cookieUUID) throws Exception {

		if (this.getThreadLocalRequest().getSession().getId().equals(sessionID)) {
			System.out.println("With attribute");
			return (String[][]) this.getThreadLocalRequest().getSession(true).getAttribute("userEmailAndType");

		} else if (cookieUUID != null) {

			for (Cookie serverCookies : this.getThreadLocalRequest().getCookies()) {
				String[] selectedUser = null;

				if (serverCookies.getValue().equals(cookieUUID)) {
					selectedUser = questionServiceDBConn.getUserUUID(cookieUUID);
					
					System.out.println("Cookie name: " + serverCookies.getName());
					System.out.println("Cookie value: " + serverCookies.getValue());
					System.out.println("Cookie lifetime: " + serverCookies.getMaxAge());
					
					this.getThreadLocalRequest().getSession().setAttribute("userEmailAndType",
							new String[][] { { selectedUser[0], selectedUser[2] } });
					
					return new String[][] { { selectedUser[0], selectedUser[2] } };
				}
			}
			return null;
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
			this.getThreadLocalRequest().getSession().invalidate();
			return false;
		}
	}

	@Override
	public void insertNewCategory(String newCategory) throws Exception {
		questionServiceDBConn.insertNewCategory(newCategory);
	}

	@Override
	public void deleteCategory(String categoryToDelete) throws SQLConstraintException {

		// Execute delete and if constraint occcures, throw exception to the
		// client.
		try {
			questionServiceDBConn.deleteCategory(categoryToDelete);
		} catch (Exception e) {
			throw new SQLConstraintException(e.getMessage());
		}
	}

	@Override
	public void updateCategory(String updatedCategory, int categoryID) throws Exception {
		questionServiceDBConn.updateCategory(updatedCategory, categoryID);
	}

	@Override
	public void insertNewUser(User newUser) throws IllegalArgumentException, SQLConstraintException {

		// Check if fields are non 0 length.
		if (newUser.email.trim().isEmpty() || newUser.password.trim().isEmpty()) {
			throw new IllegalArgumentException("No given mail or password in QuestionServiceImpl.loginUser()");
		}

		// Encrypt new user password and update user password from plain text to
		// hashed
		newUser.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
		// Hand user model to database...
		try {
			questionServiceDBConn.insertNewUser(newUser);
		} catch (Exception e) {
			throw new SQLConstraintException(e.getMessage());
		}
	}
}
