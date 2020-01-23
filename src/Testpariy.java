

import java.io.IOException;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Scanner;




public class Testpariy {
	
	public static void main (String[] args) throws IOException {
		
		/*
		BitSet bs1 = new BitSet();
		BitSet bs2 = new BitSet();
		BitSet bs3 = new BitSet();
		*/
		// ------------------------------------------------------
		// TEST AVEC ENTREES TEST 
		
		/*
			
		System.out.println("Premier test : avec entrées Client =)");
		System.out.println("		----------------------");

		
		Scanner sc11 = new java.util.Scanner(System.in);
		Scanner sc12 = new java.util.Scanner(System.in);

		Scanner sc21 = new java.util.Scanner(System.in);
		Scanner sc22 = new java.util.Scanner(System.in);

		Scanner sc31 = new java.util.Scanner(System.in);
		Scanner sc32 = new java.util.Scanner(System.in);

		
		Scanner sc41 = new java.util.Scanner(System.in);
		Scanner sc42 = new java.util.Scanner(System.in);
		
		
		BitSet bs1 = new BitSet(5);
		
		System.out.println("Saisir l'intervalle du bitset 1 ");
		int i11 = sc11.nextInt();
		int i12 = sc12.nextInt();
		
		bs1.set(i11,i12);
		
		BitSet bs2 = new BitSet(5);
		
		System.out.println("Saisir l'intervalle du bitset 2 ");
		int i21 = sc21.nextInt();
		int i22 = sc22.nextInt();

		bs2.set(i21,i22);
		
		BitSet bs3 = new BitSet(5);

		System.out.println("Saisir l'intervalle du bitset 3 ");
		int i31 = sc31.nextInt();
		int i32 = sc32.nextInt();
		
		bs3.set(i31,i32);
		
		
		BitSet bs4 = new BitSet(5);
		
		
		System.out.println("Saisir l'intervalle du bitset 4");
		int i41 = sc41.nextInt();
		int i42 = sc42.nextInt();

		bs4.set(i41,i42);
		

		
		HashMap<Integer,BitSet> test = new HashMap<Integer, BitSet>();
		
		test.put(1, bs1);
		test.put(2, bs2);
		test.put(3, bs3);
		test.put(4, bs4);
		
		System.out.println(" bs1 = " + bs1 + " bs2 = " + bs2 +"bs3 = "+ bs3+ "bs4 = "+bs4);
		System.out.println("hashmap test is : "+test);
		
		new Parity(test);
		
		
		*/
		// --------------------------------------------
		
		
		// Test of correspondence =)
		// Here bit is bs1(2,5) bs2(3,11) bs3(5,9) and parity is : {2, 4, 9, 10}
		// BitSet verif already exist, and we will compare if it matches with the test here.
		
		
		
		BitSet verif = new BitSet();
		
		verif.set(2);
		verif.set(4);
		verif.set(9);
		verif.set(10);
		
		System.out.println ("Parité type : "+ verif);

		
		BitSet bst1 = new BitSet();

		bst1.set(2,5);
		
		BitSet bst2 = new BitSet();

		bst2.set(3,11);
		
		BitSet bst3 = new BitSet();

		bst3.set(5,9);
		
		
		HashMap<Integer,BitSet> test = new HashMap<Integer, BitSet>();
		
		test.put(1, bst1);
		test.put(2, bst2);
		test.put(3, bst3);
		

		System.out.println(" bs1 = " + bst1 + " bs2 = " + bst2 +"bs3 = "+ bst3);
		//System.out.println("hashmap test is : "+test);
		
		new Parity(test);
		 
		Parity parity = new Parity(test);
		BitSet ttt = parity.getBs();
		System.out.println(ttt);

		
		if (verif.equals(ttt)){

			System.out.println("Bravo le Sang de la veine");
		}
		else {
			System.out.println("Parité non identique");
		}
		
		
		
		
		
		

		
	
	}
	
}
