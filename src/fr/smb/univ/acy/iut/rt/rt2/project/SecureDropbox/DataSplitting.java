package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;
/* The idea here is to get all the bits splitted and repartited through a specified number of BItset arrays. 
 * Then it returns a hashmap of all of these bitset arrays. HashMap<Integer, BitSet>
 * 
 * 
 * The last bit of the Bitset in parameter must be set, else the length() used isn't correct
 */
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class DataSplitting {

	private HashMap<Integer, BitSet> hmap = new HashMap<Integer, BitSet>();
	
	public DataSplitting(int nbArray, BitSet bitArray)
	{
		for (int i = 1; i <= nbArray; i++)		
			//Create a new hashmap with the specified number of arrays
		{
			hmap.put(i, new BitSet());
			hmap.get(i).set(0, true);		 //Needed to then write a bit in the bitset, 
												//cause we use the length argument -1 to access the bit to modify
		}
	
		int bitSetNumber;		//the bitset's number in the Hashmap
		int indexForModify;		//the index used in the bitsets in the Hashmap
		
        for (int i = 0; i < (bitArray.length() -1); i++)		// -1 cause the last bit isn't relevant (must be set to true since we're using .length()) 
        	//For each bit in the bitArray we copy its value to the corresponding array in hashmap
        {
        	bitSetNumber = i%nbArray +1;
        	indexForModify = hmap.get(bitSetNumber).length();
        	
            if (bitArray.get(i)) 
            {
            	hmap.get(bitSetNumber).set(indexForModify -1, true);		//Hence the importance of "hmap.get(i).set(0, true)"
            	hmap.get(bitSetNumber).set(indexForModify, true);           	
            }
            else
            {  	
            	hmap.get(bitSetNumber).set(indexForModify -1, false);
            	hmap.get(bitSetNumber).set(indexForModify, true);            	
            }
        }

        System.out.println("End generating hmap");
     }
	
	public HashMap<Integer, BitSet> getHmap() 
	{
		return hmap;
	}

    public static void main(String args[]) {
        BitSet bits1 = new BitSet();
        Random rand = new Random();
       
        int i;
        for ( i = 0; i < 98; i++) 
        {
			if ( (rand.nextInt(50)) >= 25 ) 
			{
				bits1.set(i);
			}			
		}
        bits1.set(i);
        
        System.out.println("End generating bits");
        System.out.println(bits1);
		
        DataSplitting splittedData = new DataSplitting(10, bits1);
		DataReformation reformatedData = new DataReformation(splittedData.getHmap());
		System.out.println(splittedData.getHmap());
		System.out.println(reformatedData.getReassembledBitArray());
		
		bits1.set(i, false);
		System.out.println(reformatedData.getReassembledBitArray().equals(bits1));
		
    }
}
