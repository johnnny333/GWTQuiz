package pl.johnny.gwtQuiz.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Cookies;
import com.google.web.bindery.event.shared.EventBus;

import pl.johnny.gwtQuiz.client.ui.AddQuestionsView;
import pl.johnny.gwtQuiz.client.ui.AddQuestionsViewImpl;
import pl.johnny.gwtQuiz.client.ui.AdminView;
import pl.johnny.gwtQuiz.client.ui.AdminViewImpl;
import pl.johnny.gwtQuiz.client.ui.HighScoresView;
import pl.johnny.gwtQuiz.client.ui.HighScoresViewImpl;
import pl.johnny.gwtQuiz.client.ui.LoginView;
import pl.johnny.gwtQuiz.client.ui.LoginViewImp;
import pl.johnny.gwtQuiz.client.ui.MainMenuView;
import pl.johnny.gwtQuiz.client.ui.MainMenuViewImpl;
import pl.johnny.gwtQuiz.client.ui.NavBarView;
import pl.johnny.gwtQuiz.client.ui.NavBarViewImpl;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.client.ui.QuestionViewImpl;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableViewImpl;

public class ClientFactoryImpl implements ClientFactory {
	private static final EventBus eventBus = new com.google.web.bindery.event.shared.SimpleEventBus();
	private static final PlaceController placeController = new PlaceController(eventBus);
	private static final MainMenuView mainMenuView = new MainMenuViewImpl();
	private static final QuestionView questionView = new QuestionViewImpl();
	private static final HighScoreCellTableView highScoreCellTableView = new HighScoreCellTableViewImpl();
	private static final HighScoresView highScoreView = new HighScoresViewImpl();
	private static final NavBarView navBarView = new NavBarViewImpl();
	private static final AddQuestionsView addQuestionsView = new AddQuestionsViewImpl();
	private static final AdminView adminView = new AdminViewImpl();
	private final QuestionServiceAsync questionService = GWT.create(QuestionService.class);
	private static final LoginView loginView = new LoginViewImp();

	@Override
	public EventBus getEventBus() {
		return eventBus;
	}

	@Override
	public MainMenuView getMainMenuView() {
		return mainMenuView;
	}

	@Override
	public PlaceController getPlaceController() {
		return placeController;
	}

	@Override
	public QuestionView getQuestionView() {
		return questionView;
	}

	@Override
	public HighScoreCellTableView getHighScoreCellTableView() {
		return highScoreCellTableView;
	}

	@Override
	public HighScoresView getHighScoreView() {
		return highScoreView;
	}

	@Override
	public QuestionServiceAsync getQuestionsService() {
		return questionService;
	}

	@Override
	public NavBarView getNavBarView() {
		return navBarView;
	}

	@Override
	public AddQuestionsView getAddQuestionsView() {
		return addQuestionsView;
	}

	@Override
	public AdminView getAdminView() {
		return adminView;
	}

	@Override
	public LoginView getLoginView() {
		return loginView;
	}

	@Override
	public String getCookie(CookieType cookieType) {
		
		if(Cookies.getCookie("gwtQuizCookieUser") != null) {

			String cookie = null;

			switch(cookieType) {
				case SESSION_ID:
					cookie = Cookies.getCookie("JSESSIONID");
					break;

				case USER_EMAIL:
					cookie = base64Decode(Cookies.getCookie("gwtQuizCookieUser")).split(",")[1];
					break;
					
				case USER_TYPE:
					cookie = base64Decode(Cookies.getCookie("gwtQuizCookieUser")).split(",")[2];
					break;
					
				case UUID:
					cookie = base64Decode(Cookies.getCookie("gwtQuizCookieUser")).split(",")[3];
					GWT.log("UUID: " + cookie);
					break;

				default:
					break;
			}
			return cookie;
		} else {
			return null;
		}
	}
	
	@Override
	public native String base64Encode(String stringToEncode) /*-{
	  return window.btoa(stringToEncode);
	}-*/;
	
	@Override
	public native String base64Decode(String stringToDecode) /*-{
	  return window.atob(stringToDecode);
	}-*/;
}