package pl.johnny.gwtQuiz.client;

import pl.johnny.gwtQuiz.shared.Question;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("questionsService")
public interface QuestionsService extends RemoteService {

	Question getQuestion(Integer id);
	
}
