package customExceptions;

public class InvalidValueException extends Exception{
    public InvalidValueException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public InvalidValueException(Throwable err) {
        super("Invalid Scenario Parameter Value.", err);
    }
    public InvalidValueException(String errorMessage) {
        super(errorMessage);
    }
}
