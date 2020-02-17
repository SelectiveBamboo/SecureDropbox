package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class fsTreeStorage {
	
	public static int filename_length = 8;
	public static String parameterPath = "SecureDropbox_params/";
	public static final String FI_FILENAME = "FIstore.json";
	
	private static String regexFolder = "^/?([a-zA-Z_0-9]+/)+$";
	
	public static String genRandomString(int stringLength) 
	{		
		String charc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String str = "";
		
		for (int i = 0; i < stringLength; i++) 
		{
			int r = (int) Math.floor(Math.random() * 62); // multiplied by the number of char in string charc
			str += charc.charAt(r);
		}
		
		return str;
	}
	
	public static String fileInfosToJSONString(FileInfos fileInfos) throws JsonProcessingException 
	{		
		String jsonString;
		
		ObjectMapper mapper = new ObjectMapper();
		jsonString = mapper.writeValueAsString(fileInfos);
		
		return jsonString;
	}

	public static FileInfos JSONStringToFileInfos(String jsonString) throws JsonParseException, JsonMappingException, IOException
	{		
		ObjectMapper mapper = new ObjectMapper();
		FileInfos fileInfos = mapper.readValue(jsonString, FileInfos.class);
		
		return fileInfos;
	}
	
	public static void appendStringToFile(String stringToAppen, String pathToFile, String fileName) throws IOException, WrongPathProvidedException
	{
		if( !pathToFile.matches(regexFolder) )	//if does not match a folder path (linux like)
		{
			throw new WrongPathProvidedException(pathToFile);
		}
		
	    String str = System.getProperty("line.separator") + stringToAppen;
	 
	    new File(pathToFile).mkdirs();
	    
	    File file = new File(pathToFile+fileName);
	    
	    FileWriter fr = new FileWriter(file, true);
	    fr.write(str);
	    fr.close();
	}
	
	public static void deleteStringInFile(String stringToDelete, String pathToFile, String fileName) throws IOException, WrongPathProvidedException
	{
		if( !pathToFile.matches(regexFolder) )	//if does not match a folder path (linux like)
		{
			throw new WrongPathProvidedException(pathToFile);
		}
		File inputFile = new File(pathToFile+fileName);
		File tempFile = new File(pathToFile+fileName+".temp");

		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

		String currentLine;

		while((currentLine = reader.readLine()) != null) 
		{
			String trimmedLine = currentLine.trim();		// Assume the line should be trimmed

			if(trimmedLine.equals(stringToDelete))
			{ continue; }									//If the string equals the string to delete its writing is avoided
			
			writer.write(currentLine + System.getProperty("line.separator"));
		}

		writer.close(); 
		reader.close(); 
	}
	
	public static List<FileInfos> extractFilesInfosFromFile(String pathToFile, String fileName) throws IOException, FileNotFoundException 
	{
		List<FileInfos> listFI = new ArrayList<FileInfos>();
		
		File file = new File(pathToFile+fileName);
		
		FileInputStream fis = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line;
		while ((line = br.readLine()) != null) 
		{
			if ( !line.equals("") ) 
			{
				try {
					listFI.add(JSONStringToFileInfos(line));
				} 
				catch (JsonParseException e) 
				{
					System.err.println("ERROR: "+ e.getMessage());
					System.err.println("Skipping the line anyway.");
				}
				catch (JsonMappingException e) 
				{
					System.err.println("ERROR: "+ e.getMessage());
					System.err.println("Skipping the line anyway.");
				}
			}
		}
	 
		br.close();
		
		return listFI;
	}
	
	public static int getIndexByNameOnCloud(String nameOnCloud, List<FileInfos> filesInfos)
		//return null if specified nameOnCloud hasn't been found
	{
		int i = -1;
		
		int j = 0;
		while ( j<filesInfos.size() 
				&& !filesInfos.get(j).getNameOnCloud().equals(nameOnCloud)) 
		{ j++; }		//iterate over list to find which fileInfos has this name on cloud
			
		if (j<filesInfos.size())	//Would mean an index match to the specified nameOnCloud
		{
			i = j;
		}
		
		return i;
	}
	
	public static int[] getIndexsByPathOnLocal(String pathOnLocal, List<FileInfos> filesInfos)
		//return an array containing all index of FileInfo corresponding to the specific pathOnLocal
	{
		int[] indexs = {};
		
		for (int i = 0; i < filesInfos.size(); i++) 
		{
			if (filesInfos.get(i).getPathOnLocal().equals(pathOnLocal)) 
			{
				indexs[indexs.length] = i;
			}
		}
		
		return indexs;
	}
	
	public static int getIndexByFileOnLocal(String pathOnLocal, String localFilename, List<FileInfos> filesInfos) throws NoMatchForElementException
	//return an int containing the index of the FileInfo corresponding to the specific file ( which is spotted with path to it + name of it)
	{
		int index = -1;
		boolean found = false;
		
		int i = 0;
		do
		{
			if (filesInfos.get(i).getPathOnLocal().equals(pathOnLocal)) 
			{
				if (filesInfos.get(i).getNameOnLocal().equals(localFilename))
				{
					index = i;
					found = true;	
				}
			}
			i++;	
		}
		while(!found && i<filesInfos.size());
		
		if (index == -1) 
		{
			throw new NoMatchForElementException("\n\tpathOnLocal: "+pathOnLocal + "  localFilename: " + localFilename + System.getProperty("line.separator"));
		}
		
		return index;
	}
	
	public static String genNewUniqueFilename() throws FileNotFoundException, IOException
	{	
		String genFilename;
		boolean isUnique = false;
	
		List<FileInfos> lfi = extractFilesInfosFromFile(parameterPath, FI_FILENAME);
		
		do
		{
			genFilename = genRandomString(filename_length);
	
			if ( getIndexByNameOnCloud(genFilename, lfi) != -1) 
			{
				isUnique = true;
			}
		}
		while(!isUnique);
		
		return genFilename;
	}
	
	
	
	
	public static void main(String[] args) throws IOException, WrongPathProvidedException 
	{
		FileInfos fi = new FileInfos("/oups/aaa/re/", "Michae", "ASEZDERT");
		FileInfos fee = new FileInfos("/oups/aaa/re/", "Accept", "qqqqqqqqqqqqqqqqqqqq");
		
		appendStringToFile(fileInfosToJSONString(fi), "/home/jules/Bureau/", "testFsStorage");
		appendStringToFile(fileInfosToJSONString(fee), "/home/jules/Bureau/", "testFsStorage");	
		
		List<FileInfos> lfi = extractFilesInfosFromFile("/home/jules/Bureau/", "testFsStorage");
		
		int i = getIndexByNameOnCloud("qqqqqqqqqqqqqqqqqqqq", lfi);
		
		System.out.println(i);
		
	}
}
