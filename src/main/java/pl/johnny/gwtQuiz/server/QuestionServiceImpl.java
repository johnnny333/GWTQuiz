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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.ServerGroup;
import pl.johnny.gwtQuiz.shared.UserScore;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
		QuestionService {
	
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
	public void insertUserTmpQuestion(Question userQuestion) throws IllegalArgumentException,
    ConstraintViolationException {
		
		// Verify that the inputs is valid.
	    Set<ConstraintViolation<Question>> violations = validator.validate(userQuestion,
	        Default.class, ServerGroup.class);
	    
	    if (!violations.isEmpty()) {
	      Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(
	          violations);
	      throw new ConstraintViolationException(temp);
	    }
		
		questionServiceDBConn.insertUserQuestion(userQuestion);
	}
	
	@Override
	public ArrayList<Question> getTmpQuestions() {
		return questionServiceDBConn.getTmpQuestions();
	}
	
	@Override
	public void deleteUserTmpQuestion(String questionID){
		questionServiceDBConn.deleteUserTmpQuestion(questionID);
	}
	
	@Override
	public void acceptUserTmpQuestion(Question acceptedQuestion,String tmpQuestionID) throws IllegalArgumentException,
    ConstraintViolationException {
		
		// Verify that the inputs is valid.
	    Set<ConstraintViolation<Question>> violations = validator.validate(acceptedQuestion,
	        Default.class, ServerGroup.class);
	    
	    if (!violations.isEmpty()) {
	      Set<ConstraintViolation<?>> temp = new HashSet<ConstraintViolation<?>>(
	          violations);
	      throw new ConstraintViolationException(temp);
	    }
		
		questionServiceDBConn.acceptUserTmpQuestion(acceptedQuestion, tmpQuestionID);
	}
}