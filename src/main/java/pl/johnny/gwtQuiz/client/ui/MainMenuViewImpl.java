package pl.johnny.gwtQuiz.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;

public class MainMenuViewImpl extends Composite implements MainMenuView {
	private static HelloViewImplUiBinder uiBinder = GWT.create(HelloViewImplUiBinder.class);

	interface HelloViewImplUiBinder extends UiBinder<Widget, MainMenuViewImpl> {
	}

	@UiField org.gwtbootstrap3.client.ui.Button newGameButton;
	@UiField org.gwtbootstrap3.client.ui.Button highScoresButton;
	@UiField org.gwtbootstrap3.client.ui.Button addQuestionsButton;
	
	private Presenter listener;

	public MainMenuViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("newGameButton")
	void onNewGameButtonClicked(ClickEvent e) {
		if (listener != null) {
			listener.onNewGameButtonClicked(0);
		}
	}
	
	@UiHandler("highScoresButton")
	void onHighScoresButtonClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new HighScoresPlace("HighScores"));
		}
	}
	
	@UiHandler("addQuestionsButton")
	void onAddQuestionsButtonClicked(ClickEvent e) {
		if (listener != null) {
			listener.goTo(new AddQuestionsPlace("AddQuestions"));
		}
	}
	
	@Override
	public void setPresenter(Presenter listener) {
		this.listener = listener;
	}
}