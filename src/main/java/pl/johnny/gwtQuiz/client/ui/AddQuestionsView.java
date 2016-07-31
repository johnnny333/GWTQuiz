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
public interface AddQuestionsView extends IsWidget
{
	void setPresenter(Presenter listener);
	
	/**
	 * Sets categories in ListBox.
	 */
	void setCategories(String[] categories);

	public interface Presenter {
		void goTo(Place place);
		/**
		 * Insert user question into question_tmp table and user answers into answers_tmp table.
		 * @param userQuestion
		 */
		void insertUserQuestion(Question userQuestion);
		
		/**
		 * Set image name which was uploaded via UploadWidget.class
		 * into this Activity so its View can use it to construct UserScore model with
		 * said image name.
		 * @param uploadedImageName
		 */
		void setUploadedImageName(String uploadedImageName);
		
		/**
		 * Returns uploaded image name which was sent from UploadWidget.class and set with 
		 * insertUserQuestion(Question userQuestion).
		 * @return uploadedImageName
		 */
		String getUploadedImageName();
	}
}