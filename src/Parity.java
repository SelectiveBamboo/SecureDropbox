


import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;


public class Parity 
{
	
	int hashmap_size;
	
	private BitSet bs1 = new BitSet();
	private BitSet bs2 = new BitSet();
	
	private HashMap<Integer , BitSet> hash2 = new HashMap<Integer, BitSet>();

		
	public Parity (HashMap<Integer, BitSet> hash) throws IOException
	
	{
		

		
		hashmap_size = hash.size();
		int i;
		
		
		
		bs1 = hash.get(1).get(0, hash.get(1).length()-1); //Ici obligation de préciser la range de sélection sinon bs1 est écrasé pendant l'opération et on perd le BitSet à l'instance 1
		
	
		
		for (i = 2; i <= hashmap_size; i++) 
		{
			bs2 = hash.get(i);				//Récupération du bitset à la valeur i
			bs1.xor(bs2);					//Calcul du xor 

			
			
		}


		
		hash.put(hashmap_size+1, bs1);		//Insertion de la parité sous forme de BitSet à la dernière ligne


		this.hash2 = hash;
					
	}
	
	
	
	public BitSet getBsParity() 
	{
		return hash2.get(hashmap_size);
		
	}
	
	public HashMap<Integer, BitSet> getHash() {
		return hash2;
		
	}
	


}
