
public class FolderNameNotFoundException extends Exception {
	public FolderNameNotFoundException(String folderName, String path)
	{
		super("Error the folder named '" + folderName + "' seems not exist in the path '"+path+"'");
	}

}
