import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.github.sardine.*;

public class SimpleCloud extends Cloud {
	
	
	//TODO 
	//Something with all these constructors !!!!
	
	/*
	public SimpleCloud(String url) throws IOException 
	{
		this(url, null, null, null);
	}
	
	public SimpleCloud(InetAddress ipAddress) throws IOException 
	{
		this(null, ipAddress, null, null);
	}
	*/
	public SimpleCloud(String url, String ipAddress, String username, String password, String folder ) throws IOException 
	{
			this.ipAddress = ipAddress;
			this.url = url;
			this.folder = folder;

			this.username = username;
			this.password = password;
			
			if (this.url == null) 
			{
				
				this.url = "http://"+ipAddress+"/remote.php/dav/files/";
			}
			
			if (this.username != null)
			{
				this.url+=username+"/";
			}
			
			System.out.println(this.url);
			
			System.out.println(list(1));
			
			File f = new File("connectionTest");
			f.createNewFile();
			putFile("connectionTest", f.getAbsolutePath());
			deleteFile("connectionTest");
			f.delete();
			
	}
	
	public void putFile(String nameOnCloud, String localFilePath) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		
		InputStream	fis = new FileInputStream(new File(localFilePath));
		
		System.out.println(url+folder+nameOnCloud);
		System.out.println(url);
		
		sardine.put(url+folder+nameOnCloud, fis);		
	}
	
	
	public void deleteFile(String fileName) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);		
		sardine.delete(url+folder+fileName);
	}
	
	
	public boolean doesExist(String fileName)
	{
		boolean doesExist = false;
		
		Sardine sardine = SardineFactory.begin(username, password);
		try {
			doesExist = sardine.exists(url+folder+fileName);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
		return doesExist;
	}

	
	public File getFile(String fileName) throws IOException
	{
		Sardine sardine = SardineFactory.begin(username, password);
		InputStream is = sardine.get(url+folder+fileName);
		File file = new File(fileName);
		
		OutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[1024];
		int bytesRead;
		
		while ((bytesRead = is.read(buffer)) != -1) 
		{
			fos.write(buffer, 0, bytesRead);
		}

		fos.close();
		is.close();
		
		return file;
	}
	
	
	public List<DavResource> list(int depth) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		
		List<DavResource> resources = new ArrayList<DavResource>();
		
		resources = sardine.list(url, depth);

		return resources;
	}	
}