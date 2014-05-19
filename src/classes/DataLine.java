package classes;

import java.util.HashMap;

/**
 * This class parses a line of comma-separated lines of data, with
 * strings delimited by single-quotes and optionally containing
 * commas.
 *
 * String values are manually interned, i.e., each string in the
 * parsed data will be represented in-core only once.
 *
 * @author Peter Tiedemann petert@itu.dk
 *
 * Peter Sestoft 2008: Modified to avoid building and
 * destroying a LinkedList.
 *
 * Søren Debois 2014: Moved Peter Sestoft's manual string interning
 * here.
 * 
 * Now heavily modified by Jakob Lautrup nysom. (Who needs safety anyway)
 */
public class DataLine {

	private static HashMap<String,String> interner =
            new HashMap<>();

	private String intern(String s){
            String interned = interner.get(s);
            if (interned != null)
                    return interned;
            else {
                    interner.put(s, s);
                    return s;
            }
	}

	/**
	 * Reset the interner map. This may conserve space if not all
	 * strings in the input data set are used.
	 */
	public static void resetInterner() {
		interner = new HashMap<>();
	}

	private final String line;
	int next;

	public DataLine(String line){
		this.line = line;
		next = 0;
	}

	/**
	 * Returns true if there are more tokens, and false otherwise
	 */
	public boolean hasMore() {
		return next < line.length();
	}

	/**
	 * Returns the next token. If apostrophes surround the token, they overrule commas, and the token is returned without the apostrophes
	 */
	private String nextToken() {
            int comma = line.indexOf(',', next);
            String token;
            if (comma >= 0) { // Comma separator found
                token = line.substring(next, comma);
                next = comma + 1;
            } else {          // This is the last data field
                token = line.substring(next);
                next = line.length();
            }
            return token;
	}

	/**
	 * Attempts to parse the next token as an integer
	 */
	public int getInt() {
            return Integer.parseInt(nextToken());
	}

        public long getLong() {
            return Long.parseLong(nextToken());
        }
        
        public boolean getBool() {
            return getChar() == '1';
        }

	/**
	 * Attempts to parse the next token as a double
	 */
	public double getDouble() {
            return Double.parseDouble(nextToken());
	}

        public float getFloat() {
            return Float.parseFloat(nextToken());

        }

        public short getShort() {
            return Short.parseShort(nextToken());
        }

	/**
	 * Returns the next token as a string
	 * @return
	 */
	public String getString(){
		return intern(nextToken());
	}

        public char getChar() {
            String s = nextToken();
            if (s.length() > 0) {
                return s.charAt(0);
            } else {
                return 'n'; // Default value!!!
            }
        }



	/**
	 * Discard the current token
	 *
	 */
	public void discard(){
		nextToken();
	}
}
