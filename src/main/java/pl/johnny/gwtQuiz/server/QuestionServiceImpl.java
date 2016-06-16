package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
		QuestionService {
	
	private QuestionServiceDatabaseConn questionServiceDBConn;

	public QuestionServiceImpl() {
		questionServiceDBConn = new QuestionServiceDatabaseConn();
	}

	@Override
	public ArrayList<Question> getQuestion() {
		return questionServiceDBConn.questions;
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
