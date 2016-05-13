package pl.johnny.gwtQuiz.client.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class MainMenuPlace extends Place
{
	private String helloName;
	
	public MainMenuPlace(String token)
	{
		this.helloName = token;
	}
	
	public MainMenuPlace()
	{
		
	}

	public String getHelloName()
	{
		return helloName;
	}

	public static class Tokenizer implements PlaceTokenizer<MainMenuPlace>
	{

		@Override
		public String getToken(MainMenuPlace place)
		{
			return place.getHelloName();
		}

		@Override
		public MainMenuPlace getPlace(String token)
		{
			return new MainMenuPlace(token);
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
