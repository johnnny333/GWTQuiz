package pl.johnny.gwtQuiz.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import pl.johnny.gwtQuiz.shared.Question;

/**
 * Class in charge of all database operations.
 * <br/>
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
 * @author jzarewicz
 * */
public class QuestionServiceDatabaseConn {

	/** Two dimensional array. At first[0] position we'll store question String,
	 * at second[1] base64 image representation.*/
	private String[][] questionsData;
	private String[][] answersData;
	private String[] correctAnswersData;
	public String[] authorData;
	public String[] categoryData;
	//Using default (no modifier) access modifiers
	ArrayList<Question> questions = new ArrayList<Question>();

	QuestionServiceDatabaseConn() {

		Connection c = null;
		Statement stmt = null;

		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			stmt = c.createStatement();

			c.createStatement().execute("PRAGMA foreign_keys = ON");
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			int rowsCount = rsRowCount.getInt(1);

			questionsData = new String[rowsCount][2];
			answersData = new String[rowsCount][4];
			correctAnswersData = new String[rowsCount];
			authorData = new String[rowsCount];
			categoryData = new String[rowsCount];

			ResultSet resultSet = stmt.executeQuery("SELECT answers.answer1,answers.answer2,answers.answer3,answers.answer4,"
					+ "answers.correct_answer,questions.question,questions.author,"
					+ "questions.category,questions.has_image,questions.image_url FROM answers "
					+ "LEFT JOIN questions ON answers.questionID = questions.ID; ");

			while(resultSet.next()) {
				//Get questions and save it to an Array
				String question = resultSet.getString("question");
				questionsData[resultSet.getRow() - 1][0] = question;
				
				boolean questionImg = resultSet.getBoolean("has_image");
				if(questionImg != false){
				questionsData[resultSet.getRow() - 1][1] = resultSet.getString("image_url");
				}
				
				//Get answers and save it to an Array
				for(int i = 0; i < 4; i++) {
					String answer = resultSet.getString("answer" + (i + 1));
					answersData[resultSet.getRow() - 1][i] = answer;
				}
				//Get correct answers array
				correctAnswersData[resultSet.getRow() - 1] = answersData[resultSet.getRow() - 1][resultSet.getInt("correct_answer")];
				//Get author data
				authorData[resultSet.getRow() - 1] = resultSet.getString("author");
				//Get category Data
				categoryData[resultSet.getRow() - 1] = resultSet.getString("category");
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
			Question question = new Question(questionsData[i][0],questionsData[i][1], answersData[i], correctAnswersData[i],
					authorData[i], categoryData[i]);
			questions.add(question);
		}
	}
}