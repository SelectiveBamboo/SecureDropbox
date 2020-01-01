
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class DataSplitting {

HashMap<Integer, BitSet> hmap = new HashMap<Integer, BitSet>();
	
	public DataSplitting(int nbArray, BitSet bitArray)
	{
		for (int i = 1; i <= nbArray; i++)		
			//Create a new hashmap with the specified number of arrays
		{
			hmap.put(i, new BitSet());
			hmap.get(i).set(0, true);		 //Needed to then write a bit in the bitset, 
												//cause we use the length argument -1 to access the bit to modify
		}
	
        for (int i = 0; i < bitArray.length(); i++)		
        	//For each bit in the bitArray we copy its value to the corresponding array in hashmap
        {
        	int bitSetNumber = i%nbArray +1;
        	int indexForModify = hmap.get(bitSetNumber).length();
        	
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

    public static void main(String args[]) {
        BitSet bits1 = new BitSet();
        Random rand = new Random();
       
        for (int i = 0; i < 500000000; i++) 
        {
			if ( (rand.nextInt(50)) >= 25 ) 
			{
				bits1.set(i);
			}
			
		}
        
        System.out.println("End generating bits");
		new DataSplitting(8, bits1);
    }
}