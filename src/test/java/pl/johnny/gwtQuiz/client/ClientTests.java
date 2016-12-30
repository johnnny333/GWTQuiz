package pl.johnny.gwtQuiz.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import pl.johnny.gwtQuiz.client.ClientFactory.CookieType;
import pl.johnny.gwtQuiz.client.activity.HighScoresActivity;
import pl.johnny.gwtQuiz.client.activity.MainMenuActivity;
import pl.johnny.gwtQuiz.client.activity.QuestionActivity;
import pl.johnny.gwtQuiz.client.event.NewQuestionEvent;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;
import pl.johnny.gwtQuiz.client.ui.HighScoresView;
import pl.johnny.gwtQuiz.client.ui.QuestionView;
import pl.johnny.gwtQuiz.client.ui.widgets.HighScoreCellTableView;
import pl.johnny.gwtQuiz.shared.Question;

@RunWith(MockitoJUnitRunner.class)
public class ClientTests {

	@Mock
	private ClientFactory clientFactoryMock;

	@Mock
	private PlaceController placeControllerMock;

	@Mock
	private AcceptsOneWidget acceptsOneWidgetMock;

	@Mock
	private EventBus eventBusMock;

	@Mock
	private HighScoresView highScoresViewMock;

	@Mock
	private QuestionView questionViewMock;

	@Mock
	private HighScoreCellTableView highScoreCellTableViewMock;

	@Mock
	private QuestionServiceAsync questionServiceAsynchMock;

	@Before
	public void setUp() throws Exception {
		when(clientFactoryMock.getPlaceController()).thenReturn(placeControllerMock);
		when(clientFactoryMock.getHighScoreView()).thenReturn(highScoresViewMock);
		when(clientFactoryMock.getQuestionView()).thenReturn(questionViewMock);
		when(clientFactoryMock.getHighScoreCellTableView()).thenReturn(highScoreCellTableViewMock);
		when(clientFactoryMock.getQuestionsService()).thenReturn(questionServiceAsynchMock);
		
		Answer<Void> answer = new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				AsyncCallback<List<Question>> asyncCallback = (AsyncCallback<List<Question>>) args[0];

				Question contact1 = new Question("questionDataTest", null,
						new String[] { "TestAnswer1", "TestAnswer2", "TestAnswer3", "TestAnswer4" },
						"correctAnswersDataTest", "authorDataTest1", "Muzyka");
				
				Question contact2 = new Question("questionDataTest", null,
						new String[] { "TestAnswer1", "TestAnswer2", "TestAnswer3", "TestAnswer4" },
						"correctAnswersDataTest", "authorDataTest2", "Muzyka");
				
				final List<Question> contacts = new ArrayList<Question>();
				contacts.add(contact1);
				contacts.add(contact2);
				asyncCallback.onSuccess(contacts);
				return null;
			}
		};
		doAnswer(answer).when(questionServiceAsynchMock).getShuffledQuestions(any(AsyncCallback.class));		
	}

	@Test
	public void testGotoPlace() {
		MainMenuActivity mainMenuActivity = new MainMenuActivity(new MainMenuPlace("holla"), clientFactoryMock);

		QuestionPlace questionPlace = new QuestionPlace("questionPlaceMock");
		mainMenuActivity.goTo(questionPlace);

		verify(placeControllerMock).goTo(questionPlace);
	}
	
	
	@Test
	public void getCookieTest() {
		String cookieValue = "Test cookie value";
		when(clientFactoryMock.getCookie(CookieType.USER_EMAIL)).thenReturn(cookieValue);
		
		assertEquals(cookieValue, clientFactoryMock.getCookie(CookieType.USER_EMAIL));
	}

	@Test
	public void getNullCookieTest() {
		Assert.assertNull(clientFactoryMock.getCookie(CookieType.SESSION_ID));		
	}

	@Test
	public void testMayStop() {
		HighScoresActivity highScoresActivity = new HighScoresActivity(new HighScoresPlace(null), clientFactoryMock);
		highScoresActivity.start(acceptsOneWidgetMock, eventBusMock);
		
		Assert.assertNull(highScoresActivity.mayStop());
	}

	@Test
	public void testStartWithEmptyToken() {
		HighScoresActivity highScoresActivity = new HighScoresActivity(new HighScoresPlace(""), clientFactoryMock);
		highScoresActivity.start(acceptsOneWidgetMock, eventBusMock);
				
		verify(highScoresViewMock).setPresenter(highScoresActivity);
	}

	@Test
	public void testQuestionActivity() {
		QuestionActivity questionActivity = new QuestionActivity(new QuestionPlace("hello"), clientFactoryMock);
		questionActivity.start(acceptsOneWidgetMock, eventBusMock);
		
		//Check our mocked models.
//		System.out.println(questionActivity.getQuestionsArrayList().get(0).getAuthor());
		
		assertEquals(questionActivity.getQuestionsArrayList().get(0).getAuthor(), "authorDataTest1");
		verify(questionViewMock).setPresenter(questionActivity);
		verify(eventBusMock).fireEvent(any(NewQuestionEvent.class));
	}
}