package pl.johnny.gwtQuiz.shared;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * User model which contains user email and password.
 * @author jzarewicz
 *
 */
public class User implements IsSerializable{
	
	@NotEmpty
	@Pattern(regexp ="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.(?:[a-zA-Z]{2,6})$",
						message = "Not a well-formed email address client")
	@Email(groups = ServerGroup.class)
	public String email;
	
	@NotEmpty
	@Size(min=8, message = "size must be minimum or equal to 8 chars")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[$@$!%*?&])[A-Za-z\\d$@$!%*?&]{8,}", 
				message="Password must be equal or at least 8 chars long, contain at least one UPPERCASE letter, " 
				+ "at least one lowercase letter,at least one number and at least one special character: $@$!%*?& ") //And minimum 8 characters
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