package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class AddQuestionsPlace extends Place
{
	private String helloName;
	
	public AddQuestionsPlace(String token)
	{
		this.helloName = token;
	}
	
	public AddQuestionsPlace(){}

	public String getHelloName()
	{
		return helloName;
	}

	public static class Tokenizer implements PlaceTokenizer<AddQuestionsPlace>
	{

		@Override
		public String getToken(AddQuestionsPlace place)
		{
			return place.getHelloName();
		}

		@Override
		public AddQuestionsPlace getPlace(String token)
		{
			return new AddQuestionsPlace(token);
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
