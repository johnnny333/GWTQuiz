package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class HighScoresPlace extends Place
{
	private String token;
	
	public HighScoresPlace(String token) {
		this.token = token == null ? "" : token;
	}
	
	public HighScoresPlace(){}

	public String getHelloName()
	{
		return token;
	}

	public static class Tokenizer implements PlaceTokenizer<HighScoresPlace>
	{

		@Override
		public String getToken(HighScoresPlace place)
		{
			return place.getHelloName();
		}

		@Override
		public HighScoresPlace getPlace(String token)
		{
			return new HighScoresPlace(token);
		}

	}
	
//	@Override
//	protected Place getPlace(String token)
//	{
//		return new HelloPlace(token);
//	}
//
//	@Override
//	protected Activity getActivity()
//	{
//		return new HelloActivity("David");
//	}
}
