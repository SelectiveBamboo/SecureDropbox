import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;
import java.io.*;
import java.math.BigInteger;

class SecureDropboxTest
{

	private static final int BUFFER_SIZE = 1;
	private static final String VERSION = "1.0";

	public static void usage()
	{
		System.out.println("SecureDropbox v"+VERSION+"\n"
				+ "Usage:\n"
				+ "\n"
				+ "    SecureDropbox --size N --file FILE\n"
				+ "\n"
				+ "Options:\n"
				+ "\n"
				+ "    --file FILE : file to copy over the RAID5 vDisk array.\n"
				+ "    --nb N    : number of vDisk in the RAID5 array. Must be 3 at least.\n"
				+ "\n"
				+ "    --help      : display this message.\n"
			);

		System.exit(1);
	}

	public static void main(String args[])
	{
		BitSet bits1 = new BitSet();
		File filein = null;
		int cloudsNb = 3;
		long start = System.currentTimeMillis();


		// Parse command line argument
		// --nb N : specify the number of clouds used
		for (int i = 0; i < args.length; i++) 
		{			
			if (args[i].equals("--nb"))
			{
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
			}
			else if (args[i].equals("--file"))
			{
				try {
					filein = new File(args[i+1]);
					if (!filein.exists()) 
					{
						System.out.println("Input file "+filein.toString()+" doesn't exists.");
						System.exit(1);
 					}
				} catch (NumberFormatException e) {
					System.err.println("Argument of " + args[i] + " must be a file.");
					System.exit(1);
				}
			}
			else if (args[i].equals("--cloud"))
			{
				
			}
			else if (args[i].equals("--help"))
			{
				usage();
			}
		}

		// a file must be provided
		if (filein == null)
		{
			System.out.println("You must use option --file to specify a file to read");
			System.exit(1);
		}

		System.out.println("Load file content into memory");
		try (
			InputStream inputStream = new FileInputStream(filein.toString());
			OutputStream outputStream = new FileOutputStream(filein.toString()+".new");
		) {
			byte[] buffer = new byte[BUFFER_SIZE];
			String text = null;

			while (inputStream.read(buffer) != -1) 
			{	
				text += new String(buffer);
				System.out.println("File content: "+text);

				bits1 = BitSet.valueOf(new BigInteger(text.getBytes()).toByteArray());
			}
		} 
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
			
		DataSplitting splittedData = new DataSplitting(cloudsNb, bits1);
		
		DataReformation reformatedData = new DataReformation(splittedData.getHmap());
		
		bits1.set( bits1.length()-1, false);
		if (reformatedData.getReassembledBitArray().equals(bits1)) 
		{
			System.out.println("Functions DataReformation and DataSplitting are still working well together");
		}
		else
		{
			System.err.println("Problem somewhere with DataReformation and DataSplitting");
		}
		

		long end = System.currentTimeMillis();
		System.out.println("Program took: " + (end - start) + " ms");
	}
}