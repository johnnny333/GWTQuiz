package pl.johnny.gwtQuiz.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.UserScore;

@RemoteServiceRelativePath("questionService")
public interface QuestionService extends RemoteService {
	ArrayList<Question> getQuestions();

	ArrayList<Question> getShuffledQuestions();

	ArrayList<UserScore> getUserScores();

	ArrayList<UserScore> insertUserScore(UserScore userScore);

	void updateUserScore(UserScore userScore);	
}
