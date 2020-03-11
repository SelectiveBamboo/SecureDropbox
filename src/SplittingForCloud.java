import java.util.BitSet;
import java.util.HashMap;

public class SplittingForCloud {
	
	HashMap<Integer, BitSet> reorganisedHmap = new HashMap<Integer, BitSet>();

	
	public SplittingForCloud(HashMap<Integer, BitSet> hmap) 
	{
		hmap = ShiftMapByOne(hmap);
		
		int j = 0;
		int bitArray_length = hmap.get(0).length();
		int Hsize = hmap.size();
		
		for (int a = 0; a < Hsize; a++) 	//Set the Reorganised hashmap with the good size and bitset inside
		{
			reorganisedHmap.put(a, new BitSet());
		}
		
		for (int k = 0; k < bitArray_length; k++) 
		{
			for (int i = j; i < Hsize; i++) 
			{
				boolean bit = hmap.get(i).get(k);
				
				if(bit)
				{
					reorganisedHmap.get(i-j).set(k, true);
				}
				else
				{
					reorganisedHmap.get(i-j).set(k, false);
				}
			}
			
			for (int i = 0; i < j; i++) 
			{
				boolean bit = hmap.get(i).get(k);
				
				if(bit)
				{
					reorganisedHmap.get((i+j)%Hsize).set(k, true);
				}
				else
				{
					reorganisedHmap.get((i+j)%Hsize).set(k, false);
				}
			}
			
			j= (j+1)%Hsize;
		}
	}

	private HashMap<Integer, BitSet> ShiftMapByOne(HashMap<Integer, BitSet> hmap) 
	{
		HashMap<Integer, BitSet> shiftedHmap = new HashMap<Integer, BitSet>();

		for (int i = 1; i <= hmap.size(); i++) 
		{
			shiftedHmap.put(i-1, hmap.get(i));
		}
		
		return shiftedHmap;
	}

	public HashMap<Integer, BitSet> getreorganisedHmap() {
		return reorganisedHmap;
	}
}
