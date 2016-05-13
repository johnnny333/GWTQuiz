package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class QuestionPlace extends Place
{
	private String goodbyeName;
	
	public QuestionPlace(String token)
	{
		this.goodbyeName = token;
	}

	public String getGoodbyeName()
	{
		return goodbyeName;
	}

	public static class Tokenizer implements PlaceTokenizer<QuestionPlace>
	{
		@Override
		public String getToken(QuestionPlace place)
		{
			return place.getGoodbyeName();
		}

		@Override
		public QuestionPlace getPlace(String token)
		{
			return new QuestionPlace(token);
		}
	}
	
}
