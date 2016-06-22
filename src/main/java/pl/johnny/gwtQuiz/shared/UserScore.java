package pl.johnny.gwtQuiz.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UserScore implements IsSerializable{
	public String playerDisplay;
	public String score;
	public boolean isThisCellEditable;
	
	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public UserScore() {
	};

	public UserScore(String playerDisplay, String score, boolean isThisCellEditable) {
		this.playerDisplay = playerDisplay;
		this.score = score;
		this.isThisCellEditable = isThisCellEditable;
	}
}
