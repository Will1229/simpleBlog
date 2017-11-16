package exception;

public class PostServiceException extends Exception {
    static final long serialVersionUID = 0L;

    public PostServiceException(String message) {
        super(message);
    }
}
