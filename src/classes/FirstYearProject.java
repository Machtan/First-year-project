
package classes;

import classes.AddressParsing.IllegalCharacterException;
import classes.AddressParsing.NoMatchException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author jakoblautrupnysom
 */
public class FirstYearProject {
    
    public static String[] addresses = new String [] {
        "",
        "Rued Langgaards Vej",
        "Rued Langgaards Vej 7, 5. sal, København S",
        "Rued Langgaards Vej 7 2300 København S",
        "Rued Langgaards Vej 7, 5.",
        "Rued Langgaards Vej 7A København S",
        "Rued Langgaards Vej i København",
        "Rued Langgaards Vej 23, 5. sal tv. København S",
        "Rued Langgaards Vej 23, st. tv. 2300",
        "Rued Langgaards Vej 23, stuen tv. 2300",
        "Baunevej 22, Søndre-Hakkelse",
        "Venne-Vej 33, 2350 ØverbyVester",
        "Venne-Vej 24, st. t.h. 2350 ØverbyVester",
        "Hej-Vej, 32, 5. sal tv.,    5060, Elmehøj",
        "Are you kidding me????!?!?!?!111"
    };
    
    public static void testAddresses(String[] addresses) {
        for (String testAddress : addresses) {
            try {
                String[] parts = AddressParsing.parseAddress(testAddress);
                System.out.println("== Parsed '"+testAddress+"' ==");
                System.out.println(Utils.joinStrings(parts, "#"));
            } catch (NoMatchException | IllegalCharacterException ex) {
                System.out.println(ex);
            }
        }
    }
    
    // A list of roads that should be exempt due to being impractical

    /**
     * A list of the roads that cannot be handled by the parser due to 
     * impractical formatting
     */
     public static final String[] exceptions = new String[] {
        // Handled exceptions:
        // (?:Motorvej (?:[a-zøA-ZØ]*//d+[/]*)+)
        // (?:Pier \\d)
        // (?:Rute \\d{1,3})
        // (?:Slippe \\d)
        // (?:Vej \\d{1,2} Nr)
        // (?:(?:Brabrand ){0,1}Haveforening(?:en){0,1} af (?:\\d{1,2}[.] [a-zA-Z]+){0,1}\\s*\\d{4})
        // (?:Kaj \\d+)
        // (?:Hovedvej \\d+)
        // (?:Ring \\d+)
        // (?:Motorring \\d+)
        // (?:Trafikplats Vellinge Norra \\d)
        // "(?:Fra-/tilkørsel (?:nr[.] ){0,1}(?:[A-Z]*\\d+[/ ])+\\s*"+allChars+"*)"
        // "(?:"+allChars+"+ \\d+ Haveforening)"
        // Manually added the remaining 6 :)
        };
    
    public static HashSet<String> exceptionSet = new HashSet<>(Arrays.asList(exceptions));
    public static ArrayList<String> invalidRoads = new ArrayList<>();
    
    /**
     * Tests whether the given road is parsed correctly
     * @param road The road to be tested
     * @return 
     */
    public static boolean testRoad(String road) {
        try {
            // Handle known bad input
            /*if (exceptionSet.contains(road)) {
                System.out.println("Skipping '"+road+"'...");
                return true;
            }*/
            
            String[] parts = AddressParsing.parseAddress(road);
            //System.out.println("== Parsed '"+road+"' ==");
            boolean valid = parts[0].equals(road);
            if (!valid) {
                invalidRoads.add(road);
                //System.out.println("Bad output:\n" + joinStrings(parts, "#"));
            }
        } catch (NoMatchException | IllegalCharacterException ex) {
            System.out.println(ex);
            invalidRoads.add(road);
        }
        return true;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Test that the standard test input works as expected
        System.out.println("-- Basic Test --");
        
        testAddresses(new String[]{"Lyngby Hovedgade 2, 2800 Kgs. Lyngby"});
        testAddresses(addresses);
        
        // Test that it can recognize EVERYTHING :)
        System.out.println("-- Extensive Recognition Test --");
        
        String[] roads = AddressValidator.getArray();
        int counter = 1;
        for (String road : roads) {
            if (!testRoad(road)) {
                System.out.println("Could not parse '"+road+"'");
                System.out.println("Ending test at road "+counter+" out of "+roads.length);
                return;
            }
            counter++;
        }
        int invalids = invalidRoads.size();
        System.out.println("Found "+ invalids +" invalid roads"+((invalids==0)? "!": ":"));
        for (String road : invalidRoads) {
            System.out.println("\""+road+"\",");
        }
    }
}
