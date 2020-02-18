
import java.io.IOException;
import java.util.*;

public class Split {

	private HashMap<Integer, BitSet> new_map = new HashMap<Integer, BitSet>();
	private int tab_length, hashmap_size, key, k, i, j, bit;
	private BitSet tab;
	private byte[] bytes;

	public Split(HashMap<Integer, BitSet> map) throws IOException {

		hashmap_size = map.size();

		for (int i = 0; i < hashmap_size; i++)
		// Create a new hashmap with the specified number
		{
			new_map.put(i, new BitSet());
		}

		for (j = 0; j < hashmap_size; j++) {
			i = 0;
			k = 0;
			for (HashMap.Entry<Integer, BitSet> entry : map.entrySet()) {
				key = entry.getKey() + j; // on incremente pour repartir la parité
				if (key > hashmap_size) { // si on depasse la taille du hashmap on remet la valeur a 1
					key = 1 + i;
					i++;
				}
				tab = map.get(key);
				tab_length = tab.length();
				bytes = tab.toByteArray();

				if (tab.get(j) == true) {
					bit = 1;
					new_map.get(j).set(k, true);

				} else {
					bit = 0;
					new_map.get(j).set(k, false);

				}

				k++;
			}

		}

	}

	public HashMap<Integer, BitSet> getNew_map() {
		return new_map;
	}
}
