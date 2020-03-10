package fr.univ_smb.iut.rt.neyretch.cloud;

import java.io.IOException;
import java.util.*;

public class Split {

	private HashMap<Integer, BitSet> new_map = new HashMap<Integer, BitSet>();
	private int tab_length, hashmap_size, key, k, i, j, b;
	private BitSet tab;
	private byte[] bytes;

	public Split(HashMap<Integer, BitSet> map) throws IOException {
		hashmap_size = map.size();

		for (HashMap.Entry<Integer, BitSet> entry : map.entrySet()) {
			key = entry.getKey() ; 
			tab = map.get(key);
			tab_length = tab.length();
		}


		for (int i = 0; i < hashmap_size; i++)
		// Create a new hashmap with the specified number
		{
			new_map.put(i, new BitSet());

		}
		b=0;
		for (j = 0; j < tab_length; j++) {
			i = 0;
			k = 0;
			for (HashMap.Entry<Integer, BitSet> entry : map.entrySet()) {
				if (b > hashmap_size) {
					b = 0;
				}
				key = entry.getKey() + b; // on incremente pour repartir la parité
				
				if (key > hashmap_size) { // si on depasse la taille du hashmap on remet la valeur a 1
					key = 1 + i;
					i++;
				}
				tab = map.get(key);
				bytes = tab.toByteArray();
				System.out.println("la clé = " + key + ", valeur = " + tab + ", taille tableau = " + tab_length
						+ ", taille hashmap = " + hashmap_size + ", place attribuée : " + bytes + "\n");

				if (tab.get(j) == true) {
					new_map.get(k).set(k, true);

				} else {
					new_map.get(k).set(k, false);

				}

				k++;
			}
			b++;

		}

	}

	public HashMap<Integer, BitSet> getNew_map() {
		return new_map;
	}
}
