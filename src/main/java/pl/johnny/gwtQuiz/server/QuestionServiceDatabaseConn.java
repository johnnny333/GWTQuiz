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

import org.apache.commons.io.FileUtils;

import pl.johnny.gwtQuiz.shared.FailedLoginException;
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
	QuestionServiceDatabaseConn() {
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
	 */
	ArrayList<Question> getQuestions() {

		Connection c;
		Statement stmt;
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
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			int rowsCount = rsRowCount.getInt(1);

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
			c.close();

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
		}
		return questionsArray;
	}

	/**
	 * Get user points and store them into an array.
	 * 
	 * @return
	 */
	ArrayList<UserScore> getUserScores() {

		Connection c;
		Statement stmt;

		int[] userScoreID;
		String[] userDisplay;
		int[] userScores;
		Boolean[] isEditable;
		// Array for each record timestamp
		String[] usersScoresCreatedAt;
		ArrayList<UserScore> userScoresArray = new ArrayList<UserScore>();

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

			// Actual query
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
			c.close();

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
		}
		return userScoresArray;
	}

	void insertUserScore(UserScore userScore) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement(
					"INSERT INTO user_scores (user_display,user_score,is_editable) VALUES (?, ?, ?);");

			// if(userScore.userDisplay == "");else throw new
			// Exception("Temporary user score name field is not empty(\"\")");
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
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

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
			;

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
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement("DELETE FROM user_scores WHERE ID=?;");

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
	 * 
	 * @return
	 */
	String[][] getCategories() {

		Connection c;
		Statement stmt;
		
		/**
		 * Structure is as follows:
		 * {"1", "Geografia},
		 * {"2", "Muzyka"},
		 * {"3", "Polityka"},
		 * ...
		 */
		String[][] categoryData = null;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM categories;");
			int rowsCount = rsRowCount.getInt(1);

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
			c.close();

			// Return filled category data.
			// return categoryData;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}
		// Return filled category data.
		return categoryData;
	}
	
	String[][] getCategory(String category) {
		
		String[][] categoryData = null;

		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			
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
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}
		// Return user password.
		return categoryData;
	}

	/**
	 * Insert submitted user question into question_tmp table.
	 * 
	 * @param userQuestion
	 */
	void insertUserQuestion(Question userQuestion) {

		String questionID = null;

		// Insert user question into question_tmp
		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO questions_tmp (question,author,category_id,has_image,image_url) VALUES (?,?,?,?,?);");

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
				String trimmedImageURL = userQuestion.getImageURL()
						.substring(userQuestion.getImageURL().lastIndexOf("/") + 1);
				prepStmt.setString(5, trimmedImageURL);
			} else {
				prepStmt.setString(4, "0");
				prepStmt.setString(5, userQuestion.getImageURL());
			}
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}

		// Insert user-question answers into answers_tmp
		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			/*
			 * Count rows from question_tmp to relate answers being now inserted
			 * into answers_tmp with appropriate question which was just
			 * inserted into questions_tmp (this is backed by foreign key);
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

		// Move uploaded image from /tmp to our pending storage.
		if (userQuestion.getImageURL() != null) {
			try {

				FileUtils.moveFileToDirectory(
						/*
						 * getImageURL resolves to full,absolute path of
						 * uploaded image and was brought from servlet response
						 * after successful image upload.So here we just use it
						 * to point to a source file.
						 */
						FileUtils.getFile(userQuestion.getImageURL()),
						FileUtils.getFile("quiz_resources/question_images_tmp/" + questionID), true);

			} catch (IOException e) {
				System.err.println(e);
			}
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
	 */
	ArrayList<Question> getTmpQuestions() {

		Connection c;
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
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			stmt = c.createStatement();

			// Count rows
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions_tmp;");
			int rowsCount = rsRowCount.getInt(1);

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
			c.close();

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
		}
		return questionsTmpArray;
	}

	/**
	 * Deletes temporary user question and temporary user answer by their ID.
	 * 
	 * @param questionID
	 */
	void deleteUserTmpQuestion(String questionID) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement("DELETE FROM answers_tmp WHERE ID=?;");

			prepStmt.setString(1, questionID);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			System.exit(0);
		}

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement("DELETE FROM questions_tmp WHERE ID=?;");

			prepStmt.setString(1, questionID);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}

		// Delete question image.If there is none image,no exception will be
		// thrown hence, deleteQuietly.
		FileUtils.deleteQuietly(new File("quiz_resources/question_images_tmp/" + questionID));

	}

	void acceptUserTmpQuestion(Question userQuestion, String tmpQuestionID) {

		Integer questionID = null;

		// Insert user question into question_tmp
		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO questions (question,author,category_id,has_image,image_url) VALUES (?,?,?,?,?);");

			prepStmt.setString(1, userQuestion.getQuestion());
			prepStmt.setString(2, userQuestion.getAuthor());
			prepStmt.setString(3, userQuestion.getCategory());

			/*
			 * Get rows count from questions table to determine last inserted
			 * record ID.
			 */
			Statement stmt = c.createStatement();
			ResultSet rsRowCount = stmt.executeQuery("SELECT COUNT(*) FROM questions;");
			questionID = rsRowCount.getInt(1);

			/*
			 * If user submitted image, set has_image column flag to
			 * 1(true).Otherwise its 0(false). Also, in if block set image name
			 * - trimmed from full path - or - in else block - null if none
			 * image is provided .
			 */
			if (userQuestion.getImageURL() != null) {
				prepStmt.setString(4, "1");
				String trimmedImageURL = userQuestion.getImageURL()
						.substring(userQuestion.getImageURL().lastIndexOf("/") + 1);
				prepStmt.setString(5, "quiz_resources/question_images/" + (questionID + 1) + "/" + trimmedImageURL);
			} else {
				// If there is no image, set has_image to 0 and image_url to
				// null (which was handed in model).
				prepStmt.setString(4, "0");
				prepStmt.setString(5, userQuestion.getImageURL());
			}
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}

		// Insert user-question answers into answers_tmp
		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			PreparedStatement prepStmt = c.prepareStatement(
					"INSERT INTO answers (questionID,answer1,answer2,answer3,answer4,correct_answer) VALUES (?,?,?,?,?,?);");

			prepStmt.setInt(1, (questionID + 1));
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
		}

		/*
		 * Move uploaded image from
		 * quiz_resources/question_images_tmp/{question_id}/{file_name} to our
		 * main storage. After this, delete empty directory in
		 * quiz_resources/question_images_tmp.
		 */
		if (userQuestion.getImageURL() != null) {
			try {

				FileUtils.moveFileToDirectory(

						FileUtils.getFile("quiz_resources/question_images_tmp/" + tmpQuestionID + "/"
								+ userQuestion.getImageURL()),
						FileUtils.getFile("quiz_resources/question_images/" + (questionID + 1)), true);

				FileUtils.deleteDirectory(new File("quiz_resources/question_images_tmp/" + tmpQuestionID));

			} catch (IOException e) {
				System.err.println(e);
			}
		}

		// Finally delete this particular user question and answers from
		// temporary tables.
		deleteUserTmpQuestion(tmpQuestionID);

	}

	/**
	 * Get user password by given email.
	 * 
	 * @param user
	 * @return
	 */
	String[] getUser(User user) {

		String passwordData[] = null;

		try {
			// Connection
			Connection c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

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
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			// System.exit(0);
		}
		// Return user password.
		return passwordData;
	}

	void insertNewCategory(String newCategory) {

		Connection c;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");

			prepStmt = c.prepareStatement("INSERT INTO categories (category) VALUES (?);");

			prepStmt.setString(1, newCategory);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		}
	}

	void deleteCategory(String categoryToDelete) throws SQLException {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			// Non transaction.
			c.setAutoCommit(true);
			c.createStatement().execute("PRAGMA foreign_keys = ON;");

			prepStmt = c.prepareStatement("DELETE FROM categories WHERE category=?;");

			prepStmt.setString(1, categoryToDelete);
			prepStmt.executeUpdate();
			
		} catch (Exception e) {

			if (((SQLException) e).getErrorCode() == 19) {
				throw new SQLException("[SQLITE_CONSTRAINT]");
			}

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
			
		} finally {
			
			prepStmt.close();
			c.close();
		}
	}
	
	void updateCategory(String updatedCategory, int categoryID) {

		Connection c = null;
		PreparedStatement prepStmt = null;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			// Non transaction.
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON;");

			prepStmt = c.prepareStatement("UPDATE categories SET category = ? WHERE id = ?;");

			prepStmt.setString(1, updatedCategory);
			prepStmt.setInt(2, categoryID);
			prepStmt.executeUpdate();
			
			prepStmt.close();
			c.commit();
			c.close();
			
		} catch (Exception e) {

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());	
		}
	}
	
	/**
	 * Try to insert new user into 'users' table. If user email already exist catch error 
	 * and hand it to service.
	 * @param newUser model which contain user information.
	 */
	void insertNewUser(User newUser)  throws SQLException {

		Connection c;
		PreparedStatement prepStmt;

		try {
			// Connection
			c = DriverManager.getConnection("jdbc:sqlite:quiz_resources/questions_database/questions.db");
			c.setAutoCommit(false);
			c.createStatement().execute("PRAGMA foreign_keys = ON");
			
			prepStmt = c.prepareStatement("INSERT INTO users (email,password, type) VALUES (?, ?, 1);");

			prepStmt.setString(1, newUser.email);
			prepStmt.setString(2, newUser.password);
			prepStmt.executeUpdate();

			prepStmt.close();
			c.commit();
			c.close();

		} catch (Exception e) {

//			if (e.getMessage().contains("UNIQUE constraint failed")) {
//				System.err.println("Canonical name of SQL Error: " + 
//						"Canonical " + e.getClass().getCanonicalName() + " | name " + e.getClass().getName());
//				throw new SQLException("UNIQUE constraint failed");
//			}
			
			//SQLite error codes: https://www.sqlite.org/c3ref/c_abort.html
			if (((SQLException) e).getErrorCode() == 19) {
				System.err.println("Canonical name of SQL Error: " + 
						"Canonical " + e.getClass().getCanonicalName() + " | name " + e.getClass().getName() + 
						" ((SQLException) e).getErrorCode(); " + ((SQLException) e).getErrorCode() + 
						" |SQL error message " + e.getMessage());

				throw new SQLException("UNIQUE constraint failed");
			}

			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			System.err.println(e.getCause() + " " + e.getStackTrace());
		} 
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