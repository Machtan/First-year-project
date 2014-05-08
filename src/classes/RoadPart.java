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
    public final short sLeftNum;
    public final short eLeftNum;
    public final short sRightNum;
    public final short eRightNum;
    
    // House address lettering mumbo jumbo
    public final char sLeftLetter;
    public final char eLeftLetter;
    public final char sRightLetter;
    public final char eRightLetter;
    
    // The postal code at each side
    // (What is this even used for... post area borders?)
    public final short rightZip;
    public final short leftZip;
    
    // Highway turn-off number (?)
    public final int turnoffNumber;
    
    // What type of zone is the road in (residential, industrial etc.)
    public final ZoneType zone;
    public final short  speedLimit;
    public final float driveTime;
    
    // Info related to driveability
    public final char oneWay;
    public final char fTurn;
    public final char tTurn;
    
    // The area this road is in
    public Intersection p1;
    public Intersection p2;
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
        float x = (float)Math.min(p1.x, p2.x);
        float y = (float)Math.min(p1.y, p2.y);
        float width = (float)Math.abs(p1.x - p2.x);
        float height = (float)Math.abs(p1.y - p2.y);
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
    public Line asLine(float x1, float y1, Rect target, float ppu, float heightFac, RenderInstructions ins) {
        Line line = new Line(
            target.x+(p1.x-x1)*ppu, 
            heightFac - (p1.y-y1)*ppu, 
            target.x+(p2.x-x1)*ppu, 
            heightFac - (p2.y-y1)*ppu, 
            ins.getColor(type)
        );
        return line;
    }
    
    public float x1() { return p1.x; }
    public float y1() { return p1.y; }
    public float x2() { return p2.x; }
    public float y2() { return p2.y; }
    
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
        
        sLeftNum = Tokenizer.getShort();
        eLeftNum = Tokenizer.getShort();
        sRightNum = Tokenizer.getShort();
        eRightNum = Tokenizer.getShort();
        sLeftLetter = Tokenizer.getChar();
        eLeftLetter = Tokenizer.getChar();
        sRightLetter = Tokenizer.getChar();
        eRightLetter = Tokenizer.getChar();
        rightZip = Tokenizer.getShort();
        leftZip = Tokenizer.getShort();
        turnoffNumber = Tokenizer.getInt();
        zone = ZoneType.TEMP; // TODO fix
        speedLimit = Tokenizer.getShort();
        driveTime = Tokenizer.getFloat();
        oneWay = Tokenizer.getChar();
        fTurn = Tokenizer.getChar();
        tTurn = Tokenizer.getChar();
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
