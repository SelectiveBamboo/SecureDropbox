import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;


public class SecureDropboxHandling extends Thread {

	protected static final int BUFFER_SIZE = 1;
	
	private String nameOfFile;
	private String pathToFile;
	private String actionOnFile;
	private List<Cloud> clouds;
	
	public SecureDropboxHandling(String nameOfFile, String pathToFile, String actionOnFile, List<Cloud> clouds)
	{
		this.nameOfFile = nameOfFile;
		this.pathToFile = pathToFile;
		this.actionOnFile = actionOnFile;
		this.clouds = clouds;
	}
	
	
	public void run()
	{
		if (actionOnFile.equals("ENTRY_DELETE"))
		{
			for (Cloud aCloud : clouds) 
			{
				try {
					if (aCloud instanceof CloudGoogleDrive) 
					{
						((CloudGoogleDrive) aCloud).deleteFile(nameOfFile);
					}
					else if (aCloud instanceof SimpleCloud) 
					{
						((SimpleCloud) aCloud).deleteFile(nameOfFile);
					}
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
					System.err.println("ERROR: while deleting files on cloud");
				}
			}
		}
		else if (actionOnFile.equals("ENTRY_CREATE") || actionOnFile.equals("ENTRY_MODIFIY"))
		{
			try {
				putFilesChangesOnCloud(nameOfFile, pathToFile, clouds);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.err.println("ERROR:" + e.getMessage());
			}
		}
		else
		{
			System.err.println("ERROR: unrecognized action on a file:" + pathToFile + nameOfFile);
			System.exit(1);
		}
	}
	
	private void putFilesChangesOnCloud(String nameOfFile, String pathToFile, List<Cloud> clouds) throws IOException 
	{
		BitSet bits = new BitSet();
		
		File fileIn = new File(pathToFile + nameOfFile);
		
		try (InputStream inputStream = new FileInputStream(fileIn.toString());
			) {
				byte[] buffer = new byte[BUFFER_SIZE];
				String text = null;

				while (inputStream.read(buffer) != -1) {
					
					text += new String(buffer);
					//System.out.println("File content: "+text);

					bits = BitSet.valueOf(new BigInteger(text.getBytes()).toByteArray());
				}
			} 
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
				System.err.println("ERROR: while putting file changes on cloud");
				
				return;
			}
		
			DataSplitting splittedDatas = new DataSplitting(clouds.size()-1, bits);
			
			Parity parity = new Parity(splittedDatas.getHmap());
			
			SplitInFiles splitInFiles = new SplitInFiles(parity.getHash());
			
			List<File> filesToSend = splitInFiles.getGeneratedFiles();
			
			for (Cloud aCloud : clouds) 
			{
				if (aCloud instanceof CloudGoogleDrive) 
				{
					((CloudGoogleDrive) aCloud).putFile(filesToSend.get(0).getName(), filesToSend.get(0).getPath());
				}
				else if (aCloud instanceof SimpleCloud) 
				{
					((SimpleCloud) aCloud).putFile(filesToSend.get(0).getName(), filesToSend.get(0).getPath());
				}
				
				filesToSend.get(0).delete();
				filesToSend.remove(0);
			}
	}
}
