/*
 * Path in CloudGoogleDrive is actualy the Id of the folder in which write
 * 
 * 
 * Will need an improvement to deal only with file's Id 
 * to avoid problem with likewise named files (possible with google)
 * 
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class CloudGoogleDrive extends Cloud {
	
	public CloudGoogleDrive(String folder) throws Exception, IOException, FolderNameNotFoundException
	{
		GoogleDriveUtils.getDriveService();		
		
			if (folder.contentEquals("")) 
			{
				folder = getRootFolder().get(0).getId();	//Google drive does not deal with names but IDs, so getting the Id
			}
			else if(folder.matches("^/?([a-zA-Z_0-9]+/)+$"))	//if match a folder path 
			{
				folder = getFolderByName(folder).getId();	//Google drive does not deal with names but IDs, so getting the Id
			}
			else 
			{
				throw new Exception();
			}
	}

	
    private File _createFile(String googleFolderIdParent, String contentType, //
            String customFileName, AbstractInputStreamContent uploadStreamContent) throws IOException 
    {
        File fileMetadata = new File();
        fileMetadata.setName(customFileName);
 
        List<String> parents = Arrays.asList(googleFolderIdParent);
        fileMetadata.setParents(parents);
        
        Drive driveService = GoogleDriveUtils.getDriveService();
 
        File file = driveService.files().create(fileMetadata, uploadStreamContent)
                .setFields("id, webContentLink, webViewLink, parents").execute();
 
        return file;
    }
 
    
    // Create Google File from byte[]
    public File createFile(String googleFolderIdParent, String contentType, //
            String customFileName, byte[] uploadData) throws IOException 
    {    
        AbstractInputStreamContent uploadStreamContent = new ByteArrayContent(contentType, uploadData);
        
        return _createFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }
    
    // Create Google File from java.io.File
    public File createFile(String googleFolderIdParent, String contentType, //
            String customFileName, java.io.File uploadFile) throws IOException //
    {
        AbstractInputStreamContent uploadStreamContent = new FileContent(contentType, uploadFile);
        //
        return _createFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }
 
    // Create Google File from InputStream
    public File createFile(String googleFolderIdParent, String contentType, //
            String customFileName, InputStream inputStream) throws IOException //
    {
        AbstractInputStreamContent uploadStreamContent = new InputStreamContent(contentType, inputStream);
        //
        return _createFile(googleFolderIdParent, contentType, customFileName, uploadStreamContent);
    }
 
    
    private void updateFile(Drive drive, String fileId, String nameOnCloud) throws IOException 
    {
    	File file = drive.files().get(fileId).execute();
		java.io.File fileContent = new java.io.File(nameOnCloud);
	    FileContent mediaContent = new FileContent( "text/plain", fileContent);

	    // Send the request to the API.
	    drive.files().update(fileId, file, mediaContent).execute();
    }
    
    // com.google.api.services.drive.model.File
    public List<File> getFilesByLikeName(String fileNameLike) throws IOException 
    {
        Drive driveService = GoogleDriveUtils.getDriveService();
         
        String pageToken = null;
        List<File> list = new ArrayList<File>();
 
        String query = " name contains '" + fileNameLike + "' " + " and '"
        		+ folder + "' in parents" //
                + " and mimeType != 'application/vnd.google-apps.folder' ";
 
        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime, mimeType
                    .setFields("nextPageToken, files(id, name, createdTime, mimeType)")//
                    .setPageToken(pageToken).execute();
           
            for (File file : result.getFiles()) 
            {
                list.add(file);
            }
            
            pageToken = result.getNextPageToken();    
        } 
        while (pageToken != null);
        
        return list;
    }
    
    
    public File getFileByExactName(String exactFileName) throws IOException 
    {
    	File file = null;
    	
    	List<File> listFile = getFilesByLikeName(exactFileName);
                
    	for (File f : listFile) 
    	{
    		if (f.getName().equals(exactFileName)) 
    		{
    			file = f;
    		}	
    	}
        	
    	return file;
    }
    
    // com.google.api.services.drive.model.File
    public File getFolderByName(String path) throws FolderNameNotFoundException
    {
    	File f = null;
    	
    	try {
    		List<File> foldersReturned;
    		f = getRootFolder().get(0);

    		String[] subfolders = path.split("/");

    		for (int i = 0; i < subfolders.length; i++) 
    		{
    			foldersReturned = getSubFolders(f.getId());

    			int j = 0;
    			while ( !foldersReturned.get(j).getName().equals(subfolders[i]) && j<foldersReturned.size() ) 
    			{
    				j++;
    			}

    			if ( j<foldersReturned.size() ) 
    			{
    				f = foldersReturned.get(j);
    			}
    			else
    			{
    				throw new FolderNameNotFoundException(subfolders[i], path);
    			}
    		}
    	}
    	catch (IOException ioe)
    	{
    		ioe.printStackTrace();
    	}
    	
    	return f;
    }
    
    public List<File> getSubFolders(String googleFolderIdParent) throws IOException 
    {
        Drive driveService = GoogleDriveUtils.getDriveService();
 
        String pageToken = null;
        List<File> list = new ArrayList<File>();
 
        String query = null;
        if (googleFolderIdParent == null) 
        {
            query = " mimeType = 'application/vnd.google-apps.folder' " //
                    + " and 'root' in parents";
        } 
        else
        {
            query = " mimeType = 'application/vnd.google-apps.folder' " //
                    + " and '" + googleFolderIdParent + "' in parents";
        }
 
        do {
            FileList result = driveService.files().list().setQ(query).setSpaces("drive") //
                    // Fields will be assigned values: id, name, createdTime
                    .setFields("nextPageToken, files(id, name, createdTime)")//
                    .setPageToken(pageToken).execute();
            
            for (File file : result.getFiles()) 
            {
                list.add(file);
            }
            
            pageToken = result.getNextPageToken();
        } 
        while (pageToken != null);
        
        return list;
    }
 
    
    // com.google.api.services.drive.model.File
    public List<File> getRootFolder() throws IOException 
    {
        return getSubFolders(null);
    }
    
    
    public java.io.File getFile(String fileName) throws IOException 
    {
    	java.io.File file = new java.io.File(fileName);
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    	Drive driveService = GoogleDriveUtils.getDriveService();
    	
    	String fileId = getFileByExactName(fileName).getId();
    	
    	driveService.files().get(fileId)
    	    .executeMediaAndDownloadTo(outputStream);

    	try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file))
    	{
    		fos.write(outputStream.toByteArray());
    	}
    	
		return file;
    }
    
    public void putFile(String nameOnCloud, String localFilePath) throws IOException 
    { 	
    	java.io.InputStream fis = new java.io.FileInputStream(new java.io.File(localFilePath));

    	Drive drive = GoogleDriveUtils.getDriveService();
    	String fileId = getFileByExactName(nameOnCloud).getId();

    	if (fileId != null) 
    	{
    		updateFile(drive, fileId, nameOnCloud);
    	}
    	else
    	{
    		createFile(folder, "text/plain", nameOnCloud, fis);
    	}
			
    	fis.close();  	
    }
    
    
    public void deleteFile(String fileName) throws IOException 
    {
    	Drive driveService = GoogleDriveUtils.getDriveService();
    	
    	String fileId = getFileByExactName(fileName).getId();
    	driveService.files().delete(fileId);
    }
}
