package pl.johnny.gwtQuiz.client.mvp;

import pl.johnny.gwtQuiz.client.place.GoodbyePlace;
import pl.johnny.gwtQuiz.client.place.HelloPlace;

import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.WithTokenizers;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */
@WithTokenizers( { HelloPlace.Tokenizer.class, GoodbyePlace.Tokenizer.class })
public interface AppPlaceHistoryMapper extends PlaceHistoryMapper {
}