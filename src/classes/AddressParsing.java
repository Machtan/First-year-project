package classes;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * The AddressParsing class contains classes and methods to do with parsing
 * addresses
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 28-Jan-2014
 */
public class AddressParsing {
    /**
     * An exception for when the parser finds an non-allowed character in the 
     * given address.
     */
    public static class IllegalCharacterException extends Exception {
        public IllegalCharacterException(String input, String character) {
            super("Found illegal character '"+character +
                    "' in the input string '"+input+"'!");
        }
    }
    
    /**
     * An exception for when the parser doesn't find any matches in the 
     * given address.
     */
    public static class NoMatchException extends Exception {
        public NoMatchException(String input) {
            super("Couldn't parse the address '"+input+"'");
        }
    }
    
    
    private static Pattern pattern; // Just for debug purposes
    private static boolean hasPattern = false;
    private static Pattern illegalPattern;
    
    /**
     * Creates the pattern that the parser should use
     */
    private static void createPattern() {
        // Make the editing of charsets easier
        String lcaseChars = "[a-zâüäæöøåéè\\-/'´&\\(\\)]";
        String ucaseChars = "[A-ZÂÛÆÄØÖÅ:]";
        
        String uChars = ucaseChars.replace("[", "").replace("]", "");
        String lChars = lcaseChars.replace("[", "").replace("]", "");
        String allChars = "["+uChars+lChars+"]";
        
        // Checks for characters that aren't allowed in the address input
        String illegalChars = "[^"+uChars+lChars+"0-9,. ]";
        //System.out.println("Illegal char pattern:\n" + illegalChars);
        illegalPattern = Pattern.compile(illegalChars);
        
        
        // Handle some of the usual unusual patterns
        // "Found 125 invalid roads:" (the exception list handles all of these)
        // Keep in mind that this is out of 58455 roads ;)
        String[] exceptionList = new String[] {
            "(?:Vej \\d{1,2} Nr)",
            "(?:Slippe \\d)",
            "(?:Rute [a-zA-Z]?\\d{1,3})",
            "(?:Pier \\d)",
            "(?:Motor(?:trafik)?vej (?:[a-zøA-ZØ]*\\d+[/]*)+(?:\\s*"+allChars+")*)",
            "(?:Brøndby Haveby Afd. \\d+)",
            "(?:(?:Brabrand )?Haveforening(?:en)? af (?:\\d{1,2}[.] [a-zA-Z]+)?\\s*\\d{4})",  
            "(?:Kaj \\d+)",
            "(?:Hovedvej \\d+)",
            "(?:Ring \\d+)",
            "(?:Motorring \\d+)",
            "(?:Trafikplats Vellinge Norra \\d)",
            "(?:Fra-/tilkørsel (?:nr[.] )?(?:[A-Z]*\\d+[/ ]*)+\\s*"+allChars+"*)",
            "(?:"+allChars+"+ \\d+ Haveforening)",
            
            // Manual overrides
            "(?:City 2)",
            "(?:HAVEFORENING AF 1934)",
            "(?:Haveselskab af 1916)",
            "(?:Haveselskabet 1948)",
            "(?:Haveforeningen 515)",
            "(?:Frederik 7 Vej)",
            "(?:5 Junivej)",
        };
        // Combine the exceptions into a single pattern
        String exceptions = "(?:" + Utils.joinStrings(exceptionList, "|") + ")";
        
        // number | date     | name
        // nr. 6  | 24. juni | Bondevej
        String number = "(?:nr[.] \\d+)"; // Allows numbers
        String date = "(?:\\d{1,2}\\. )"; //Allows dates 
        String name = "(?:"+allChars+"+\\.?)";
        // Combine the road elements
        String regularRoads = "(?:(?:(?:\\s*(?:"+ number +"|"+ date +"|"+ name +"))+))";
        
        // Combine the regular roads with the exceptions
        String road = "(" + exceptions + "|" + regularRoads+ ")";
        //System.out.println("Road Pattern:\n"+road); // IT'S OVER 9000!
        
        String roadNumber = "(\\d+[a-zA-Z]?)?";
        
        // Floor component of an address
        String floorNo      = "((?:\\d{1,2}|(?:st(?:uen)?))[. ]+"; // Floor number
        String floorDesc    = "(?:\\s*[sS]al)?"; // Explicit floor notation
        String doorPos      = "(?: ?t\\.?[hv]\\.?)?)?"; // Door position
        // Combine the elemnts to a pattern for floor descriptions
        String floor = floorNo + floorDesc + doorPos;
        
        String postCode = "(\\d{4})?";
        String city = "(?:i )?((?:(?:\\s*|-)"+ucaseChars+lcaseChars+"*[.]?)+)?";
        
        // The expression to look for between the address fields
        String sep = "\\s*,?\\s*"; 
        
        // Combine it all into a single expression
        String regex = Utils.joinStrings(new String[] {
            road,
            roadNumber,
            floor,
            postCode,
            city
        }, sep); // Join all these with the separator 'sep'
        
        // Debug / Bragging rights
        pattern = Pattern.compile(regex);
        hasPattern = true;
    }
    
    /**
     * Parses the given address and returns an array of its ordered components
     * It would be much more extensible and useful if it returned a HashMap.
     * @param address
     * @return A list of strings denoting the following address fields
     * ['road', 'roadnumber', 'floor', 'postal code', 'city' ]
     * If nothing is recognised for a field, its position will contain ""
     * @throws bfst.exercises.AddressParsing.IllegalCharacterException
     * @throws bfst.exercises.AddressParsing.NoMatchException
     */
    public static String[] parseAddress(String address) throws IllegalCharacterException, NoMatchException {
        if (!hasPattern) {
            createPattern();
        }
        
        Matcher illegalMatcher = illegalPattern.matcher(address);
        if (illegalMatcher.find()) {
            String illegalChar = illegalMatcher.group(0);
            throw new IllegalCharacterException(address, illegalChar);
        }
        
        // Create the regex-related instances
        Matcher matcher = pattern.matcher(address);
        
        // Create an ArrayList for storing the results
        ArrayList<String> results = new ArrayList<>();
        
        int counter = 1; // deprecated. Could be used for parsing multiple 
        // addresses in one call
        
        while (matcher.find()) { // Start the finding
            if (counter > 1) { break; } // Break after the first loop
            // Loop over all 'groups' in the match 
            // (denoted by parentheses in the pattern) eg. (hat)
            for (int i = 1; i < matcher.groupCount()+1; i++) { 
                String res = matcher.group(i);
                if (res != null) {
                    // If a group is matched, add it to the results
                    //System.out.println("Match: '"+res+"'");
                    results.add(res);
                } else {
                    // If there's no match for the group, add an empty string
                    results.add(""); 
                }
            }
            counter++;
        }
        
        // Check whether the array is completely empty
        boolean hasOutput = false;
        for (String result : results) {
            if (!result.isEmpty()) {
                hasOutput = true;
                break;
            }
        }
        if (!hasOutput) { // If so: raise an exception
            throw new NoMatchException(address);
        }
        
        // Check the road name against valid road names
        if (!AddressValidator.validate(results.get(0))) {
            throw new NoMatchException(address);
        }
        
        // Return the ArrayList converted to an array of strings
        return results.toArray(new String[matcher.groupCount()]);
    }
}
