package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;

public class FileInfos {

	private String pathOnLocal;
	private String nameOnLocal;
	private String nameOnCloud;	
	
	public FileInfos() 
	{
		this.pathOnLocal = null;
		this.nameOnLocal = null;
		this.nameOnCloud = null;
	}
	
	public FileInfos(String pathOnLocal, String nameOnLocal, String nameOnCloud) 
	{
		this.pathOnLocal = pathOnLocal;
		this.nameOnLocal = nameOnLocal;
		this.nameOnCloud = nameOnCloud;
	}
	
	public String getPathOnLocal() 
	{
		return pathOnLocal;
	}
	public void setPathOnLocal(String pathOnLocal) 
	{
		this.pathOnLocal = pathOnLocal;
	}
	public String getNameOnLocal() {
		return nameOnLocal;
	}
	public void setNameOnLocal(String nameOnLocal) 
	{
		this.nameOnLocal = nameOnLocal;
	}
	public String getNameOnCloud() 
	{
		return nameOnCloud;
	}
	public void setNameOnCloud(String nameOnCloud) 
	{
		this.nameOnCloud = nameOnCloud;
	}
	
	
}
