package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
		QuestionService {

	private final ArrayList<Question> questions = new ArrayList<Question>();

	public QuestionServiceImpl() {
		initQuestions();
	}

	private void initQuestions() {
		QuestionServiceDatabaseConn dbConn = new QuestionServiceDatabaseConn();
		for(int i = 0; i < dbConn.questionsData.length; ++i) {
			Question question = new Question(dbConn.questionsData[i], dbConn.answersData[i], dbConn.correctAnswersData[i]);
			questions.add(question);
		}
	}

	@Override
	public ArrayList<Question> getQuestion() {
		return questions;
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
