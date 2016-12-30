package pl.johnny.gwtQuiz.shared;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Cookies;

import pl.johnny.gwtQuiz.client.ClientFactory;
import pl.johnny.gwtQuiz.client.ClientFactory.CookieType;

public class GetCookiesTest extends GWTTestCase {

	@Override
	public String getModuleName() {

		return "pl.johnny.gwtQuiz.GWTQuizJUNIT";
	}

	public void testQuestionService() {

		// One minute cookie.
		final long DURATION = 1000 * 1 * 60;
		Date expires = new Date(System.currentTimeMillis() + DURATION);
		String cookieValue = "Test Cookie";

		Cookies.setCookie("JSESSIONID", cookieValue, expires, null, "/", false);
		Cookies.setCookie("gwtQuiz", "Test gwtQuiz Cookie", expires, null, "/", false);

		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		assertEquals(cookieValue, clientFactory.getCookie(CookieType.SESSION_ID));
	}
}