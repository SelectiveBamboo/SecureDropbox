import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.api.services.drive.Drive;

public class SecureDropbox {
	
	private static final String VERSION = "1.0";
	
	private static String regexFolder = "^/?([a-zA-Z_0-9]+/)+$";
	private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";
	
	private static List<Cloud> clouds = new ArrayList<Cloud>();
	
	private static String path = "";
	private static int cloudsNb = 0;
	
	private static String config_file = "./config.json";
	
	private static JSONArray config = new JSONArray();

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
				+ "    --config FILE : path to the ocnfiguration file. Default: "+config_file+"\n"
				+ "\n"
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
				case "--config":	
					File f1 = new File(args[i+1]);
					if (f1.exists()) 
					{
						config_file = new String(args[i+1]);
					}
					else
					{
						System.err.println("ERROR: configuration file '"+args[i+1]+"' not found.");
					}
					
					break;
			
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
		
		boolean initResult = true;
		File configFile = new File(config_file);
		
		if (configFile.exists()) 
		{
			initResult = initializeUsingConfigfile();
		}
		
		if (initResult) 
		{
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
					
			if (!configFile.exists()) 
			{
				try {
					configFile.createNewFile();
				} 
				catch (IOException e) {e.printStackTrace();}
			}
			
			FileWriter fw = null;	
			
			try {
				fw = new FileWriter(configFile);
				fw.write(config.toJSONString());
			} 
			catch (IOException e) { e.printStackTrace();}

			finally {
				try {
					fw.flush();
					fw.close();
				} 
				catch (IOException e) { e.printStackTrace();}
			}
		}
	}
	
	private static boolean initializeUsingConfigfile()
	{
		boolean bool = false;

		// Read configuration file
		try {
			JSONParser parser = new JSONParser();
			JSONArray config = (JSONArray) parser.parse(
					new FileReader(config_file)
					);
			// We must have a minimum of three clouds defined for RAID 5
			if (cloudsNb < 3)
			{
				System.err.println("ERROR: you must defined at least 3 clouds for RAID 5.");
				bool = true;
			}
			else
			{
				for (Object o : config)
				{
					JSONObject cloudconfig = (JSONObject) o;

					String strName = (String) cloudconfig.get("name");
					String strType = (String) cloudconfig.get("type");
					String strIp = (String) cloudconfig.get("ipAddress");
					String strUrl = (String) cloudconfig.get("url");
					String strFolder = (String) cloudconfig.get("url");
					String strUser = (String) cloudconfig.get("username");
					String strPwd = (String) cloudconfig.get("password");

					System.out.println("Name:" + strName);
					System.out.println("Type:" + strType);
					System.out.println("Ip address:" + strIp);
					System.out.println("Url:" + strUrl);
					System.out.println("Folder:" + strFolder);
					System.out.println("Username:" + strUser);
					System.out.println("Password:" + strPwd);
					System.out.println();

					switch (strType) 
					{
					case "google": //Google Drive
						addCloudGoogle(strFolder);
						break;

					case "nextcloud": //Nextcloud - Owncloud
					case "owncloud":
						addSimpleCloud(strIp, strUrl, strFolder, strUser, strPwd);
						break;

					default:
						System.err.println("ERROR: cloud type '"+strType+"' not supported.");
						bool = true;
						break;
					}
				}
			}
		} 
		catch (IOException | ParseException e) 
		{
			e.printStackTrace();
			bool = true;		
		}
		
		return bool;
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
		
		addCloudGoogle(folder);
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
		
		addSimpleCloud(ipAddress, url, folder, username, password);
		
		JSONObject obj = new JSONObject();
		obj.put("type", "nextcloud");
		obj.put("url", url);
		obj.put("ipAddress", ipAddress);
		obj.put("username", username);
		obj.put("password", password);
		obj.put("folder", folder);

		config.add(obj);
	}
	
	private static void addCloudGoogle(String folder) 
	{
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
			
			Scanner sc = new Scanner(System.in);
			initializeCloudGoogle(sc);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Cloud added !");
	}
	
	private static void addSimpleCloud(String ipAddress, String url, String folder, String username,
			String password)
	{
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
			
			Scanner sc = new Scanner(System.in);
			initializeSimpleCloud(sc);
		}
	}
}