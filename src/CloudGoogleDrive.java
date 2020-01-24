/*
 * Will need an improvement to deal only with file's Id 
 * to avoid problem with likewise named files (possible with google)
 * 
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import com.google.api.client.http.AbstractInputStreamContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

public class CloudGoogleDrive {
	
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
 
    
    // com.google.api.services.drive.model.File
    public List<File> getFilesByLikeName(String fileNameLike) throws IOException 
    {
        Drive driveService = GoogleDriveUtils.getDriveService();
         
        String pageToken = null;
        List<File> list = new ArrayList<File>();
 
        String query = " name contains '" + fileNameLike + "' " //
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
    
    
    public File getFilesByExactName(String exactFileName) throws IOException 
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
    
    
    public List<File> getGoogleSubFolders(String googleFolderIdParent) throws IOException 
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
    public List<File> getGoogleRootFolders() throws IOException 
    {
        return getGoogleSubFolders(null);
    }
    
    public OutputStream getInputStreamFile(String fileId) throws IOException 
    {
    	OutputStream outputStream = new ByteArrayOutputStream();

    	Drive driveService = GoogleDriveUtils.getDriveService();
    	
    	driveService.files().get(fileId)
    	    .executeMediaAndDownloadTo(outputStream);

    	
		return outputStream;
    }
}
