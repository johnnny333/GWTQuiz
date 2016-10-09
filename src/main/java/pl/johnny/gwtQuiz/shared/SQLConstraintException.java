package pl.johnny.gwtQuiz.shared;

import java.io.Serializable;

public class SQLConstraintException extends Exception implements Serializable {

    public SQLConstraintException()
    {
        super();
    }

    public SQLConstraintException(String message)
    {
        super(message);
    }

    public SQLConstraintException(Exception e){
        super(e);
    }
}