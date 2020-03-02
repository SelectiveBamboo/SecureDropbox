

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;

public class DataRecons {
	
	/* -- Data Reconstruction */
	
	public static HashMap<Integer, BitSet> Reconstruct(HashMap<Integer, BitSet> hmap) {

		for(int i=0;i<hmap.size();i++){
			
			if(hmap.get(i) == null) {
				
				/* Si on a un serveur de de down */
				/* On reconstruit la chaine avec les autres + bit de partiÈ */
				
				int sizeRef = (i==0) ? 1 : i-1;
				int size = hmap.get(sizeRef).toByteArray().length;
				hmap = Rebuild(hmap, size, i);
				break;
				
			}
		}
		return hmap;
	}

	/* -- Others -- */
	
	public static void DisplayMap(HashMap<Integer, BitSet> map) {
		
		for(int i=0;i<map.size();i++) {
			
			if(map.get(i) != null) {
				byte[] data = map.get(i).toByteArray();
				for(int j=0;j<data.length;j++) {
					
					if(data.length > 4) {
						System.out.print(String.format("%02x", data[j]) + " ");
						if(j%32==0 && j!=0) {
							System.out.println();
						}
					}else {
						System.out.print(ByteToBinaryString(data[j]) + " (0x" + String.format("%02x", data[j]) + ") ");
					}
				}
			}else {
				System.out.print("null ");
			}
			System.out.println();
		}
	}
	private static HashMap<Integer, BitSet> Rebuild(HashMap<Integer, BitSet> map, int size, int index) {

		byte[] reconst = new byte[size];
		
		// pour chaques byte !
		for(int i=0;i<size;i++) {
			
			byte op = 0x00;
			for(int j=0;j<map.size();j++) {
			
				if(j != index) {
					op ^= map.get(j).toByteArray()[i];
				}
			}
			// on ajoute le byte dans les donnÈs !
			reconst[i] = op;
		}
		map.put(index, BitSet.valueOf(reconst));
		return map;
	}
	public static BitSet fromString(String binary) {
	    BitSet bitset = new BitSet(binary.length());
	    for (int i = 0; i < binary.length(); i++) {
	        if (binary.charAt(i) == '1') {
	            bitset.set(binary.length()-1-i);
	        }
	    }
	    return bitset;
	}
	private static String ByteToBinaryString(byte buff) {
		String s1 = String.format("%8s", Integer.toBinaryString(buff & 0xFF)).replace(' ', '0');
		return s1;
	}
	public static int checkIntegrity(HashMap<Integer, BitSet> map) {
		for(int i=0;i<map.size();i++){
			if(map.get(i) == null) {
				
				/* Si on a un serveur de de down */
				/* On retourn l'index */
				return i;
			}
		}
		
		return -1;
	}
}
