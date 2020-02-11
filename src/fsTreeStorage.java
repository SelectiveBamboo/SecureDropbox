
public class fsTreeStorage {
	
	public static String genRandomString(int stringLength) 
	{		
		String charc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String str = "";
		
		for (int i = 0; i < stringLength; i++) 
		{
			int r = (int) Math.floor(Math.random() * 62); // multiplied by the number of char in string charc
			str += charc.charAt(r);
		}
		return str;
	}
	
	

}
