import java.util.BitSet;
import java.util.HashMap;

public class DataReformation {
	
	BitSet reassembledBitArray = new BitSet();
	
	public DataReformation(HashMap<Integer, BitSet> hmap) 
	{
		int nbArray = hmap.size();
		
		int indexBitArray = 0;
		
		for (int i = 0; i < (hmap.get(1).length() -1) ; i++) 
		{
			for (int j = 1; j <= nbArray; j++) 
			{
				if (hmap.get(j).get(i)) 
				{
					reassembledBitArray.set(indexBitArray);
				}
				
				indexBitArray++;
			}
		}	
		
		int lastIndex = 0;
		int toHandleArray = nbArray;
		
		for (int i = hmap.size(); i >= 1; i--) 
		{
			if (hmap.get(i).length() > lastIndex)
			{
				lastIndex = hmap.get(i).length() -1;
				toHandleArray = i;
			}
		}
		
		for (int i = 1; i <= toHandleArray; i++) 
		{
			if (hmap.get(i).get(lastIndex)) 
			{
				reassembledBitArray.set(lastIndex + i -1);
			}
		}
	}
}
