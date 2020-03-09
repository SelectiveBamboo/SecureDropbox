import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Scanner;

//import com.google.api.services.drive.Drive;

public class SecureDropbox {
	
	private static final String VERSION = "1.0";
	
	public static String regexFolder = "^/?([a-zA-Z_0-9]+/)+$";
	
	private static List<Cloud> clouds;
	
	static String path;
	static int cloudsNb = 0;

	public static void usage()
	{
		System.out.println("SecureDropbox v"+VERSION+"\n"
				+ "Usage:\n"
				+ "\n"
				+ "    SecureDropbox --clouds N --path PATH\n"
				+ "\n"
				+ "Options:\n"
				+ "\n"
				+ "    --path PATH : PATH to the directory which will be listened \n"
				+ "    --clouds N    : number of clouds you plan to use to replicate your datas. Must be at least 3.\n"
				+ "\n"
				+ "    --help      : display help.\n"
			);

		System.exit(1);
	}

	
	
	public static void main(String[] args)
	{	
		//String[] arg = {"--clouds", "3"}; For test purposes
		initialization(args);
		
		while(true)
		{
			EventListener eventCaptured;
			try {
				eventCaptured = new EventListener("/Users/hugomounier/Desktop/divers");
				
			    boolean state = eventCaptured.Listen();
			    
			    
			    System.out.println(eventCaptured.getActionOnFile());
			    System.out.println(eventCaptured.getNameOfFile());
			    System.out.println(eventCaptured.getPathOfFile());

			    
			    if(!state) { break; }
				
				//SecureDropboxHandling newThread = new SecureDropboxHandling(eventCaptured.getPathOfFile(), eventCaptured.getNameOfFile(), eventCaptured.getActionOnFile(), clouds);
			    //newThread.start();
				
				

				
			} catch (Exception e) {
				System.err.println("Listen init error !");
				e.printStackTrace();
			}
			
			
		}
		
	}

	
	
	private static void initialization(String[] args) 
	{		
		for (int i = 0; i < args.length; i++) 
		{
			String s = args[i];
			
			switch (s) 
			{
				case "--clouds":
					try {
						cloudsNb = Integer.parseInt(args[i+1]);
					} 
					catch (NumberFormatException e) 
					{
						System.err.println("ERROR: Argument of " + args[i] + " must be an integer.");
						System.exit(1);
					}
					if (cloudsNb < 3)
					{
						System.err.println("ERROR: You must provide at least 3 vDisk for RAID5 emulation.");
						System.exit(1);
					}
					
					break;
					
				case "--path":	
					File f = new File(args[i+1]);
					if (f.exists() && f.isDirectory()) 
					{
					   path = args[i+1];
					}
					else
					{
						System.err.println("ERROR: Wrong path provided.");
						System.exit(1);
					}
					
					break;
					
				case "--help":
					usage();
					
					break;
	
				default:
					break;
			}
		}
		
		for (int i = 1; i <= cloudsNb; i++) 
		{
			Scanner sc = new Scanner(System.in);
			System.out.println("Information about cloud n°" + i);
			
			System.out.print("What kind of cloud is it ? ");
			System.out.println("Type :\n"
					+ "'1' for Google Drive \n"
					+ "'2' for Nextcloud/Owncloud");
			
			String cloudType = sc.nextLine();
			
			switch (cloudType) 
			{
				case "1": //Google Drive
					initializeCloudGoogle(sc);
					break;

					
				case "2": //Nextcloud - Owncloud
					initializeSimpleCloud(sc);
					break;
			
					
				default:
					System.err.println("ERROR: Can not recognize input.");
					i--;
					
					break;
			}
			
			sc.close();
		}
	}
	
	
	private static void initializeCloudGoogle(Scanner sc)
	{
		String folder = null;
		
		boolean isFolderInquired =false;
		while ( !isFolderInquired ) 
		{
			System.out.println("\nWhat's the folder in which you would write ? \n"
					+ "Inquire the full path to this folder in linux style:   /../../../../\n");
			
			folder = sc.nextLine();
			
			if (folder.contentEquals("")) 
			{
				isFolderInquired = true;
			}
			else if(folder.matches(regexFolder))	//if match a folder path (linux like)
			{
				isFolderInquired = true;		
			}
		}
		
		try {
			CloudGoogleDrive cloud = new CloudGoogleDrive(folder);
			clouds.add(cloud);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.err.println("ERROR: when adding the cloud, operation aborted");
			System.exit(1);
		} 
		catch (FolderNameNotFoundException e) 
		{
			System.err.println("ERROR: folder name not found on the server, be sure it exists or create it");
			initializeCloudGoogle(sc);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Cloud added !");
	}
	
	private static void initializeSimpleCloud(Scanner sc)
	{
		System.out.print("\n\nIp address of the cloud (press enter if none): ");
		InetAddress ipAddress = null;
		String strIP;
	
		try {
			strIP = sc.nextLine();
			
			if (!strIP.equals("")) 
			{
				 ipAddress = InetAddress.getByAddress(strIP.getBytes());
			}
		} 
		catch (UnknownHostException e1) 
		{
			e1.printStackTrace();
			System.err.println("ERROR: with Ip address provided");
			
			initializeSimpleCloud(sc);
		}
		
		
		System.out.print("\n\nURL of the cloud (press enter if none): ");
		String url = sc.nextLine();
		if (url.equals("")) 
		{
			url = null;
		}
		
		String folder = null;
		System.out.print("\n\nFolder or path in which write in the cloud (press enter if none): ");
		String temp = sc.nextLine();
		if (temp.equals("") || temp.matches(regexFolder)) 
		{
			folder = temp;
		}
		
		System.out.print("\n\nUsername to acces the cloud (press enter if none): ");
		String username = sc.nextLine();
		if (username.equals("")) 
		{
			username = null;
		}
		
		System.out.print("\n\nPassword to access the cloud (press enter if none): ");
		String password = sc.nextLine();
		if (password.equals("")) 
		{
			password = null;
		}
		
		try {
			SimpleCloud cloud = new SimpleCloud(url, ipAddress, username, password, folder);
			clouds.add(cloud);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.err.println("ERROR: when adding the cloud, operation aborted");
			System.exit(1);
		}
		System.out.println("Cloud added !");
		
	}
}