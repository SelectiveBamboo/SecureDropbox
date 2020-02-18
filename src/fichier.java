

import java.util.*;
import java.util.Map.Entry;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.io.FileReader;

@SuppressWarnings({})

public class fichier {

	private int hashmap_size, i;

	@SuppressWarnings("unused")
	public fichier(HashMap<Integer, BitSet> new_map) throws IOException {
		hashmap_size = new_map.size();
		String charc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String nom = "";
		for (int x = 0; x < 8; x++) {
			int i = (int) Math.floor(Math.random() * 62); // /!\ Si tu supprimes des lettres tu diminues ce nb
			nom += charc.charAt(i);
		}
		for (i = 0; i < hashmap_size; i++) {

			String nom2=nom + "_" + i;
			OutputStream outputstream = new FileOutputStream(nom2);
			System.out.println(nom2);

			for (HashMap.Entry<Integer, BitSet> entry : new_map.entrySet()) {
				byte[] array = new_map.get(i).toByteArray();
				outputstream.write(array);
				outputstream.flush();

			}
			outputstream.close();

		}

	}

}
