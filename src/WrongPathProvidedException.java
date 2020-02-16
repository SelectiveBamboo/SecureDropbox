
public class WrongPathProvidedException extends Exception {
	
	public WrongPathProvidedException(String pathProvided)
	{
		super("\nWrong path provided: " + pathProvided);
	}

}
