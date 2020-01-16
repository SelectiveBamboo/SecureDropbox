import java.net.InetAddress;


public class Cloud {

	private InetAddress ipAddress;
	private String url;
	private String username;
	private String password;
	
	public Cloud(InetAddress ipAddress) 
	{
		this(null, ipAddress, null, null);
	}
	
	public Cloud(String url) 
	{
		this(url, null, null, null);
	}
	
	public Cloud(String url, String username, String password) 
	{
		this(url, null, username, password);
	}
	
	public Cloud(InetAddress ipAdress, String username, String password) 
	{
		this(null, ipAdress, username, password);
	}
	
	public Cloud(String url, InetAddress ipAddress, String username, String password ) 
	{
		this.url = url;
		this.ipAddress = ipAddress;
		this.username = username;
		this.password = password;
	}
	
	
	
}
