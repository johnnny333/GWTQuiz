package pl.johnny.gwtQuiz.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import pl.johnny.gwtQuiz.shared.Question;

/**
 * Basically,we need to recreate arrays structure as follows:
 * <pre>
 * 
 * 	String[] questionsData = new String[] {
 *			"Ile strun ma typowa gitara elektryczna?",
 *			"Jak na nazwisko ma Barack?",
 *			"W jakim województwie leży Jurata?",
 *			"W którym roku wydano Nevermind Nirvany?" };
 *
 *	String[][] answersData = new String[][] {
 *			{ "5", "6", "4", "7" },
 *			{ "Pacheco", "Blake", "Horton", "Obama" },
 *			{ "Mazowieckim", "Śląskim", "Pomorskim", "Łódzkim" },
 *			{ "1994", "2001", "1984", "2007" } };
 *
 *	String[] correctAnswersData = new String[] {
 *			answersData[0][1],
 *			answersData[1][3],
 *			answersData[2][2],
 *  		answersData[3][0]
 *	};
 * </pre>
 * */
public class QuestionServiceDatabaseConn {
	
	private String[] questionsData;
	private String[][] answersData;
	private String[] correctAnswersData;
	//Using default (no modifier) access modifiers
	ArrayList<Question> questions = new ArrayList<Question>();
	
	QuestionServiceDatabaseConn() {

		Connection c = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:questions.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			int rowsCount = rsRowCount.getInt(1);

			questionsData = new String[rowsCount];
			answersData = new String[rowsCount][rowsCount];
			correctAnswersData = new String[rowsCount];

			ResultSet resultSet = stmt.executeQuery("SELECT answers.answer1,answers.answer2,answers.answer3,answers.answer4,"
					+ "answers.correct_answer,questions.question FROM answers "
					+ "LEFT JOIN questions ON answers.questionID = questions.ID; ");

			while(resultSet.next()) {
				//Get questions and save it to an Array
				String question = resultSet.getString("question");
				questionsData[resultSet.getRow() - 1] = question;
				//Get answers and save it to an Array
				for(int i = 0; i < rowsCount; i++) {
					String answer = resultSet.getString("answer" + (i + 1));
					answersData[resultSet.getRow() - 1][i] = answer;
				}
				//Get correct answers array
				correctAnswersData[resultSet.getRow() - 1] = answersData[resultSet.getRow() - 1][resultSet.getInt("correct_answer")];
			}
			resultSet.close();
			stmt.close();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		/* After filling arrays with questions data, make models with them 
		 * and pack said models to an ArrayList in order to use it on QuestionServiceImpl
		 */
		for(int i = 0; i < questionsData.length; ++i) {
			Question question = new Question(questionsData[i], answersData[i], correctAnswersData[i]);
			questions.add(question);
		}
	}
}