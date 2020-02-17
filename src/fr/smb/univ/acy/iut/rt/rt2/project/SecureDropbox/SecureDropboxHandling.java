package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

public class SecureDropboxHandling extends Thread {

	protected static final int BUFFER_SIZE = 1;
	public static String parameterPath = "SecureDropbox_params/";
	public static final String FI_FILENAME = "FIstore.json";
	
	private String nameOfLocalFile;
	private String pathToFile;
	private String actionOnFile;
	private List<Cloud> clouds;
	
	public SecureDropboxHandling(String nameOfLocalFile, String pathToFile, String actionOnFile, List<Cloud> clouds)
	{
		this.nameOfLocalFile = nameOfLocalFile;
		this.pathToFile = pathToFile;
		this.actionOnFile = actionOnFile;
		this.clouds = clouds;
	}
	
	
	public void run()
	{
		if (actionOnFile.equals("deleted"))
		{
			deleteFilesOnCloud(nameOfLocalFile, pathToFile, clouds);
		}
		else if (actionOnFile.equals("modified"))
		{
			putFilesChangesOnCloud(nameOfLocalFile, pathToFile, clouds);
		}
		else if (actionOnFile.equals("created")) 
		{
			putFilesCreationOnCloud(nameOfLocalFile, pathToFile, clouds);
		}
		else
		{
			System.err.println("ERROR: unrecognized action on a file:" + pathToFile + nameOfLocalFile);
			System.exit(1);
		}
	}
	
	private void deleteFilesOnCloud(String nameOfFile, String pathToFile, List<Cloud> clouds)
	{
		List<FileInfos> lfi;
		int index = -1;
		FileInfos fileInfos = null;
		try {
			lfi = fsTreeStorage.extractFilesInfosFromFile(parameterPath, FI_FILENAME);
			try {
				index = fsTreeStorage.getIndexByFileOnLocal(pathToFile, nameOfLocalFile, lfi);
			} 
			catch (NoMatchForElementException e)
			{
				e.printStackTrace();
				System.err.println("ERROR:" + e.getMessage());
			}
			fileInfos = lfi.get(index);
			
			
		} 
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		
		for (Cloud aCloud : clouds) 
		{
			try {
				if (aCloud instanceof CloudGoogleDrive) 
				{
					((CloudGoogleDrive) aCloud).deleteFile(nameOfLocalFile);
				}
				else if (aCloud instanceof SimpleCloud) 
				{
					((SimpleCloud) aCloud).deleteFile(nameOfLocalFile);
				}
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				System.err.println("ERROR: while deleting files on cloud");
			}
		}
		
		try {
			String finfosToDelete = fsTreeStorage.fileInfosToJSONString(fileInfos);
			fsTreeStorage.deleteStringInFile(finfosToDelete, parameterPath, FI_FILENAME);
		} 
		catch (JsonProcessingException e) 
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		} 
		catch (WrongPathProvidedException e)
		{
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}
	
	
	private void putFilesChangesOnCloud(String nameOfFile, String pathToFile, List<Cloud> clouds) 
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
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
				System.err.println("ERROR: while reading file on local");
			}
		
			DataSplitting splittedDatas = new DataSplitting(clouds.size()-1, bits);
			
			Parity parity = new Parity(splittedDatas.getHmap());
			
			SplitInFiles splitInFiles = new SplitInFiles(parity.getHmap);
			
			List<File> filesToSend = splitInFiles.getGeneratedFiles();
			
			List<FileInfos> lfi = fsTreeStorage.extractFilesInfosFromFile(parameterPath, FI_FILENAME);
			int indexToFileInfo = fsTreeStorage.getIndexByFileOnLocal(pathToFile, nameOfFile, lfi);
			
			if (indexToFileInfo != -1) 
			{
				for (Cloud aCloud : clouds) 
				{
					if (aCloud instanceof CloudGoogleDrive) 
					{
						((CloudGoogleDrive) aCloud).putFile(lfi.get(indexToFileInfo).getNameOnCloud(), filesToSend.get(0).getPath());
					}
					else if (aCloud instanceof SimpleCloud) 
					{
						((SimpleCloud) aCloud).putFile(lfi.get(indexToFileInfo).getNameOnCloud(), filesToSend.get(0).getPath());
					}
					
					filesToSend.get(0).delete();
					filesToSend.remove(0);
				}	
			}
			else
			{
				putFilesCreationOnCloud(nameOfFile, pathToFile, clouds);
			}
			
	}
	
	private void putFilesCreationOnCloud(String nameOfFile, String pathToFile, List<Cloud> clouds) 
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
			catch (IOException ioe) 
			{
				ioe.printStackTrace();
				System.err.println("ERROR: while reading file on local");
			}
		
			DataSplitting splittedDatas = new DataSplitting(clouds.size()-1, bits);
			
			Parity parity = new Parity(splittedDatas.getHmap());
			
			SplitInFiles splitInFiles = new SplitInFiles(parity.getHmap);
			
			List<File> filesToSend = splitInFiles.getGeneratedFiles();
			
			String nameOnCloud = fsTreeStorage.genNewUniqueFilename();
			
			for (Cloud aCloud : clouds) 
			{
				if (aCloud instanceof CloudGoogleDrive) 
				{
					((CloudGoogleDrive) aCloud).putFile(nameOnCloud, filesToSend.get(0).getPath());
				}
				else if (aCloud instanceof SimpleCloud) 
				{
					((SimpleCloud) aCloud).putFile(nameOnCloud, filesToSend.get(0).getPath());
				}
				
				filesToSend.get(0).delete();
				filesToSend.remove(0);
			}
			
			FileInfos fi = new FileInfos(pathToFile, nameOfFile, nameOnCloud);
			String jsonFi = fsTreeStorage.fileInfosToJSONString(fi);
			fsTreeStorage.appendStringToFile(jsonFi, parameterPath, FI_FILENAME);
	}

}
