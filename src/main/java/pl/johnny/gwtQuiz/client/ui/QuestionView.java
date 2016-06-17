package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.shared.Question;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 * 
 * @author jzarewicz
  */
public interface QuestionView extends IsWidget
{	
	/** 
	 * Function used by corresponding Activity to establish communication
	 * between said Activity and this View.
	 * 
	 * @param listener
	 */
	void setPresenter(Presenter listener);
	
	/** 
	 * Sets question text in a View. 
	 * 
	 * @param question
	 */
	void setQuestion(String question);
	
	/** 
	 * Sets answers in views buttons.
	 * 
	 * @param answers
	 */
	
	void setQuestionImage(String questionImageURL,boolean isVisible);
	
	public void setAnswers(Question answers);
	
	/** 
	 * Shows modal (at the end of the quiz) with user points.
	 * 
	 * @param userPoints given from QuestionActivity
	 * */
	void showModal(int userPoints);
	
	void setPrvBtnVsbl(boolean bool);
	
	/** 
	 * Displays actual question number in a widget View.
	 * 
	 * @param questionNumber given from QuestionActivity
	 */
	void setQuestionCounter(int questionNumber,int questionsNumber );
	
	/** 
	 * Displays user points in a widget View.
	 * 
	 * @param userPoints given from QuestionActivity
	 */
	void setPointsCounter(int userPoints);
	
	void setCategoryField(String category);
	
	void setAuthorField(String timerSeconds);
	
	void setProgressBar(Double percent);
	
	public interface Presenter {
		void goTo(Place place);
		
		/** 
		 * Function to check for correct answer and manage points,
		 * show next question and
		 * end quiz if the actual question is last.
		 *  
		 * @param clkdBtnTxt
		 */
		void onAnswerBtnClicked(String clkdBtnTxt);
		
		/** Takes user to previous question relative to the actual.*/
		void onPreviousBtnClicked();
	}

}