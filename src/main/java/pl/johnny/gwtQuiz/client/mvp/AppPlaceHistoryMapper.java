package pl.johnny.gwtQuiz.client.mvp;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

import pl.johnny.gwtQuiz.client.place.AddQuestionsPlace;
import pl.johnny.gwtQuiz.client.place.AdminPlace;
import pl.johnny.gwtQuiz.client.place.HighScoresPlace;
import pl.johnny.gwtQuiz.client.place.MainMenuPlace;
import pl.johnny.gwtQuiz.client.place.QuestionPlace;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { MainMenuPlace.Tokenizer.class, QuestionPlace.Tokenizer.class, HighScoresPlace.Tokenizer.class,
	AddQuestionsPlace.Tokenizer.class, AdminPlace.Tokenizer.class  })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}
