package pl.johnny.gwtQuiz.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

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

	//Package access modifiers
	QuestionServiceDatabaseConn() {
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

		Connection c;
		Statement stmt;
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
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

			//Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			int rowsCount = rsRowCount.getInt(1);

			//Initialize arrays
			questionsData = new String[rowsCount][2];
			answersData = new String[rowsCount][4];
			correctAnswersData = new String[rowsCount];
			authorData = new String[rowsCount];
			categoryData = new String[rowsCount];

			//Actual query
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
			stmt.close();
			c.close();

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

		Connection c;
		Statement stmt;

		int[] userScoreID;
		String[] userDisplay;
		int[] userScores;
		Boolean[] isEditable;
		//Array for each record timestamp
		String[] usersScoresCreatedAt;
		ArrayList<UserScore> userScoresArray = new ArrayList<UserScore>();

		try {
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

			//Actual query
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM user_scores;");
			int rowsCount = rsRowCount.getInt(1);
			System.out.println(rsRowCount.getInt(1));

			userScoreID = new int[rowsCount];
			userDisplay = new String[rowsCount];
			userScores = new int[rowsCount];
			isEditable = new Boolean[rowsCount];
			usersScoresCreatedAt = new String[rowsCount];

			ResultSet resultSet = stmt.executeQuery("SELECT ID, user_display, user_score, "
					+ "is_editable, datetime(created_at, 'localtime') AS created_at"
					+ " FROM user_scores ORDER BY user_score DESC ,created_at DESC;");

			while(resultSet.next()) {
				//Get user score ID and save it to an Array
				userScoreID[resultSet.getRow() - 1] = resultSet.getInt("ID");
				//Get user displays and save it to an Array
				userDisplay[resultSet.getRow() - 1] = resultSet.getString("user_display");
				//Get user scores and save it to an Array
				userScores[resultSet.getRow() - 1] = resultSet.getInt("user_score");
				//Get isEditable flags and save it on an Array
				isEditable[resultSet.getRow() - 1] = resultSet.getBoolean("is_editable");
				//Get users scores created at and save it to an Array
				usersScoresCreatedAt[resultSet.getRow() - 1] = resultSet.getString("created_at");
			}
			resultSet.close();
			stmt.close();
			c.close();

			/* After filling arrays with user scores data, make models with them 
			 * and pack said models to an ArrayList in order to use it on QuestionServiceImpl */
			for(int i = 0; i < userDisplay.length; ++i) {
				UserScore userScore = new UserScore(userScoreID[i], userDisplay[i], userScores[i], isEditable[i], usersScoresCreatedAt[i]);
				userScoresArray.add(userScore);
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
		return userScoresArray;
	}

	void insertUserScore(UserScore userScore) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement(
					"INSERT INTO user_scores (user_display,user_score,is_editable) VALUES (?, ?, ?);");

			//			if(userScore.userDisplay == "");else throw new Exception("Temporary user score name field is not empty(\"\")");
			prepStmt.setString(1, userScore.userDisplay);
			prepStmt.setInt(2, userScore.score);
			prepStmt.setBoolean(3, userScore.isEditable);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
	}

	void updateUserScore(UserScore userScore) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement(
					"UPDATE user_scores SET user_display=?, is_editable=? WHERE ID=?;");

			String checkedNameString = userScore.userDisplay;
			/*
			 * User provided name but deletes all its chars leaving us with empty string (""),
			 * so we delete this invalid record; 
			 */
			if(stringBarber(checkedNameString) == null) {
				deleteUserScore(userScore);
				return;
			} ;

			prepStmt.setString(1, stringBarber(checkedNameString));
			prepStmt.setBoolean(2, userScore.isEditable);
			prepStmt.setInt(3, userScore.userScoreID);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
	}

	void deleteUserScore(UserScore userScore) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement(
					"DELETE FROM user_scores WHERE ID=?;");

			prepStmt.setInt(1, userScore.userScoreID);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
	}

	/**
	 * Return question categories from database.
	 * @return
	 */
	String[] getCategories() {

		Connection c;
		Statement stmt;

		String[] categoryData = null;

		try {
			//Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			stmt = c.createStatement();

			//Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM category_type_enum;");
			int rowsCount = rsRowCount.getInt(1);

			//Initialize array
			categoryData = new String[rowsCount];

			//Actual query
			ResultSet resultSet = stmt.executeQuery("SELECT category FROM category_type_enum;");

			//Iterate over ResultSet and put each record into an array
			while(resultSet.next()) {
				String category = resultSet.getString("category");
				categoryData[resultSet.getRow() - 1] = category;
			}

			//Close connection gracefully.
			resultSet.close();
			stmt.close();
			c.close();

			//Return filled category data.
			//			return categoryData;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}
		//Return filled category data.
		return categoryData;
	}

	/**
	 * Insert submitted user question into question_tmp table. 
	 * @param userQuestion
	 */
	void insertUserQuestion(Question userQuestion) {

		String questionID = null;

		//Insert user question into question_tmp
		try {
			//Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO questions_tmp (question,author,category,has_image,image_url) VALUES (?,?,?,?,?);");

			prepStmt.setString(1, userQuestion.getQuestion());
			prepStmt.setString(2, userQuestion.getAuthor());
			prepStmt.setString(3, userQuestion.getCategory());
			//If user submitted image, set has_image column flag to 1(true).Otherwise its 0(false).
			if(userQuestion.getImageURL() != null) {
				prepStmt.setString(4, "1");
				String trimmedImageURL = userQuestion.getImageURL().substring(userQuestion.getImageURL().lastIndexOf("/")); 
				prepStmt.setString(5, trimmedImageURL);
			} else {
				prepStmt.setString(4, "0");
				prepStmt.setString(5, userQuestion.getImageURL());
			}
			/* Substrings whole image path after last slash. Supports only forward slash: "/". 
			 * TODO Implement regex to detect "\" or "/" : [^\/|\\]+$ 
			 * if/else is used to determine whether imageURL is null to avoid NullPointerException when substring*/
			//			if(userQuestion.getImageURL() != null) {
			//				prepStmt.setString(5, userQuestion.getImageURL().substring(userQuestion.getImageURL().lastIndexOf("/")));
			//			} else {
			//				prepStmt.setString(5, userQuestion.getImageURL());
			//			}
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}

		//Insert user-question answers into answers_tmp
		try {
			//Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			/*
			 * Count rows from question_tmp to relate answers being now inserted into answers_tmp 
			 * with appropriate question which was just inserted into questions_tmp.
			 */
			Statement stmt = c.createStatement();
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions_tmp;");
			questionID = rsRowCount.getString(1);

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO answers_tmp (questionID,answer1,answer2,answer3,answer4,correct_answer) VALUES (?,?,?,?,?,?);");

			prepStmt.setString(1, questionID);
			prepStmt.setString(2, userQuestion.getAnswer(0));
			prepStmt.setString(3, userQuestion.getAnswer(1));
			prepStmt.setString(4, userQuestion.getAnswer(2));
			prepStmt.setString(5, userQuestion.getAnswer(3));
			prepStmt.setString(6, userQuestion.getCorrectAnsw());
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}

		//Move uploaded image from /tmp to our pending storage.
		if(userQuestion.getImageURL() != null) {
			try {

				FileUtils.moveFileToDirectory(
						/* 
						 * getImageURL resolves to full,absolute path of uploaded image
						 * and was brought from servlet response after successful image
						 * upload.So here we just use it to point to a source file.
						 */
						FileUtils.getFile(userQuestion.getImageURL()),
						FileUtils.getFile("quiz_resources/question_images_tmp/" + questionID), true);

			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	/**
	 * Trims white spaces and non-visible characters, checks for empty strings ("") 
	 * and substrings strings (from 0 to 15) to be inserted to database.
	 * @param stringToCut
	 * @return cut string
	 */
	private String stringBarber(String stringToCut) {
		/* Strings are constant; their values cannot be changed after they are created,
		 * hence local assignments;*/

		//Removes all white-spaces and non-visible characters;
		String trimmedString = stringToCut.replaceAll("\\s+", "");
		//User provided name but deletes it leaving us with empty string ("").
		if(trimmedString == "") return null;

		if(trimmedString.length() > 15) {
			String trimmedAndSubstringed = trimmedString.substring(0, 15);
			return trimmedAndSubstringed;
		}
		return trimmedString;
	}
}