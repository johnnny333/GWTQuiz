package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;
import java.util.Collections;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.UserScore;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
		QuestionService {
	
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
	public void insertUserTmpQuestion(Question userQuestion){
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

	//  @Override
	//public Contact addContact(Contact contact) {
	//    contact.setId(String.valueOf(contacts.size()));
	//    contacts.put(contact.getId(), contact); 
	//    return contact;
	//  }
	//
	//  @Override
	//public Contact updateContact(Contact contact) {
	//    contacts.remove(contact.getId());
	//    contacts.put(contact.getId(), contact); 
	//    return contact;
	//  }
	//
	//  @Override
	//public Boolean deleteContact(String id) {
	//    contacts.remove(id);
	//    return true;
	//  }

	//  @Override
	//public ArrayList<ContactDetails> deleteContacts(ArrayList<String> ids) {
	//
	//    for (int i = 0; i < ids.size(); ++i) {
	//      deleteContact(ids.get(i));
	//    }
	//    
	//    return getContactDetails();
	//  }

	//  @Override
	//public ArrayList<ContactDetails> getContactDetails() {
	//    ArrayList<ContactDetails> contactDetails = new ArrayList<ContactDetails>();
	//    
	//    Iterator<String> it = contacts.keySet().iterator();
	//    while(it.hasNext()) { 
	//      Contact contact = contacts.get(it.next());          
	//      contactDetails.add(contact.getLightWeightContact());
	//    }
	//    
	//    return contactDetails;
	//  }

}
