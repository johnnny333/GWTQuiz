package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserScore implements IsSerializable{
	public int userScoreID;
	public String userDisplay;
	public int score;
	public boolean isEditable;
	
	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public UserScore() {
	};
	
	/** User score retrieved from database with id set by database */
	public UserScore(int userScoreID, String userDisplay, int score, boolean isEditable) {
		this.userScoreID = userScoreID;
		this.userDisplay = userDisplay;
		this.score = score;
		this.isEditable = isEditable;
	}
	
	/** Used when user score is set and database sets the id of a record so 
	we don't do it here */
	public UserScore(String userDisplay, int score, boolean isEditable) {
		this.userDisplay = userDisplay;
		this.score = score;
		this.isEditable = isEditable;
	}
}
