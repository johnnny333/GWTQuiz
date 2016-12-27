package pl.johnny.gwtQuiz.shared;

import java.util.ArrayList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import pl.johnny.gwtQuiz.client.QuestionService;
import pl.johnny.gwtQuiz.client.QuestionServiceAsync;
import pl.johnny.gwtQuiz.shared.Question;

public class GetQuestionsTest extends GWTTestCase {

	@Override
	public String getModuleName() {

		return "pl.johnny.gwtQuiz.GWTQuizJUNIT";
	}

//	public void testSimple() {
//		assertTrue(true);
//	}

	/**
	 * This test will send a request to the server using the getQuestions method
	 * in QuestionService and verify the response.
	 * 
	 * Gotta' make a symbolic link to a database:
	 * ln -s ~/workspace.gwt/GWTQuiz/src/main/webapp/quiz_resources ~/workspace.gwt/GWTQuiz/quiz_resources
	 */
	public void testQuestionService() {
		// Create the service that we will test.
		QuestionServiceAsync questionService = GWT.create(QuestionService.class);
		ServiceDefTarget target = (ServiceDefTarget) questionService;
		target.setServiceEntryPoint(com.google.gwt.core.client.GWT.getModuleBaseURL() + "gwtQuiz/questionService");

		/*
		 * Since RPC calls are asynchronous, we will need to wait for a response 
		 * after this test method returns. This line tells the test runner to 
		 * wait up to 10 seconds before timing out.
		 */
		delayTestFinish(10000);

		// Send a request to the server.
		questionService.getQuestions(new AsyncCallback<ArrayList<Question>>() {

			@Override
			public void onSuccess(ArrayList<Question> result) {
				System.out.println(result);

				if (result.get(0) instanceof Question) {
					/*
					 * Now that we have received a response, we need to tell the
					 * test runner that the test is complete. You must call finishTest()
					 * after an asynchronous test finishes successfully, or the test will 
					 * time out.
					 */
					System.out.println("Result from database: " + result);
					finishTest();
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				// The request resulted in an unexpected error.
				fail("Request failure " + caught.getMessage());
			}
		});
	}
}
