package classes;
import enums.ZoneType;
import classes.Utils.Tokenizer;
import enums.RoadType;
import interfaces.QuadNode;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import krak.EdgeData;

/**
 * The RoadPart class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
public class RoadPart implements CharSequence, QuadNode {
    public final long sourceID; // The ID of one of the road's ending intersections
    public final long targetID; // The ID of one of the road's ending intersections
    public final RoadType type; // The road type
    public final String name; // The name of the road
    
    // Address numbering on sides of the road
    public final int sLeftNum;
    public final int eLeftNum;
    public final int sRightNum;
    public final int eRightNum;
    
    // House address lettering mumbo jumbo
    public final String sLeftLetter;
    public final String eLeftLetter;
    public final String sRightLetter;
    public final String eRightLetter;
    
    // The postal code at each side
    // (What is this even used for... post area borders?)
    public final int rightZip;
    public final int leftZip;
    
    // Highway turn-off number (?)
    public final int turnoffNumber;
    
    // What type of zone is the road in (residential, industrial etc.)
    public final ZoneType zone;
    public final int speedLimit;
    public final double driveTime;
    
    // Info related to driveability
    public final String oneWay;
    public final String fTurn;
    public final String tTurn;
    
    // The area this road is in
    protected Intersection p1;
    protected Intersection p2;
    protected Rect area;
    
    // Probably unneeded
    //String leftParish;
    //String rightParish;
    
    public static final Pattern exPattern = Pattern.compile("((?:[a-zâüäæöøåéèA-ZÂÛÆÄØÖÅ:\\-/'´&\\(\\)]+\\s*)+), Den");
    public static final HashMap<String, String> rep = new HashMap<>();
    public static boolean initialized = false;
    /**
     * Initializes the static string replacement map
     */
    public static void createReplacementMap() {
        if (initialized) { return; }
        rep.put("Brøndby Haveby Afd. 6, Rosen", "Brøndby Haveby Afd. 6");
        rep.put(", P.PLADS", "");
        rep.put(",P.PLADS","");
        rep.put(",HAVESELAB"," Haveselskab");
        rep.put(",HAVEFORENING", " HAVEFORENING");
        rep.put(",HAVEF"," HAVEFORENING");
        rep.put("KRATHUS, SKOVALLEEN", "SKOVALLEEN");
        initialized = true;
    }
    
    public void setPoints(Intersection p1, Intersection p2) {
        this.p1 = p1;
        this.p2 = p2;
        double x = Math.min(p1.x, p2.x);
        double y = Math.min(p1.y, p2.y);
        double width = Math.abs(p1.x - p2.x);
        double height = Math.abs(p1.y - p2.y);
        area = new Rect(x,y,width,height);
    }

    /**
     * This method returns the roadpart as a drawable line with a color which
     * can be rendered in the given target area
     * @param x1 The origin x-coordinate of the source area
     * @param y1 The origin y-coordinate of the source area
     * @param target The target 
     * @param ppu Pixels per units (a ratio)
     * @param heightFac The heightFactor (windowHieght-target.y)
     * @param ins Render instructions
     * @return The roadPart as a drawable line
     */
    public Line asLine(double x1, double y1, Rect target, double ppu, double heightFac, RenderInstructions ins) {
        Line line = new Line(
            target.x+(p1.x-x1)*ppu, 
            heightFac - (p1.y-y1)*ppu, 
            target.x+(p2.x-x1)*ppu, 
            heightFac - (p2.y-y1)*ppu, 
            ins.getColor(type)
        );
        return line;
    }
    
    /**
     * Returns the bounding area of this road part
     * @return the bounding area of this road part
     */
    public Rect getRect() {
        return area;
    }
    
    /**
     * Attempts to convert a name, handling exception cases :i
     * @param name The name of the road
     * @return The converted road name .
     */
    private static String convertName(String name) {
        if (!initialized) { createReplacementMap(); }
        if (name.contains(",")) {
            //System.out.println("Converting name '"+name+"'");
            for (String r : rep.keySet()) {
                name = name.replace(r, rep.get(r));
            }
            Matcher m = exPattern.matcher(name);
            if (m.find()) {
                name = "Den "+m.group(1);
            }
            System.out.println("Converted to '"+name+"'");
        }
        
        return name;
    }
    
    /**
        Constructor taking a revised line of data and parsing it
        @param line The line of data to parse
    */
    public RoadPart(String line) {
        // TOKENIZE DATA SHIT
        Tokenizer.setLine(line);
        sourceID = Tokenizer.getLong();
        targetID = Tokenizer.getLong();
        type = RoadType.fromValue(Tokenizer.getInt());
        
        // Special case handling :u
        name = convertName(Tokenizer.getString());
        
        sLeftNum = Tokenizer.getInt();
        eLeftNum = Tokenizer.getInt();
        sRightNum = Tokenizer.getInt();
        eRightNum = Tokenizer.getInt();
        sLeftLetter = Tokenizer.getString();
        eLeftLetter = Tokenizer.getString();
        sRightLetter = Tokenizer.getString();
        eRightLetter = Tokenizer.getString();
        rightZip = Tokenizer.getInt();
        leftZip = Tokenizer.getInt();
        turnoffNumber = Tokenizer.getInt();
        zone = ZoneType.TEMP; // TODO fix
        speedLimit = Tokenizer.getInt();
        driveTime = Tokenizer.getDouble();
        oneWay = Tokenizer.getString();
        fTurn = Tokenizer.getString();
        tTurn = Tokenizer.getString();
    }
    
    /**
        Constructor taking an EdgeData entity
        @param data An EdgeData entity
    */
    public RoadPart(EdgeData data) {
        sourceID = data.FNODE;
        targetID = data.TNODE;
        sLeftNum = data.FROMLEFT;
        eLeftNum = data.TOLEFT;
        sRightNum = data.FROMRIGHT;
        eRightNum = data.TORIGHT;
        sLeftLetter = data.FROMLEFT_BOGSTAV;
        eLeftLetter = data.TOLEFT_BOGSTAV;
        sRightLetter = data.FROMRIGHT_BOGSTAV;
        eRightLetter = data.TORIGHT_BOGSTAV;
        turnoffNumber = data.FRAKOERSEL;
        zone = ZoneType.TEMP; // TODO permananent fix
        speedLimit = data.SPEED;
        driveTime = data.DRIVETIME;
        type = RoadType.fromValue(data.TYP);
        
        name = convertName(data.VEJNAVN);
        
        rightZip = data.H_POSTNR;
        leftZip = data.V_POSTNR;
        oneWay = data.ONE_WAY;
        fTurn = data.F_TURN;
        tTurn = data.T_TURN;
    }
    
    /**
     Returns a string representation of the object 
     (can also be used for serialization)
     * @return 
     */
    @Override 
    public String toString() {
        return Utils.joinStrings(new Object[]{
            sourceID,
            targetID,
            type.value,
            name,
            sLeftNum,
            eLeftNum,
            sRightNum,
            eRightNum,
            sLeftLetter,
            eLeftLetter,
            sRightLetter,
            eRightLetter,
            rightZip,
            leftZip,
            turnoffNumber,
            zone.value,
            speedLimit,
            driveTime,
            oneWay,
            fTurn,
            tTurn
        }, ",");
    }

    @Override
    public int length() {
        return toString().length();
    }

    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }
}
