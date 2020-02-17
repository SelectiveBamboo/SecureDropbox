package fr.smb.univ.acy.iut.rt.rt2.project.SecureDropbox;
/*
 * Here we're doing the exact opposite of DataSplitting. Having in parameter an HashMap <Integer, BitSet>
 * and returning a bitset array with all the bits replaced as they were before DataSplitting.
 * 
 */

import java.util.BitSet;
import java.util.HashMap;

public class DataReformation {
	
	private BitSet reassembledBitArray = new BitSet();
	
	public DataReformation(HashMap<Integer, BitSet> hmap) 
	{
		int nbLastArray = hmap.size();		//number of the last bitset array in the hashmap (and of bitsets also)
		
		int indexBitArray = 0;		//index in the returned bitset array
		
		for (int i = 0; i < (hmap.get(1).length() -2) ; i++) 
			/*iterate for bit index in all bitset array in the hashmap
			 * 
			 * Until length() -2 cause last bit is not a data to keep (set to 1 if the bit before is relevant)
			 * and because the before last is handled in another way after this loop since it is not always relevant
			 */
		{
			for (int j = 1; j <= nbLastArray; j++) 
			{
				if (hmap.get(j).get(i)) 
				{
					reassembledBitArray.set(indexBitArray);
				}
				
				indexBitArray++;
			}
		}	
		
		boolean isIndexRelevant = true;
		int j = 1;
		
		do		//We iterate until we found the last relevant bit or until we reach the end of the hashmap
		{
			if (hmap.get(j).get(hmap.get(1).length() -2)) 
			{
				reassembledBitArray.set(indexBitArray);
			}
			
			j++;
			
			if (j <= nbLastArray) 
			{
				if (hmap.get(j).length() != hmap.get(1).length())
				{
					isIndexRelevant = false;
				}
				
				indexBitArray++;
			}
			else
			{
				isIndexRelevant = false;
			}
		}
		while(isIndexRelevant);
	}

	
	public BitSet getReassembledBitArray() 
	{
		return reassembledBitArray;
	}
}
