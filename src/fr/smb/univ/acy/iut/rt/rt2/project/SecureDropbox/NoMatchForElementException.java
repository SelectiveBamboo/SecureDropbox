package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;

public class NoMatchForElementException extends Exception {
	
	public NoMatchForElementException(String message)
	{
		super("No element matched " + message);
	}

}
