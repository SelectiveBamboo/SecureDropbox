import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.List;

import com.github.sardine.*;

public class SimpleCloud extends Cloud {
	
	
	//TODO 
	//Something with all these constructors !!!!
	public SimpleCloud(String url) 
	{
		this(url, null, null, null);
	}
	
	public SimpleCloud(InetAddress ipAddress) 
	{
		this(null, ipAddress, null, null);
	}
	
	public SimpleCloud(String url, InetAddress ipAddress, String username, String password ) 
	{
			this.ipAddress = ipAddress;
			this.url = url;

			this.username = username;
			this.password = password;
	}
	
	public void putFile(String nameOnCloud, String localFilePath) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		
		InputStream	fis = new FileInputStream(new File(localFilePath));
		sardine.put(url+nameOnCloud, fis);		
	}
	
	
	public void deleteFile(String fileName) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);		
		sardine.delete(url+fileName);
	}
	
	
	public boolean doesExist(String fileName)
	{
		boolean doesExist = false;
		
		Sardine sardine = SardineFactory.begin(username, password);
		try {
			doesExist = sardine.exists(url+fileName);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
		return doesExist;
	}

	
	public File getFile(String fileName) throws IOException, Exception
	{
		Sardine sardine = SardineFactory.begin(username, password);
		InputStream is = null;
		File file;
		
		if (path != null) 
		{
			is = sardine.get(url+path+fileName);
		}
		else
		{
			is = sardine.get(url+fileName);
		}
		
		if (is == null) 
		{
			throw new Exception();
		}
		
		file = new File(fileName);
		OutputStream fos = new FileOutputStream(file);

		byte[] buffer = new byte[8 * 1024];
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
		List<DavResource> resources = sardine.list(url, depth);

		return resources;
	}	
}