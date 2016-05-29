package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Question implements IsSerializable {
	
	private String question;
	private String[] answers;
	private String correctAnsw;
	
	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public Question(){};
	
	public Question(String questionData, String[] contactsLastNameData, String correctAnswersData) {
		this.question = questionData;
		this.answers = contactsLastNameData;
		this.correctAnsw = correctAnswersData;
	}

	public String getQuestion() { return question; }
	/**
	 * Get all questions from an array
	 * @return String[] 
	 */
	public String[] getAnswers() { return answers; }
	/**
	 * Get specific answer from answers array
	 * @param i
	 * @return Specific question from an array in a String
	 */
	public String getAnswer(int i) { return answers[i]; }
	public String getCorrectAnsw() {return correctAnsw; }
}
