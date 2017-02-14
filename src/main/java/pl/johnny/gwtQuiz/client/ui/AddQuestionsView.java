package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.Modal;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.shared.Question;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface AddQuestionsView extends IsWidget
{
	void setPresenter(Presenter listener);
	
	/**
	 * Sets categories in ListBox.
	 */
	void setCategories(String[][] categories);

	public interface Presenter {
		void goTo(Place place);
		/**
		 * Insert user question into question_tmp table and user answers into answers_tmp table.
		 * @param userQuestion
		 */
		void insertUserQuestion(Question userQuestion);
		
	}
	
	/**
	 * Display error message in a view.
	 * @param propertyPath - used to determine to which field append error message.
	 * @param errorMessage - actual error message.
	 */
	void setServerErrorMessage(String propertyPath, String errorMessage);

	void formReset();

	void showConfirmationModal();

	Modal getModalLoading();
}