/**
 * @author jzarewicz
 */
package pl.johnny.gwtQuiz.shared;

import java.io.Serializable;

/**
 * 
 * @author jzarewicz
 *
 */
public class FailedLoginException extends Exception implements Serializable {
	
	private String symbol;

    public FailedLoginException() {
    	super();
    }

    public FailedLoginException(String message) {
        super(message);
    }
    
    public FailedLoginException(Exception e) {
		super(e);
	}
}