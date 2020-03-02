
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class Main {

	public static void main(String[] args) {
		
		HashMap<Integer, BitSet> hmap = new HashMap<Integer, BitSet>();
		/* On as 4 serveurs, l'un d'eux sert de paritÈ */
		hmap.put(0, DataRecons.fromString("000111110011"));
		hmap.put(1, null);
		hmap.put(2, DataRecons.fromString("110010101001"));
		
		// Si il y a un serveur de down (null)
		if(DataRecons.checkIntegrity(hmap)!=-1) {
			// on reconstruit tout
			hmap = DataRecons.Reconstruct(hmap);
		}
		
		//Data.DisplayMap(hmap);
		System.out.println(Arrays.toString(hmap.get(1).toByteArray()));
	}

}

