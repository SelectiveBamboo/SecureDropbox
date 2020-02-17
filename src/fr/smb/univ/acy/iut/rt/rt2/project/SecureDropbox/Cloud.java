package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;
import java.net.InetAddress;



public abstract class Cloud {

	protected InetAddress ipAddress;
	protected String url;		//Generic URL to reach the cloud, like www.example.com 
	protected String folder;			//with eventually the directory in which write

	protected String username;
	protected String password;

	
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(InetAddress ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
