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
	
	public SecureDropboxHandling(String pathToFile,String nameOfFile, String actionOnFile, List<Cloud> clouds) 
	{
		if (actionOnFile.equals("deleted"))
		{
			for (List<Cloud> aCloud : clouds) 
			{
				aCloud.delete(nameOfFile);
			}
		}
		else if(actionOnFile.equals("modified"))
		{
			BitSet bits;
			
			File fileIn = new File(pathToFile + nameOfFile);
			
			try (InputStream inputStream = new FileInputStream(fileIn.toString());
				) {
					byte[] buffer = new byte[1];
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
				
				int i = 1;
				for (List<Cloud> aCloud : clouds) 
				{
					aCloud.send(filesToSend.get(0).toString()+i);
					i++;
					
					filesToSend.get(0).delete();
					filesToSend.remove(0);
				}
		}
		else if (actionOnFile.equals("created"))
		{
			BitSet bits;
			
			File fileIn = new File(pathToFile + nameOfFile);
			
			try (InputStream inputStream = new FileInputStream(fileIn.toString());
				) {
					byte[] buffer = new byte[1];
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
				
				int i = 1;
				for (List<Cloud> aCloud : clouds) 
				{
					aCloud.send(filesToSend.get(0).toString()+i);
					i++;
					
					filesToSend.get(0).delete();
					filesToSend.remove(0);
				}
		}
		else
		{
			System.err.println("ERROR: unrecognized action on a file:" + pathToFile + nameOfFile);
			System.exit(1);
		}
	}

}
