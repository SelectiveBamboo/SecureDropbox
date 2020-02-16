import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class fsTreeStorage {
	
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
		
	    String str = "\r\n"+stringToAppen;
	 
	    new File(pathToFile).mkdirs();
	    
	    File file = new File(pathToFile+fileName);
	    
	    FileWriter fr = new FileWriter(file, true);
	    fr.write(str);
	    fr.close();
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
			
		if (j<filesInfos.size())	//Means an index match to the specified nameOnCloud
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
