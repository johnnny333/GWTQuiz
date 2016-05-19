package pl.johnny.gwtQuiz.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("questionService")
public interface QuestionService extends RemoteService {

//	String getQuestion(Integer id);

	String getQuestion();
	
}
