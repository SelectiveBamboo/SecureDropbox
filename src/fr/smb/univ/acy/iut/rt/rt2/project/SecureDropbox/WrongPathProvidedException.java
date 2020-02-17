package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;

public class WrongPathProvidedException extends Exception {
	
	public WrongPathProvidedException(String pathProvided)
	{
		super("\nWrong path provided: " + pathProvided);
	}

}
