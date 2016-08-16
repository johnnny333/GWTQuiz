package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class AdminPlace extends Place {
	private String token;

	public AdminPlace(String token) {
		this.token = token;
	}

	public AdminPlace() {
	}

	public String getTokenName() {
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<AdminPlace> {

		@Override
		public String getToken(AdminPlace place) {
			return place.getTokenName();
		}

		@Override
		public AdminPlace getPlace(String token) {
			return new AdminPlace(token);
		}

	}
}