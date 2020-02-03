import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

public class SecureDropboxHandling extends Thread {
	
	protected static final int BUFFER_SIZE = 1;
	
	public SecureDropboxHandling(String pathToFile,String nameOfFile, String actionOnFile, List<Cloud> clouds) throws IOException 
	{
		if (actionOnFile.equals("deleted"))
		{
			for (Cloud aCloud : clouds) 
			{
				if (aCloud instanceof CloudGoogleDrive) 
				{
					((CloudGoogleDrive) aCloud).deleteFile(nameOfFile);
				}
				else if (aCloud instanceof SimpleCloud) 
				{
					((SimpleCloud) aCloud).deleteFile(nameOfFile);
				}
			}
		}
		else if (actionOnFile.equals("created") || actionOnFile.equals("modified"))
		{
			putFileChangesOnCloud(nameOfFile, pathToFile, clouds);
		}
		else
		{
			System.err.println("ERROR: unrecognized action on a file:" + pathToFile + nameOfFile);
			System.exit(1);
		}
	}
	
	private void putFileChangesOnCloud(String nameOfFile, String pathToFile, List<Cloud> clouds) 
	{
		BitSet bits;
		
		File fileIn = new File(pathToFile + nameOfFile);
		
		try (InputStream inputStream = new FileInputStream(fileIn.toString());
			) {
				byte[] buffer = new byte[BUFFER_SIZE];
				String text = null;

				while (inputStream.read(buffer) != -1) {
					
					text += new String(buffer);
					System.out.println("File content: "+text);

					bits = BitSet.valueOf(new BigInteger(text.getBytes()).toByteArray());
				}
			} 
			catch (IOException ex) 
			{
				ex.printStackTrace();
			}
		
			DataSplitting splittedDatas = new DataSplitting(clouds.size()-1, bits);
			
			Parity parity = new Parity(splittedDatas.getHmap());
			
			SplitInFiles splitInFiles = new SplitInFiles(parity.getHmap);
			
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
