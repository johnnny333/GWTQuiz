package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Question implements IsSerializable {
	
	public String id;
	public String question;
	public String[] answers;

	public Question(){}
	
	public Question(String question, String[] contactsLastNameData) {
//		this.id = id;
		this.question = question;
		this.answers = contactsLastNameData;
	}

	public String getQuestion() { return question; }
	public String[] getAnswers() { return answers; }

	
}
