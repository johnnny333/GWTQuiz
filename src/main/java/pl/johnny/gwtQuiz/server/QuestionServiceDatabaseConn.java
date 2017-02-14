package pl.johnny.gwtQuiz.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;

import org.apache.commons.io.FileUtils;

import pl.johnny.gwtQuiz.shared.Question;
import pl.johnny.gwtQuiz.shared.User;
import pl.johnny.gwtQuiz.shared.UserScore;

/**
 * Class in charge of all database operations. General database connection and
 * statement creation is done in the constructor. Then used in functions by "c"
 * and "stm" global fields previously initialized in said constructor.
 * 
 * @author jzarewicz
 */
public class QuestionServiceDatabaseConn {

	// Package access modifiers
	public QuestionServiceDatabaseConn() {
		System.out.println("QuestionServiceDatabaseConn() intialized !!!!");
	}

	/**
	 * Return database connection with foreign_keys set to 'ON'.
	 * 
	 * @throws ClassNotFoundException
	 * @throws ServletException
	 */
	private Connection getConnection() throws SQLException, ClassNotFoundException, ServletException {

		final String dbType = "mySQLlocal";
		String url;

		switch (dbType) {
		case "sqlite":

			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Connection connection = DriverManager
					.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			connection.createStatement().execute("PRAGMA foreign_keys = ON");
			return connection;

		case "mysql_lucid":

			return DriverManager.getConnection("jdbc:mysql://localhost/quiz?socket=/lucid/services/MySQL/mysql.sock",
					"lucid", "");

		case "mySQLlocal":

			return DriverManager.getConnection("jdbc:mysql://localhost/quiz", "user", "kyt");

		case "mysql":

			if (System.getProperty("com.google.appengine.runtime.version").startsWith("Google App Engine/")) {

				url = "jdbc:google:mysql://quizownik:europe-west1:quizownik1/quiz?user=root&password=33szarikow88";

				try {
					// Load the class that provides the new
					// "jdbc:google:mysql://" prefix.
					Class.forName("com.mysql.jdbc.GoogleDriver");
				} catch (ClassNotFoundException e) {
					throw new ServletException("Error loading Google JDBC Driver", e);
				}

			} else {
				// Set the url with the local MySQL database connection url when
				// running locally
				url = System.getProperty("ae-cloudsql.local-database-url");
			}

			return DriverManager.getConnection(url);

		default:
			throw new SQLException("No connection was selected in getConnection()");
		}
	}

	/**
	 * Basically,we need to recreate arrays structure as follows: <br/>
	 * 
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
	 * 
	 * @throws SQLException
	 */
	ArrayList<Question> getQuestions() throws Exception {

		Connection c = null;
		Statement stmt = null;
		/**
		 * Two dimensional array. At first[i][0] position we'll store question
		 * String, at second[i][1] image server url.
		 */
		String[][] questionsData;
		String[][] answersData;
		String[] correctAnswersData;
		String[] authorData;
		String[] categoryData;
		/* Array to store our model. Is returned by getQuestion(). */
		ArrayList<Question> questionsArray = new ArrayList<Question>();

		try {
			// Connection
			c = getConnection();
			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			int rowsCount = 0;
			if (rsRowCount.next()) {
				rowsCount = rsRowCount.getInt(1);
			}

			// Initialize arrays
			questionsData = new String[rowsCount][2];
			answersData = new String[rowsCount][4];
			correctAnswersData = new String[rowsCount];
			authorData = new String[rowsCount];
			categoryData = new String[rowsCount];

			// Actual query
			ResultSet resultSet = stmt
					.executeQuery("SELECT answers.answer1,answers.answer2,answers.answer3,answers.answer4,"
							+ "answers.correct_answer,questions.question,questions.author,"
							+ "questions.has_image,questions.image_url, categories.category FROM answers "
							+ "LEFT JOIN questions ON answers.questionID = questions.ID "
							+ "LEFT JOIN categories ON questions.category_id = categories.id; ");

			while (resultSet.next()) {
				// Get questions and save it to an Array
				String question = resultSet.getString("question");
				questionsData[resultSet.getRow() - 1][0] = question;

				boolean questionImg = resultSet.getBoolean("has_image");
				if (questionImg != false) {
					questionsData[resultSet.getRow() - 1][1] = resultSet.getString("image_url");
				}

				// Get answers and save it to an Array
				for (int i = 0; i < 4; i++) {
					String answer = resultSet.getString("answer" + (i + 1));
					answersData[resultSet.getRow() - 1][i] = answer;
				}

				// Get correct answers array
				correctAnswersData[resultSet.getRow() - 1] = answersData[resultSet.getRow() - 1][resultSet
						.getInt("correct_answer")];
				// Get author data
				authorData[resultSet.getRow() - 1] = resultSet.getString("author");
				// Get category data
				categoryData[resultSet.getRow() - 1] = resultSet.getString("category");
			}
			resultSet.close();
			stmt.close();

			/*
			 * After filling arrays with questions data, make models with them
			 * and pack said models to an ArrayList in order to use it on
			 * QuestionServiceImpl
			 */
			for (int i = 0; i < questionsData.length; ++i) {
				Question question = new Question(questionsData[i][0], questionsData[i][1], answersData[i],
						correctAnswersData[i], authorData[i], categoryData[i]);
				questionsArray.add(question);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			c.close();
		}

		return questionsArray;
	}

	/**
	 * Get user points and store them into an array.
	 * 
	 * @return
	 * @throws Exception
	 */
	ArrayList<UserScore> getUserScores() throws Exception {

		Connection c = null;
		Statement stmt = null;

		int[] userScoreID;
		String[] userDisplay;
		int[] userScores;
		Boolean[] isEditable;
		// Array for each record timestamp
		String[] usersScoresCreatedAt;
		ArrayList<UserScore> userScoresArray = new ArrayList<UserScore>();

		try {
			// Connection
			c = getConnection();
			stmt = c.createStatement();

			// Actual query
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM user_scores;");
			int rowsCount = 0;

			if (rsRowCount.next()) {
				rowsCount = rsRowCount.getInt(1);
			}

			userScoreID = new int[rowsCount];
			userDisplay = new String[rowsCount];
			userScores = new int[rowsCount];
			isEditable = new Boolean[rowsCount];
			usersScoresCreatedAt = new String[rowsCount];

			ResultSet resultSet = stmt.executeQuery("SELECT ID, user_display, user_score, " + "is_editable, created_at"
					+ " FROM user_scores ORDER BY user_score DESC ,created_at DESC;");

			while (resultSet.next()) {
				// Get user score ID and save it to an Array
				userScoreID[resultSet.getRow() - 1] = resultSet.getInt("ID");
				// Get user displays and save it to an Array
				userDisplay[resultSet.getRow() - 1] = resultSet.getString("user_display");
				// Get user scores and save it to an Array
				userScores[resultSet.getRow() - 1] = resultSet.getInt("user_score");
				// Get isEditable flags and save it on an Array
				isEditable[resultSet.getRow() - 1] = resultSet.getBoolean("is_editable");
				// Get users scores created at and save it to an Array
				usersScoresCreatedAt[resultSet.getRow() - 1] = resultSet.getString("created_at");
			}
			resultSet.close();
			stmt.close();
			/*
			 * After filling arrays with user scores data, make models with them
			 * and pack said models to an ArrayList in order to use it on
			 * QuestionServiceImpl
			 */
			for (int i = 0; i < userDisplay.length; ++i) {
				UserScore userScore = new UserScore(userScoreID[i], userDisplay[i], userScores[i], isEditable[i],
						usersScoresCreatedAt[i]);
				userScoresArray.add(userScore);
			}

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
		return userScoresArray;
	}

	void insertUserScore(UserScore userScore) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement(
					"INSERT INTO user_scores (user_display,user_score,is_editable) VALUES (?, ?, ?);");

			prepStmt.setString(1, userScore.userDisplay);
			prepStmt.setInt(2, userScore.score);
			prepStmt.setBoolean(3, userScore.isEditable);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	void updateUserScore(UserScore userScore) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("UPDATE user_scores SET user_display=?, is_editable=? WHERE ID=?;");

			String checkedNameString = userScore.userDisplay;
			/*
			 * User provided name but deletes all its chars leaving us with
			 * empty string (""), so we delete this invalid record;
			 */
			if (stringBarber(checkedNameString) == null) {
				deleteUserScore(userScore);
				return;
			}

			prepStmt.setString(1, stringBarber(checkedNameString));
			prepStmt.setBoolean(2, userScore.isEditable);
			prepStmt.setInt(3, userScore.userScoreID);
			prepStmt.executeUpdate();
			prepStmt.close();
			// c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	void deleteUserScore(UserScore userScore) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("DELETE FROM user_scores WHERE ID=?;");

			prepStmt.setInt(1, userScore.userScoreID);
			prepStmt.executeUpdate();

			prepStmt.close();
			// c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	/**
	 * Return question categories from database.
	 * 
	 * @return
	 * @throws Exception
	 */
	String[][] getCategories() throws Exception {

		Connection c = null;
		Statement stmt;

		/**
		 * Structure is as follows: {"1", "Geografia}, {"2", "Muzyka"}, {"3",
		 * "Polityka"}, ...
		 */
		String[][] categoryData = null;

		try {
			// Connection
			c = getConnection();

			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM categories;");
			int rowsCount = 0;
			if (rsRowCount.next()) {
				rowsCount = rsRowCount.getInt(1);
			}

			// Initialize array
			categoryData = new String[rowsCount][2];

			// Actual query
			ResultSet resultSet = stmt.executeQuery("SELECT id, category FROM categories;");

			// Iterate over ResultSet and put each record into an array
			while (resultSet.next()) {
				categoryData[resultSet.getRow() - 1][0] = resultSet.getString("id");
				categoryData[resultSet.getRow() - 1][1] = resultSet.getString("category");
			}

			// Close connection gracefully.
			resultSet.close();
			stmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
		// Return filled category data.
		return categoryData;
	}

	String[][] getCategory(String category) throws Exception {

		Connection c = null;
		String[][] categoryData = null;

		try {
			// Connection
			c = getConnection();

			// Initialize array
			categoryData = new String[1][2];

			PreparedStatement prepStmt = c.prepareStatement("SELECT id,category FROM categories WHERE category = ?;");
			prepStmt.setString(1, category);
			ResultSet rs = prepStmt.executeQuery();

			while (rs.next()) {
				categoryData[0][0] = rs.getString("id");
				categoryData[0][1] = rs.getString("category");
			}

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
		// Return user password.
		return categoryData;
	}

	/**
	 * Insert submitted user question into question_tmp table.
	 * 
	 * @param userQuestion
	 * @throws Exception
	 */
	void insertUserQuestion(Question userQuestion) throws Exception {

		Connection c = null;
		int questionID = 0;

		// Insert user question into question_tmp
		try {
			// Connection
			c = getConnection();
			c.setAutoCommit(false);

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO questions_tmp (question,author,category_id,has_image,image_url) VALUES (?,?,?,?,?);",
					Statement.RETURN_GENERATED_KEYS);

			prepStmt.setString(1, userQuestion.getQuestion());
			prepStmt.setString(2, userQuestion.getAuthor());
			prepStmt.setString(3, userQuestion.getCategory());
			/*
			 * If user submitted image, set has_image column flag to
			 * 1(true).Otherwise its 0(false). Also, in if block set image name
			 * - trimmed from full path - or - in else block - null if none
			 * image is provided .
			 */
			if (userQuestion.getImageURL() != null) {
				prepStmt.setString(4, "1");
				
				prepStmt.setString(5, userQuestion.getImageURL());
			} else {
				prepStmt.setString(4, "0");
				prepStmt.setString(5, userQuestion.getImageURL());
			}
			prepStmt.executeUpdate();

			// Get last ID of questions_tmp table.
			ResultSet lastQuestionID = prepStmt.getGeneratedKeys();
			lastQuestionID.next();
			questionID = lastQuestionID.getInt(1);

			prepStmt.close();
			c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}

		// Insert user-question answers into answers_tmp
		try {
			// Connection
			c = getConnection();
			c.setAutoCommit(false);

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO answers_tmp (questionID,answer1,answer2,answer3,answer4,correct_answer) VALUES (?,?,?,?,?,?);");

			prepStmt.setInt(1, questionID);
			prepStmt.setString(2, userQuestion.getAnswer(0));
			prepStmt.setString(3, userQuestion.getAnswer(1));
			prepStmt.setString(4, userQuestion.getAnswer(2));
			prepStmt.setString(5, userQuestion.getAnswer(3));
			prepStmt.setString(6, userQuestion.getCorrectAnsw());
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());

		} finally {
			c.close();
		}

		
	}

	/**
	 * SELECT answers_tmp.ID, answers_tmp.answer1,answers_tmp.answer2,
	 * answers_tmp.answer3,answers_tmp.answer4,
	 * answers_tmp.correct_answer,questions_tmp.question,
	 * questions_tmp.author,questions_tmp.category,questions_tmp.has_image,
	 * questions_tmp.image_url FROM answers_tmp LEFT JOIN questions_tmp ON
	 * answers_tmp.questionID = questions_tmp.ID;
	 * 
	 * @return ArrayList <Question>
	 * @throws Exception
	 */
	ArrayList<Question> getTmpQuestions() throws Exception {

		Connection c = null;
		Statement stmt;
		/**
		 * Two dimensional array. At first[i][0] position we'll store question
		 * String, at second[i][1] image server url.
		 */
		String[][] questionsTmpData;
		String[][] answerTmpData;
		String[] correctAnswersTmpData;
		int[] correctAnswersIntData;
		String[] authorTmpData;
		String[] categoryTmpData;
		// The additional field alluding to getQuestions();
		String[] IDTmpData;
		/* Array to store our model. Is returned by getQuestion(). */
		ArrayList<Question> questionsTmpArray = new ArrayList<Question>();

		try {
			// Connection
			c = getConnection();
			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions_tmp;");
			int rowsCount = 0;
			if (rsRowCount.next()) {
				rowsCount = rsRowCount.getInt(1);
			}

			// Initialize arrays
			questionsTmpData = new String[rowsCount][2];
			answerTmpData = new String[rowsCount][4];
			correctAnswersTmpData = new String[rowsCount];
			correctAnswersIntData = new int[rowsCount];
			authorTmpData = new String[rowsCount];
			categoryTmpData = new String[rowsCount];
			IDTmpData = new String[rowsCount];

			// Actual query
			ResultSet resultSet = stmt.executeQuery("SELECT answers_tmp.ID, answers_tmp.answer1,answers_tmp.answer2,"
					+ "answers_tmp.answer3,answers_tmp.answer4,"
					+ "answers_tmp.correct_answer,questions_tmp.question,questions_tmp.author,"
					+ "questions_tmp.has_image,questions_tmp.image_url, categories.category FROM answers_tmp "
					+ "LEFT JOIN questions_tmp ON answers_tmp.questionID = questions_tmp.ID "
					+ "LEFT JOIN categories ON questions_tmp.category_id = categories.id; ");

			while (resultSet.next()) {
				// Get questions and save it to an Array
				String question = resultSet.getString("question");
				questionsTmpData[resultSet.getRow() - 1][0] = question;

				boolean questionImg = resultSet.getBoolean("has_image");
				if (questionImg != false) {
					questionsTmpData[resultSet.getRow() - 1][1] = resultSet.getString("image_url");
				}

				// Get answers and save it to an Array
				for (int i = 0; i < 4; i++) {
					String answer = resultSet.getString("answer" + (i + 1));
					answerTmpData[resultSet.getRow() - 1][i] = answer;
				}
				// Get correct answers array as string
				correctAnswersTmpData[resultSet.getRow() - 1] = answerTmpData[resultSet.getRow() - 1][resultSet
						.getInt("correct_answer")];
				// Get correct answer as int
				correctAnswersIntData[resultSet.getRow() - 1] = resultSet.getInt("correct_answer");
				// Get author data
				authorTmpData[resultSet.getRow() - 1] = resultSet.getString("author");
				// Get category data
				categoryTmpData[resultSet.getRow() - 1] = resultSet.getString("category");
				// Get question table ID
				IDTmpData[resultSet.getRow() - 1] = resultSet.getString("ID");
			}
			resultSet.close();
			stmt.close();

			/*
			 * After filling arrays with questions data, make models with them
			 * and pack said models to an ArrayList in order to use it on
			 * QuestionServiceImpl
			 */
			for (int i = 0; i < questionsTmpData.length; ++i) {
				Question question = new Question(questionsTmpData[i][0], questionsTmpData[i][1], answerTmpData[i],
						correctAnswersTmpData[i], correctAnswersIntData[i], authorTmpData[i], categoryTmpData[i],
						IDTmpData[i]);
				questionsTmpArray.add(question);
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			c.close();
		}
		return questionsTmpArray;
	}

	/**
	 * Deletes temporary user question and temporary user answer by their ID.
	 * 
	 * @param questionID
	 * @throws Exception
	 */
	void deleteUserTmpQuestion(String questionID) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("DELETE FROM answers_tmp WHERE ID=?;");

			prepStmt.setString(1, questionID);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("DELETE FROM questions_tmp WHERE ID=?;");

			prepStmt.setString(1, questionID);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}

	}

	void acceptUserTmpQuestion(Question userQuestion, String tmpQuestionID) throws Exception {

		Connection c = null;
		Integer questionID = null;

		// Insert user question into question_tmp
		try {
			// Connection
			c = getConnection();

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO questions (question,author,category_id) VALUES (?,?,?);",
					Statement.RETURN_GENERATED_KEYS);

			prepStmt.setString(1, userQuestion.getQuestion());
			prepStmt.setString(2, userQuestion.getAuthor());
			prepStmt.setString(3, userQuestion.getCategory());

			prepStmt.executeUpdate();

			// Get last ID of questions table.
			ResultSet lastQuestionID = prepStmt.getGeneratedKeys();
			lastQuestionID.next();
			questionID = lastQuestionID.getInt(1);

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}

		// Question image handling
		if (userQuestion.getImageURL() != null) {

			try {
				System.out.println("has image url");
				c = getConnection();

				PreparedStatement prepStmt = c
						.prepareStatement("UPDATE questions SET has_image = ?, image_url = ? WHERE ID = ?;");

				prepStmt.setString(1, "1");
				prepStmt.setString(2, userQuestion.getImageURL());
				prepStmt.setInt(3, questionID);

				prepStmt.executeUpdate();

				prepStmt.close();

			} catch (Exception e) {
				System.err.println(e.getClass().getName() + ": " + e.getMessage());
				System.err.println(e.getCause() + " " + e.getStackTrace());
			} finally {
				c.close();
			}
		}

		// Insert user-question answers into answers_tmp
		try {
			// Connection
			c = getConnection();

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO answers (questionID,answer1,answer2,answer3,answer4,correct_answer) VALUES (?,?,?,?,?,?);");

			prepStmt.setInt(1, (questionID));
			prepStmt.setString(2, userQuestion.getAnswer(0));
			prepStmt.setString(3, userQuestion.getAnswer(1));
			prepStmt.setString(4, userQuestion.getAnswer(2));
			prepStmt.setString(5, userQuestion.getAnswer(3));
			prepStmt.setString(6, userQuestion.getCorrectAnsw());
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}


		// Finally delete this particular user question and answers from
		// temporary tables.
		deleteUserTmpQuestion(tmpQuestionID);

	}

	/**
	 * Get user email,password and type by given email.
	 * 
	 * @param user
	 * @return [0] = email, [1] = password, [2] type;
	 * @throws Exception
	 */
	String[] getUser(User user) throws Exception {

		Connection c = null;
		String passwordData[] = null;

		try {
			// Connection
			c = getConnection();

			PreparedStatement prepStmt = c.prepareStatement("SELECT email,password,type from users WHERE email= ?;");

			prepStmt.setString(1, user.email);
			ResultSet rs = prepStmt.executeQuery();

			// Initialize array for data.
			passwordData = new String[rs.getMetaData().getColumnCount()];

			// Check if there are any results.
			if (!rs.isBeforeFirst()) {
				System.out.println("No such user");
				return new String[] { "No such user" };
			}

			while (rs.next()) {
				passwordData[0] = rs.getString(1);
				passwordData[1] = rs.getString(2);
				passwordData[2] = rs.getString(3);
			}

			prepStmt.close();
			// c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
		// Return user password.
		return passwordData;
	}

	void insertNewCategory(String newCategory) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("INSERT INTO categories (category) VALUES (?);");

			prepStmt.setString(1, newCategory);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	void deleteCategory(String categoryToDelete) throws SQLException {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("DELETE FROM categories WHERE category=?;");

			prepStmt.setString(1, categoryToDelete);
			prepStmt.executeUpdate();
			prepStmt.close();

		} catch (Exception e) {

			// SQLite error codes: https://www.sqlite.org/c3ref/c_abort.html
			// MySQL error codes:
			// https://dev.mysql.com/doc/refman/5.5/en/error-messages-server.html
			if (((SQLException) e).getErrorCode() == 1451) {
				System.out.println("Error code: " + ((SQLException) e).getErrorCode());
				throw new SQLException("[SQL_CONSTRAINT]");
			}

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());

		} finally {
			c.close();
		}
	}

	void updateCategory(String updatedCategory, int categoryID) throws Exception {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("UPDATE categories SET category = ? WHERE id = ?;");

			prepStmt.setString(1, updatedCategory);
			prepStmt.setInt(2, categoryID);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	/**
	 * Try to insert new user into 'users' table. If user email already exist
	 * catch error and hand it to service.
	 * 
	 * @param newUser
	 *            model which contain user information.
	 */
	void insertNewUser(User newUser) throws SQLException {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("INSERT INTO users (email,password, type) VALUES (?, ?, 1);");

			prepStmt.setString(1, newUser.email);
			prepStmt.setString(2, newUser.password);
			prepStmt.executeUpdate();
			prepStmt.close();

		} catch (Exception e) {

			// SQLite error codes: https://www.sqlite.org/c3ref/c_abort.html
			if (((SQLException) e).getErrorCode() == 19) {
				throw new SQLException("User already exist");
			}

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());

		} finally {
			c.close();
		}
	}

	void insertUserUUID(User userToUpdate, String UUID) throws SQLException {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = getConnection();

			prepStmt = c.prepareStatement("UPDATE users SET cookie_uuid = ? WHERE email = ?");

			prepStmt.setString(1, userToUpdate.email);
			prepStmt.setString(2, UUID);
			prepStmt.executeUpdate();

			prepStmt.close();

		} catch (Exception e) {

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
	}

	String[] getUserUUID(String cookieUUID) throws Exception {

		Connection c = null;
		String userUUID[] = null;

		try {
			// Connection
			c = getConnection();

			PreparedStatement prepStmt = c
					.prepareStatement("SELECT email,password,type,cookie_uuid FROM users WHERE cookie_uuid = ?;");

			prepStmt.setString(1, cookieUUID);
			ResultSet rs = prepStmt.executeQuery();

			// Check if there are any results.
			// Initialize array for data.
			userUUID = new String[rs.getMetaData().getColumnCount()];

			// Check if there are any results.
			if (!rs.isBeforeFirst()) {
				System.out.println("No such user");
				return new String[] { "No such user" };
			}

			while (rs.next()) {
				userUUID[0] = rs.getString(1);
				userUUID[1] = rs.getString(2);
				userUUID[2] = rs.getString(3);
				userUUID[3] = rs.getString(4);
			}

			prepStmt.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} finally {
			c.close();
		}
		return userUUID;
	}

	/**
	 * Trims white spaces and non-visible characters, checks for empty strings
	 * ("") and substrings strings (from 0 to 15) to be inserted to database.
	 * 
	 * @param stringToCut
	 * @return cut string
	 */
	private String stringBarber(String stringToCut) {
		/*
		 * Strings are constant; their values cannot be changed after they are
		 * created, hence local assignments;
		 */

		// Removes all white-spaces and non-visible characters;
		String trimmedString = stringToCut.replaceAll("\\s+", "");
		// User provided name but deletes it leaving us with empty string ("").
		if (trimmedString == "")
			return null;

		if (trimmedString.length() > 15) {
			String trimmedAndSubstringed = trimmedString.substring(0, 15);
			return trimmedAndSubstringed;
		}
		return trimmedString;
	}
}
