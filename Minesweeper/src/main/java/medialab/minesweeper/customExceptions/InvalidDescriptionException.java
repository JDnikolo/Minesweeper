package medialab.minesweeper.customExceptions;

public class InvalidDescriptionException extends Exception{
    public InvalidDescriptionException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
    public InvalidDescriptionException(Throwable err) {
        super("Invalid Scenario Description.", err);
    }
}
