import java.io.File;

public class SecureDropbox {
	
	private static final String VERSION = "1.0";

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
		int cloudsNb = 0;
		String path;
		
		for (int i = 0; i < args.length; i++) 
		{
			String s = args[i];
			
			switch (s) 
			{
				case "--clouds":
					try {
						cloudsNb = Integer.parseInt(args[i+1]);
					} catch (NumberFormatException e) {
						System.err.println("Argument of " + args[i] + " must be an integer.");
						System.exit(1);
					}
					if (cloudsNb < 3)
					{
						System.err.println("You must provide at least 3 vDisk for RAID5 emulation.");
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
						System.err.println("Wrong path provided.");
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
		
		
	}

}
