package pl.johnny.gwtQuiz.client.ui;

import org.gwtbootstrap3.client.ui.AnchorButton;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.gwtbootstrap3.client.ui.DropDownMenu;
import org.gwtbootstrap3.client.ui.ListDropDown;
import org.gwtbootstrap3.client.ui.NavbarBrand;
import org.gwtbootstrap3.client.ui.NavbarCollapse;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.LoginPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;

public class NavBarViewImpl extends Composite implements NavBarView {

	private static NavBarViewImplUiBinder uiBinder = GWT.create(NavBarViewImplUiBinder.class);

	interface NavBarViewImplUiBinder extends UiBinder<Widget, NavBarViewImpl> {
	}

	private Presenter listener;

	@UiField
	NavbarBrand navBarBrand;
	@UiField
	AnchorListItem newGameAnchor;
	@UiField
	AnchorListItem highScoreAnchor;
	@UiField
	AnchorListItem addQuestionsAnchor;
	@UiField
	NavbarCollapse navBarCollapse;
	@UiField
	AnchorButton anchorButton;
	@UiField
	DropDownMenu dropDownMenu;
	@UiField
	AnchorListItem logOutAnchorListItem;
	@UiField
	AnchorListItem adminPanelAnchor;
	@UiField
	AnchorListItem signUpAnchor;

	public NavBarViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		anchorButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				signUpAnchor.setActive(false);
			}
		});
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
			navBarCollapse.hide();
		}
	}

	@UiHandler("newGameAnchor")
	void onNewGameAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new QuestionPlace("Quiz!"));
			navBarCollapse.hide();
		}
	}

	@UiHandler("highScoreAnchor")
	void onHighScoreAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new HighScoresPlace("HighScores"));
			navBarCollapse.hide();
		}
	}

	@UiHandler("addQuestionsAnchor")
	void onAddQuestionsAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new AddQuestionsPlace(""));
			navBarCollapse.hide();
		}
	}

	@UiHandler("adminPanelAnchor")
	void onAdminPanelAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new AdminPlace(""));
			navBarCollapse.hide();
		}
	}

	@Override
	public void setAnchorListItemActive(int whichAnchorToHighlight) {
		AnchorListItem[] anchorListItems = new AnchorListItem[] { newGameAnchor, highScoreAnchor, addQuestionsAnchor,
				adminPanelAnchor, signUpAnchor };

		/*
		 * Reset all nav bar anchors and navBarBrand (which don't have
		 * setActive() to non-active on every call.
		 */

		for (int i = 0; i < anchorListItems.length; i++) {
			anchorListItems[i].setActive(false);
		}

		navBarBrand.getElement().getStyle().setProperty("backgroundColor", "#F8F8F8");

		switch (whichAnchorToHighlight) {
		case 0:
			navBarBrand.getElement().getStyle().setProperty("backgroundColor", "#E7E7E7");
			break;

		case 1:
			anchorListItems[0].setActive(true);
			break;

		case 2:
			anchorListItems[1].setActive(true);
			break;

		case 3:
			anchorListItems[2].setActive(true);
			break;

		case 4:
			anchorListItems[3].setActive(true);
			break;

		case 5:
			anchorListItems[4].setActive(true);
			break;
		}
	}

	@Override
	public void setNavBarAnchorsVisibility(String[][] userData, boolean isLoggedIn) {

		if (userData != null) {
			anchorButton.setText(userData[0][0]);
		} else {
			anchorButton.setText("Log in");
		}

		anchorButton.setToggleCaret(isLoggedIn);
		dropDownMenu.setVisible(isLoggedIn);
		addQuestionsAnchor.setVisible(isLoggedIn);
		signUpAnchor.setVisible(!isLoggedIn);

		/* Display Admin Panel anchor only for user with super administrative
		privileges (IOW - 0). */
		if (isLoggedIn && Integer.parseInt(userData[0][1]) == 0) {
			adminPanelAnchor.setVisible(isLoggedIn);
		} else if (isLoggedIn && Integer.parseInt(userData[0][1]) == 1) {
			adminPanelAnchor.setVisible(!isLoggedIn);
		} else {
			adminPanelAnchor.setVisible(isLoggedIn);
		}
	}

	@UiHandler("anchorButton")
	void onAnchorButtonClicked(ClickEvent e) {
		if (listener != null && anchorButton.getText() == "Log in") {
			listener.goTo(new LoginPlace("Login"));
			navBarCollapse.hide();
		}
	}

	@UiHandler("logOutAnchorListItem")
	void onlogOutAnchorListItemClicked(ClickEvent e) {
		listener.logOutUser();
		navBarCollapse.hide();
	}

	@UiHandler("signUpAnchor")
	void onSignUpAnchorClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new LoginPlace("SignUp"));
			navBarCollapse.hide();
		}
	}
}