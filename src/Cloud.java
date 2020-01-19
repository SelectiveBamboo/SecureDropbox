import java.net.InetAddress;


public class Cloud {

	protected InetAddress ipAddress;
	protected String url;		//Generic URL to reach the cloud, like www.example.com 
									//with eventually the directory in which write
									//have to end with an '/'
	protected String username;
	protected String password;
	
	/*
	 * public Cloud(InetAddress ipAddress) { this(null, ipAddress, null, null); }
	 * 
	 * public Cloud(String url) { this(url, null, null, null); }
	 * 
	 * public Cloud(String url, String username, String password) { this(url, null,
	 * username, password); }
	 * 
	 * public Cloud(InetAddress ipAdress, String username, String password) {
	 * this(null, ipAdress, username, password); }
	 */
	
	public Cloud(String url, InetAddress ipAddress, String username, String password ) 
	{
		
		//TODO 
		//Add a test for URL with an exception throw
		this.url = url;
		
		this.ipAddress = ipAddress;
		this.username = username;
		this.password = password;
	}
	
	
	
}
