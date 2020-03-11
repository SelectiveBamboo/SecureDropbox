
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class SplitInFiles {
	
	private HashMap<Integer, BitSet> new_map = new HashMap<Integer, BitSet>();
	private int hashmap_size, key, k, i, j, tab_length;
	private BitSet tab;
	private List<File> generatedFiles = new ArrayList<File>();

	public SplitInFiles(HashMap<Integer, BitSet> map) throws IOException 
	{
		hashmap_size = map.size();
		
		for(HashMap.Entry<Integer, BitSet> entry : map.entrySet())
		{
			key = entry.getKey();
			tab = map.get(key);
			tab_length = tab.length();
		}
		
		for (int i = 0; i < hashmap_size; i++)
		// Create a new hashmap with the same size as the one in parameter
		{
			new_map.put(i, new BitSet());
		}

		int b = -1;
		for (j = 0; j < tab_length; j++) {
			i = 0;
			k = 0;
			b++;
			for (HashMap.Entry<Integer, BitSet> entry : map.entrySet()) {
				if (b > hashmap_size) 
				{
					b=0;
				}
				key = entry.getKey() + b; // on incremente pour repartir la parit�
				if (key > hashmap_size) { // si on depasse la taille du hashmap on remet la valeur a 1
					key = 1 + i;
					i++;
				}
				tab = map.get(key);
				
				System.out.println(j);

				if (tab.get(j) == true) 
				{
					new_map.get(k).set(k, true);
				} 
				else 
				{
					new_map.get(k).set(k, false);
				}
				k++;
			}
		}		
		fichier(new_map);
	}

	public HashMap<Integer, BitSet> getNew_map() 
	{
		return new_map;
	}

	public void fichier(HashMap<Integer, BitSet> new_map) throws IOException  
	{
		hashmap_size = new_map.size();
		String charc = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String nom = "";
		for (int x = 0; x < 8; x++) {
			int i = (int) Math.floor(Math.random() * 62); // /!\ Si tu supprimes des lettres tu diminues ce nb
			nom += charc.charAt(i);
		}
		
		for (i = 0; i < hashmap_size; i++) {

			String nom2=nom + "_" + i;
			
			File f = new File(nom2);
			f.createNewFile();
			
			FileOutputStream outputstream = new FileOutputStream(f);
			
			System.out.println("map numero " + i + "\n"+ new_map.get(i));
			
			byte[] array = new_map.get(i).toByteArray();
			
			System.out.println("array ength in splitfile" + array.length);
			outputstream.write(array);
			outputstream.flush();

			outputstream.close();
			
			generatedFiles.add(f);
		}
	}
	
	public List<File> getGeneratedFiles()
	{
		return generatedFiles;
	}


}
