package pl.johnny.gwtQuiz.client.ui;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Alert;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

import pl.johnny.gwtQuiz.client.ui.widgets.PanelWidget;
import pl.johnny.gwtQuiz.shared.Question;

/**
 * View interface. Extends IsWidget so a view impl can easily provide
 * its container widget.
 *
 * @author jzarewicz
 */
public interface AdminView extends IsWidget
{
	void setPresenter(Presenter listener);
	
	public interface Presenter {
		void goTo(Place place);

		void fetchAndBuildPanelWithTmpQuestions();

		void acceptUserTmpQuestion(Question acceptedQuestion, String tmpQuestionID);

		void deleteUserTmpQuestion(String questionID);

		void addCategory(String newCategory, final List<String> list);

		void removeCategory(String categoryToDelete, final List<String> list, Alert contraintAlert);

		void updateCategory(String updatedCategory, int categoryID);
		
	}

	void setCategories(String[] categories);

	void buildAndFillPanelsWithTmpQuestions(ArrayList<Question> tmpQuestion);

	void refreshPanel();

	PanelWidget[] getPanelWidgets();

	void buildCategoriesCellList(String[] categories);
}