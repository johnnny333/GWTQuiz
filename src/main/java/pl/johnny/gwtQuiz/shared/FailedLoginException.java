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
    }

    public FailedLoginException(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return this.symbol;
    }

}