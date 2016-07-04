package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.NavbarBrand;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;

public class NavBarViewImpl extends Composite implements NavBarView {

	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	interface NavBarViewImplUiBinder extends UiBinder<Widget, NavBarViewImpl> {
	}
	
	private Presenter listener;
	
	@UiField NavbarBrand navBarBrand;
	@UiField AnchorListItem newGameAnchor;
	@UiField AnchorListItem highScoreAnchor;
	@UiField AnchorListItem addQuestionsAnchor;

	public NavBarViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
	
	@UiHandler("navBarBrand")
	void onNavBarBrandClicked(ClickEvent e) {
		navBarBrand.getText();
		if (listener != null) {
			listener.goTo(new MainMenuPlace("MainMenu"));
		}
	}
	
	@UiHandler("newGameAnchor")
	void onNewGameAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new QuestionPlace("Quiz!"));
		}
	}
	
	@UiHandler("highScoreAnchor")
	void onHighScoreAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new HighScoresPlace("HighScores"));
		}
	}
	
	@UiHandler("addQuestionsAnchor")
	void onAddQuestionsAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new AddQuestionsPlace("HighScores"));
		}
	}
	
	@Override
	public void setAnchorListItemActive(int whichAnchorToHighlight){
		AnchorListItem[] anchorListItems = new AnchorListItem[]{newGameAnchor,highScoreAnchor,
				addQuestionsAnchor};
		
		/*
		 * Reset all nav bar anchors and navBarBrand (which don't have setActive() to non-active 
		 * on every call.
		 */
		
		for(int i=0; i < anchorListItems.length; i++){anchorListItems[i].setActive(false);};
		navBarBrand.getElement().getStyle().setProperty("backgroundColor", "#F8F8F8");
		
		switch(whichAnchorToHighlight){
			case 0 : navBarBrand.getElement().getStyle().setProperty("backgroundColor", "#E7E7E7");
			break;
			
			case 1 : anchorListItems[0].setActive(true);
			break;
			
			case 2 : anchorListItems[1].setActive(true);
			break;
			
			case 3 : anchorListItems[2].setActive(true);
			break;
		}
	}
}