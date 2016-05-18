package pl.johnny.gwtQuiz.shared;

import java.io.Serializable;

public class Question implements Serializable {
	
	public String id;
	public String question;
	public String[] answers;

	public Question(){}
	
	public Question(String question, String[] contactsLastNameData) {
//		this.id = id;
		this.question = question;
		this.answers = contactsLastNameData;
	}

//	public String getId() { return id; }

	
}
