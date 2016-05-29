package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.shared.Question;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget. 
 * 
 * @author drfibonacci
 */
public interface QuestionView extends IsWidget
{
	void setPresenter(Presenter listener);
	void setQuestion(String question);
	public void setAnswers(Question answers);
	
	public interface Presenter {
		void goTo(Place place);
		void onAnswerBtnClicked(String clkdBtnTxt);
	}
}