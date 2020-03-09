import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.google.api.services.drive.Drive;

public class SecureDropbox {
	
	private static final String VERSION = "1.0";
	
	private static String regexFolder = "^/?([a-zA-Z_0-9]+/)+$";
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	
	private static List<Cloud> clouds = new ArrayList<Cloud>();
	
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
		String[] arg = {"--clouds", "3", "--path", "/home/jules/notes"}; //For test purposes
		initialization(arg);
		
		while(true)
		{
			Listen eventCaptured = new Listen("/home/jules/notes");
					
			System.out.println(eventCaptured.getPathOfFile());
			System.out.println(eventCaptured.getNameOfFile());
			System.out.println(eventCaptured.getActionOnFile());
			
			SecureDropboxHandling newThread = new SecureDropboxHandling(eventCaptured.getNameOfFile(), eventCaptured.getPathOfFile()+"/", eventCaptured.getActionOnFile(), clouds);
			newThread.start();
		}
		
	}

	
	private static void initialization(String[] args) 
	{		
	
		Scanner sc = new Scanner(System.in);
		
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
		}
	}
	
	
	private static void initializeCloudGoogle(Scanner sc)
	{
		String folder = null;
		
		boolean isFolderInquired =false;
		while ( !isFolderInquired ) 
		{
			System.out.println("\nWhat's the folder in which you would write ? \n"
					+ "Inquire the full path to this folder in linux style: /../../../ (press enter if none):");
			
			folder =sc.nextLine();
			
			if (folder.equals("")) 
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
		
		String ipAddress = null;
		String strIP;
		boolean isIPInquired = false;
		
		while( !isIPInquired)
		{
			System.out.print("\n\nIp address of the cloud (press enter if none): ");
			strIP = sc.nextLine();
			
			if ( strIP.matches(ipv4Pattern) || strIP.matches(ipv6Pattern)) 
			{
				ipAddress = strIP;
				isIPInquired = true;
			}
			else if (strIP.equals("")) 
			{
				isIPInquired = true;
			}
		}
		
		
		System.out.print("\n\nURL of the cloud (press enter if none): ");
		String url = sc.nextLine();
		if (url.equals("")) 
		{
			url = null;
		}
		
		
		String folder = null;
		while(folder == null)
		{
			System.out.println("\nWhat's the folder in which you would write ? \n"
					+ "Inquire the full path to this folder in linux style: /../../../  (press enter if none): \n");
			String temp = sc.nextLine();
			if (temp.equals("") || temp.matches(regexFolder)) 
			{
				folder = temp;
			}		
		}
		
		System.out.print("\n\nUsername to acces to the cloud (press enter if none): ");
		String username = sc.nextLine();
		if (username.equals("")) 
		{
			username = null;
		}
		
		System.out.print("\n\nPassword to access to the cloud (press enter if none): ");
		String password = sc.nextLine();
		if (password.equals("")) 
		{
			password = null;
		}
		
		if (url != null || ipAddress != null)
		{
			try {
				SimpleCloud cloud = new SimpleCloud(url, ipAddress, username, password, folder);
				clouds.add(cloud);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.err.println("ERROR: unable to add the cloud, operation aborted");
				System.exit(1);
			}
			System.out.println("Cloud added !");
		}
		else
		{
			System.out.println("You can't have blank IpAddres AND blank url, another chance is given to you");
			initializeSimpleCloud(sc);
		}
	}
}