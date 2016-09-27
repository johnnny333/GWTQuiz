package pl.johnny.gwtQuiz.shared;

import org.hibernate.validator.constraints.Email;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable{
	
	public String email;
	public String password;
	
	/**
	 * Do NOT delete this default,public,no-arg constructor. It's necessary 
	 * for Serialization.
	 */
	public User() {
	};
		
	/** Used when user score is set and database sets the id of a record so 
	we don't do it here */
	public User(String email, String password){
		this.email = email;
		this.password = password;
	}
}