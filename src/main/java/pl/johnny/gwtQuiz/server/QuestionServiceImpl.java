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
	public String[][] getCategories() {
		return questionServiceDBConn.getCategories();
	}
	
	@Override
	public String[][] getCategory(String category) {
		return questionServiceDBConn.getCategory(category);
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
	public String[][] loginUser(User user) throws IllegalArgumentException, pl.johnny.gwtQuiz.shared.FailedLoginException {
		
		//Check if fields are non 0 length.
		if (user.email.trim().isEmpty() || user.password.trim().isEmpty()) {
			throw new IllegalArgumentException("No given mail or password in QuestionServiceImpl.loginUser()");
		}

		// Saved in a variable to avoid duplicated database calling.
		String[] selectedUser = questionServiceDBConn.getUser(user);

		if (selectedUser[0] == "No such user") {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException("No such user");
		}

		if (BCrypt.checkpw(user.password, selectedUser[1])) {
			// Save user email and type as String[] in a session.
			
			String[][] userEmailAndType = new String[1][2];
			userEmailAndType[0][0] = selectedUser[0];
			userEmailAndType[0][1] = selectedUser[2];
			
			this.getThreadLocalRequest().getSession().setAttribute("userEmailAndType", userEmailAndType);
			
			// Return session id.
			return new String[][]{{ this.getThreadLocalRequest().getSession().getId(), selectedUser[0],  selectedUser[2] }};
		} else {
			throw new pl.johnny.gwtQuiz.shared.FailedLoginException("Bad password");
		}
	};

	@Override
	public String[][] validateSession(String sessionID) {

		String[][] sessionUser = (String[][]) this.getThreadLocalRequest().getSession(true).getAttribute("userEmailAndType");
		
		if (this.getThreadLocalRequest().getSession().getId().equals(sessionID) ) {
			return sessionUser;
		} 
		else {
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
	public void insertNewCategory(String newCategory) {
		questionServiceDBConn.insertNewCategory(newCategory);
	}

	@Override
	public void deleteCategory(String categoryToDelete) throws SQLConstraintException{
		
		//Execute delete and if constraint occcures, throw exception to the client.
		try {
			questionServiceDBConn.deleteCategory(categoryToDelete);
		} catch (Exception e) {
			throw new SQLConstraintException(e.getMessage());
		}
	}
	
	@Override
	public void updateCategory(String updatedCategory, int categoryID) {
		questionServiceDBConn.updateCategory(updatedCategory, categoryID);
	}
	
	@Override
	public void insertNewUser(User newUser) throws IllegalArgumentException, SQLConstraintException{
		
		//Check if fields are non 0 length.
		if (newUser.email.trim().isEmpty() || newUser.password.trim().isEmpty()) {
			throw new IllegalArgumentException("No given mail or password in QuestionServiceImpl.loginUser()");
		}
		
		//Encrypt new user password and update user password from plain text to hashed 
		newUser.password = BCrypt.hashpw(newUser.password, BCrypt.gensalt());
		//Hand user model to database...
		try {
			questionServiceDBConn.insertNewUser(newUser);
		} catch (Exception e) {
			throw new SQLConstraintException(e.getMessage());
		}
	}
}