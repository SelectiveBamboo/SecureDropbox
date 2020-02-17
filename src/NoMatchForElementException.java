
public class NoMatchForElementException extends Exception {
	
	public NoMatchForElementException(String message)
	{
		super("No element matched " + message);
	}

}
