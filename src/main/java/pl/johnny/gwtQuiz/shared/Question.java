package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Model Class.
 * @author jzarewicz
 *
 */
@SuppressWarnings("serial")
public class Question implements IsSerializable {

	private String question;
	private String[] answers;
	private String correctAnsw;
	private String authorData;
	private String categoryData;
	/**Base64 encoded image represented as String/ */
	private String questionImageData;

	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public Question() {
	};

	public Question(String questionData,String questionImageData, String[] contactsLastNameData, String correctAnswersData, 
			String authorData, String categoryData) {
		this.question = questionData;
		this.questionImageData = questionImageData;
		this.answers = contactsLastNameData;
		this.correctAnsw = correctAnswersData;
		this.authorData = authorData;
		this.categoryData = categoryData;
	}

	public String getQuestion() {
		return question;
	}
	
	/**Get Base64 encoded image represented as String. */
	public String getQuestionImage() {
		return questionImageData;
	}

	/**
	 * Get all questions from an array <br/>
	 * Handy e.g in getting size() of questions array
	 * 
	 * @return String[] 
	 */
	public String[] getAnswers() {
		return answers;
	}

	/**
	 * Get specific answer from answers array
	 * @param i
	 * @return Specific question from an array in a String
	 */
	public String getAnswer(int i) {
		return answers[i];
	}

	public String getCorrectAnsw() {
		return correctAnsw;
	}

	public String getAuthor() {
		return authorData;
	}

	public String getCategory() {
		return categoryData;
	}
}
