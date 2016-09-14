package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class LoginPlace extends Place {
	private String token;

	public LoginPlace(String token) {
		this.token = token;
	}

	public LoginPlace() {
	}

	public String getTokenName() {
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<LoginPlace> {

		@Override
		public String getToken(LoginPlace place) {
			return place.getTokenName();
		}

		@Override
		public LoginPlace getPlace(String token) {
			return new LoginPlace(token);
		}

	}
}