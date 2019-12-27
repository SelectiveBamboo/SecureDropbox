
import java.util.BitSet;
import java.util.HashMap;
import java.util.Random;

public class DataSplitting {

HashMap<Integer, BitSet> hmap = new HashMap<Integer, BitSet>();
	
	public DataSplitting(int nbArray, BitSet bitArray)
	{
		for (int i = 1; i <= nbArray; i++) 
		{
			hmap.put(i, new BitSet());
			hmap.get(i).set(0, true);
		}
	
        for (int i = 0; i < bitArray.length(); i++)
        {
        	int bitSetNumber = i%nbArray+1;
        	int indexForModify = hmap.get(bitSetNumber).length();
        	
            if (bitArray.get(i)) 
            {
            	hmap.get(bitSetNumber).set(indexForModify -1, true);
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