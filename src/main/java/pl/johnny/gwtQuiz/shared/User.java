package pl.johnny.gwtQuiz.shared;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gwt.user.client.rpc.IsSerializable;

public class User implements IsSerializable{
	
	@NotEmpty
	@Email(groups = ServerGroup.class)
	public String email;
	
	@NotEmpty
	@Size(min=8)
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]", 
				message="Password must contain at least 1 Uppercase Alphabet, " 
				+ "1 Lowercase Alphabet, 1 Number and 1 Special Character:", groups = ServerGroup.class) //And minimum 8 characters
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