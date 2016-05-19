package pl.johnny.gwtQuiz.server;

import pl.johnny.gwtQuiz.client.QuestionService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class QuestionServiceImpl extends RemoteServiceServlet implements
    QuestionService{

  private static final String[] contactsFirstNameData = new String[] {
      "Hollie", "Emerson", "Healy", "Brigitte"};
  
  private final String[][] contactsLastNameData = new String[][] {
		  {"Voss", "Milton", "Colette", "Cobb", "Lockhart", "Engle"},
		  {"Pacheco", "Blake", "Horton", "Daniel", "Childers", "Starnes"},
		  {"Carson", "Kelchner", "Hutchinson", "Underwood", "Rush", "Bouchard"}, 
		  {"Louis", "Andrews", "English", "Snedden"}};
  
//  private final String[] contactsEmailData = new String[] {
//      "mark@example.com", "hollie@example.com", "boticario@example.com",
//      "emerson@example.com", "healy@example.com", "brigitte@example.com",
//      "elba@example.com", "claudio@example.com", "dena@example.com",
//      "brasilsp@example.com", "parker@example.com", "derbvktqsr@example.com",
//      "qetlyxxogg@example.com", "antenas_sul@example.com",
//      "cblake@example.com", "gailh@example.com", "orville@example.com",
//      "post_master@example.com", "rchilders@example.com", "buster@example.com",
//      "user31065@example.com", "ftsgeolbx@example.com"};
  
      
//  private final HashMap<Integer, String> questions = new HashMap<Integer, String>();

  public QuestionServiceImpl() {
//    initContacts();
  }
  
  private void initContacts() {
    // TODO: Create a real UID for each contact
    //
//    for (int i = 0; i < contactsFirstNameData.length && i < contactsLastNameData.length;) {
//    	Question question = new Question(contactsFirstNameData[i], contactsLastNameData[i]);
//      questions.put(1, "sdfdsf"); 
//    }
  }

@Override
public String getQuestion() {
//	System.out.println("" + questions);
	return "Hello";
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
