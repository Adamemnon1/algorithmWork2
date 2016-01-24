package lab2;

import java.util.ArrayList;

/**
 * A hash table mapping Strings to their positions in the pattern sequence.
 *
 * Fill in the methods for this class.
 */
public class StringTable {

	int numElements ; //I added, to keep track of number of elements in hash table
	int size; //size of table
	Record[] table; //hashtable 
	double loadFactor = 0; 

	/**
	 * Create an empty table of size n
	 *
	 * @param n size of the table
	 */
	public StringTable(int n) { //make tables size of power of 2, easier to operate on it.
		this.size = n;
		int t = 2;
		while (t < n) {
			t = t+2;
		}
		size = t; //t is a power of 2;
		this.table = new Record[size];
	}

	/**
	 * Create an empty table.  You should use this construction when you are
	 * implementing the doubling procedure.
	 */
	public StringTable() {
		this.table = new Record[4];
		this.size = 4;
	}

	/**
	 * Insert a Record r into the table.
	 *
	 * If you get two insertions with the same key value, return false.
	 *
	 * @param r Record to insert into the table.
	 * @return boolean true if the insertion succeeded, false otherwise
	 */
	public boolean insert(Record r) {
		String keygivenS = r.getKey();
		int keygiven = toHashKey(keygivenS);
		loadFactor = (double)(numElements) / (double)(this.size);
		if (loadFactor >= 0.25) { 
			doubling(); //double the size of the table.
		}
		if (find(r.getKey()) == null ) {
			table[hash(r.getKey())] = r;
			numElements++;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Return the hash position of this key.  If it is in the table, return
	 * the postion.  If it isn't in the table, return the position it would
	 * be put in.  This value should always be smaller than the size of the
	 * hash table.
	 *
	 * @param key to hash
	 * @return the int hash
	 */
	public int hash(String key) {
		int h = baseHash(toHashKey(key));
		int changingH = h;
		int totalchangeinH = 0;
		int steph = stepHash(toHashKey(key));
		int kgiven = toHashKey(key);
	
		int foundempty = -1;
		int dummyloc = -1;
		
		boolean foundrecord = false; //record was found
		boolean founddummy = false; //dummy was found
		boolean foundnull = false; // null found in a location we looked at.
		
		for (int i = 0; i < size ; i++) {
			totalchangeinH = changingH % size; //the total change h experiences. 
			if (table[totalchangeinH] != null ) { //if not null, check the values there.
				//check the int and then the string values.
				if (toHashKey(table[totalchangeinH].getKey()) == kgiven && table[totalchangeinH].getKey().equals(key)) {
					foundrecord = true;
					break;//found it, done.
				}
				else if (!founddummy && toHashKey(table[totalchangeinH].getKey()) == toHashKey("dummy") && table[totalchangeinH].getKey().equals(key)) {
					founddummy =true;
					dummyloc = totalchangeinH; //location of dummy
				}
			}
			else {
				foundnull = true;
				break;
			}
			changingH = (changingH + steph) ; //increment changingH 
		}
		if (foundrecord) {
			return totalchangeinH;
		}
		else if (founddummy) {
			return dummyloc;
		}
		return totalchangeinH;
	}

	/**
	 * Doubling method. This changes the size of the table and transfers the values over from
	 * the previously sized table to the new one. 
	 */
	private void doubling() {
		Record[] origtable = table;
		int origsize = table.length;
		int doubsize = origsize *2;
		table = new Record[doubsize];
		size = doubsize;
		numElements = 0;
		for (int i= 0; i < origsize; i++) {
			if (origtable[i] != null) {
				insert(origtable[i]);
			}
		}
	}

	/**
	 * Delete a record from the table.
	 *
	 * Note: You'll have to find the record first unless you keep some
	 * extra information in the Record structure.
	 *
	 * @param r Record to remove from the table.
	 */
	public void remove(Record r) {
		Record rec = find(r.getKey());
		String dum = "dummy";
		Record dummy = new Record(dum);
		if (find(r.getKey()) != null && toHashKey(find(r.getKey()).getKey()) != toHashKey("dummy") && !find(r.getKey()).equals("dummy")  ) {
			int loc = hash(r.getKey());
			table[loc] = dummy;
			numElements--; //reduce number of elements in the table.
		}
	}

	/**
	 * Find a record with a key matching the input.
	 *
	 * @param key to find
	 * @return the matched Record or null if no match exists.
	 */
	public Record find(String key) {
		int h = hash(key);
		int keygiven = toHashKey(key);
		//check to see if the given key is there. 
		if (table[h] != null && toHashKey(table[h].getKey()) == keygiven && key.equals(table[h].getKey()) ){
			return table[h];
		}
		else {
			return null; //returns null even if is dummy.
		}
	}

	/**
	 * Return the size of the hash table (i.e. the number of elements
	 * this table can hold)
	 *
	 * @return the size of the table
	 */
	public int size() {
		return this.size;
	}

	/**
	 * Computes the step hash of a hash key
	 *
	 * @param hashKey
	 * @return int step hash
	 */
	int stepHash(int hashKey) { //S1k and on
		int h2 = hash2(hashKey);
		return h2;
	}

	/**
	 * Computes the base hash of a hash key
	 *
	 * @param hashKey
	 * @return int base hash
	 */
	int baseHash(int hashKey) { //S0k
		int m = this.size;
		int sk = hash1(hashKey) ;
		return sk;
	}

	/**
	 * Helper method to calculate baseHash.
	 * @param key = int representation of the key
	 * @return = int, the index where the record can/should go.
	 */
	public int hash1(int key) {
		int m = this.size;
		double a = (Math.sqrt(5) - 1) / 2 ;
		double innest = (key*a) - Math.floor(key*a); //this can never be greater than 1;
		double inner = m * innest;
		int hk1 = (int) Math.floor(inner);
		return hk1;
	}

	/**
	 * Helper method for stepHash.
	 * @param key = int representation of the key
	 * @return int, the index where the record can/should go.
	 */
	public int hash2(int key) {
		int m = this.size;
		double a = (Math.sqrt(5) - 0.5) / 2;
		double innest = key * a - Math.floor(key*a); 
		double inner = m * innest;
		int hk2 = (int) Math.floor(inner);
		//make odd
		if ((hk2 %2) == 0) {
			hk2 += 1; //doubles my collision rate.
		}
		return hk2;
	}

	/**
	 * Convert a String key into an integer that serves as input to hash functions.
	 * This mapping is based on the idea of a linear-congruential pseuodorandom
	 * number generator, in which successive values r_i are generated by computing
	 *    r_i = (A * r_(i-1) + B) mod M
	 * A is a large prime number, while B is a small increment thrown in so that
	 * we don't just compute successive powers of A mod M.
	 *
	 * We modify the above generator by perturbing each r_i, adding in the ith
	 * character of the string and its offset, to alter the pseudorandom
	 * sequence.
	 *
	 * @param s String to hash
	 * @return int hash
	 */
	int toHashKey(String s) {
		int A = 1952786893;
		int B = 367253;
		int v = B;
		for (int j = 0; j < s.length(); j++) {
			char c = s.charAt(j);
			v = A * (v + (int) c + j) + B;
		}
		if (v < 0) {
			v = -v;
		}
		return v;
	}
}
