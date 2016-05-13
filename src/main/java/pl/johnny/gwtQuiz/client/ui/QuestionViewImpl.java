package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

import pl.johnny.gwtQuiz.client.ui.MainMenuView.Presenter;

public class QuestionViewImpl extends Composite implements QuestionView
{

	SimplePanel viewPanel = new SimplePanel(); 
	Element nameSpan = DOM.createSpan();

	public QuestionViewImpl()
	{
		viewPanel.getElement().appendChild(nameSpan);
		initWidget(viewPanel);
	}

	@Override
	public void setName(String name)
	{
		nameSpan.setInnerText("Good-bye, " + name);
	}

	@Override
	public void setPresenter(Presenter listener) {
		
	}

	@Override
	public Boolean getName() {
		if (nameSpan.getInnerText() == "") return null;
		return true;
	}
}
