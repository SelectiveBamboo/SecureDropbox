import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.List;

import com.github.sardine.*;

public class SimpleCloud extends Cloud {
	
	public SimpleCloud(InetAddress ipAddress, String username, String password )
	{
		super(null, ipAddress, username, password);
	}
	
	public SimpleCloud(String url)
	{
		super(url, null, null, null);
	}

	public SimpleCloud(InetAddress ipAddress)
	{
		super(null, ipAddress, null, null);
	}
	
	public SimpleCloud(String url, String username, String password )
	{
		super(url, null, username, password);
	}
	
	
	public void putFile(String nameOnCloud, String localFilePath) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		
		InputStream	fis = new FileInputStream(new File(localFilePath));
		sardine.put(url+nameOnCloud, fis);		
	}
	
	
	public void deleteFile(String nameOnCloud) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);		
		sardine.delete(url+nameOnCloud);
	}
	
	
	public boolean doesExist(String nameOnCloud)
	{
		boolean doesExist = false;
		
		Sardine sardine = SardineFactory.begin(username, password);
		try {
			doesExist = sardine.exists(url+nameOnCloud);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
		return doesExist;
	}

	
	public InputStream getFile(String nameOnCloud) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		
		InputStream is = sardine.get(url+nameOnCloud);
		
		return is;
	}
	
	
	public List<DavResource> list(int depth) throws IOException 
	{
		Sardine sardine = SardineFactory.begin(username, password);
		List<DavResource> resources = sardine.list(url, depth);

		return resources;
	}	
}