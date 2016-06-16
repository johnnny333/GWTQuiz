package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class Question implements IsSerializable {

	private String question;
	private String[] answers;
	private String correctAnsw;
	private String authorData;
	private String categoryData;

	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public Question() {
	};

	public Question(String questionData, String[] contactsLastNameData, String correctAnswersData, 
			String authorData, String categoryData) {
		this.question = questionData;
		this.answers = contactsLastNameData;
		this.correctAnsw = correctAnswersData;
		this.authorData = authorData;
		this.categoryData = categoryData;
	}

	public String getQuestion() {
		return question;
	}

	/**
	 * Get all questions from an array <br/>
	 * e.g getting size() of questions array
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
