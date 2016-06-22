package pl.johnny.gwtQuiz.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.UserScore;

/**
 * Class in charge of all database operations. 
 * General database connection and statement creation is done in the constructor.
 * Then used in functions by "c" and "stm" global fields previously initialized 
 * in said constructor. 
 * @author jzarewicz
 * */
public class QuestionServiceDatabaseConn {

	private Connection c;
	private Statement stmt;

	//Package access modifiers
	QuestionServiceDatabaseConn() {

		try {
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

		} catch (SQLException e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
	}

	/**
	 * Basically,we need to recreate arrays structure as follows:
	 * <br/>
	 * <pre>
	 * 	String[][] questionsData = new String[][] {
	 *			{"Ile strun ma typowa gitara elektryczna?", NULL},
	 *			{"Jak na nazwisko ma Barack?", NULL},
	 *			{"W jakim województwie leży Jurata?", NULL"},
	 *			{"W którym roku wydano Nevermind Nirvany?", NULL"},
	 *			{"Czy to pies czy to kot a może lisek?", "quiz_resources/question_images/5/small-fox.jpg"}  };
	 *
	 *	String[][] answersData = new String[][] {
	 *			{ "5", "6", "4", "7" },
	 *			{ "Pacheco", "Blake", "Horton", "Obama" },
	 * 			{ "Mazowieckim", "Śląskim", "Pomorskim", "Łódzkim" },
	 *			{ "1994", "2001", "1984", "2007" },
	 *			{ "Kot", "Pies", "Lisek", "Niedźwiedz" } };
	 *
	 *	String[] correctAnswersData = new String[] {
	 *			answersData[0][1],
	 *			answersData[1][3],
	 *			answersData[2][2],
	 *  		answersData[3][0],
	 *  		answersData[4][2] };
	 *  
	 *	String[] authorData = new String[] {
	 *			"johnny",
	 *			"johnny",
	 *			"johnny",
	 *  		"johnny",
	 *  		"johnny" };
	 *  
	 *	String[] categoryData = new String[] {
	 *			"Muzyka",
	 *			"Polityka",
	 *			"Muzyka",
	 *  		"Muzyka",
	 *  		"Geografia" };
	 * </pre>
	 * */
	ArrayList<Question> getQuestions() {

		/** Two dimensional array. At first[i][0] position we'll store question String,
		 * at second[i][1] image server url.*/
		String[][] questionsData;
		String[][] answersData;
		String[] correctAnswersData;
		String[] authorData;
		String[] categoryData;
		/* Array to store our model. Is returned by getQuestion(). */
		ArrayList<Question> questionsArray = new ArrayList<Question>();

		try {

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
				if(questionImg != false) {
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
				//Get category data
				categoryData[resultSet.getRow() - 1] = resultSet.getString("category");
			}
			resultSet.close();
//			stmt.close();
//			c.close();

			/* After filling arrays with questions data, make models with them 
			 * and pack said models to an ArrayList in order to use it on QuestionServiceImpl */
			for(int i = 0; i < questionsData.length; ++i) {
				Question question = new Question(questionsData[i][0], questionsData[i][1], answersData[i], correctAnswersData[i],
						authorData[i], categoryData[i]);
				questionsArray.add(question);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}
		return questionsArray;
	}

	/**
	 * Get user points and store them into an array.
	 * @return
	 */
	ArrayList<UserScore> getUserScores() {

		String[] userDisplay;
		String[] userScores;
		ArrayList<UserScore> userScoresArray = new ArrayList<UserScore>();

		try {
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM user_scores;");
			int rowsCount = rsRowCount.getInt(1);
			System.out.println(rsRowCount.getInt(1));

			userDisplay = new String[rowsCount];
			userScores = new String[rowsCount];

			ResultSet resultSet = stmt.executeQuery("SELECT user_display, user_score FROM user_scores;");

			while(resultSet.next()) {
				//Get user displays and save it to an Array
				userDisplay[resultSet.getRow() - 1] = resultSet.getString("user_display");
				//Get user scores and save it to an Array
				userScores[resultSet.getRow() - 1] = resultSet.getString("user_score");
			}
			resultSet.close();
//			stmt.close();
//			c.close();
			
			/* After filling arrays with user scores data, make models with them 
			 * and pack said models to an ArrayList in order to use it on QuestionServiceImpl */
			for(int i = 0; i < userDisplay.length; ++i) {
				UserScore userScore = new UserScore(userDisplay[i], userScores[i], false);
				userScoresArray.add(userScore);
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
		return userScoresArray;
	}
}