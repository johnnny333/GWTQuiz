package pl.johnny.gwtQuiz.server;

import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.shared.Question;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
    QuestionService{

  private static final String[] questionsData = new String[] {
      "Ile strun ma typowa gitara elektryczna?",
      "Jak na nazwisko ma Barack?", 
      "W jakim województwie leży Jurata?",
      "W którym roku wydano Nevermind Nirvany?"};
  
  private static final String[][] answersData = new String[][] {
		  {"5", "6", "4", "7"},
		  {"Pacheco", "Blake", "Horton", "Obama"},
		  {"Mazowieckim", "Śląskim", "Pomorskim", "Łódzkim"}, 
		  {"1994", "2001", "1984", "2007"}};
  
  private static String[] correctAnswersData = new String[] {
      answersData[0][1],
      answersData[1][3],
      answersData[2][2],
      answersData[3][0]
//		  "1","2","3","4"
		  };
  
      
  private final ArrayList<Question> questions = new ArrayList<Question>();

  public QuestionServiceImpl() {
    initContacts();
  }
  
  private void initContacts() {
    for (int i = 0; i < questionsData.length ; ++i) {
    	Question question = new Question(questionsData[i], answersData[i], correctAnswersData[i]);
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
