package classes;
import classes.Utils.Tokenizer;
import krak.EdgeData;

/**
 * The RoadPart class <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 25-Feb-2014
 */
public class RoadPart {
    final int sourceID; // The ID of one of the road's ending intersections
    final int targetID; // The ID of one of the road's ending intersections
    final int type; // The road type
    final String name; // The name of the road
    
    // Address numbering on sides of the road
    final int sLeftNum;
    final int eLeftNum;
    final int sRightNum;
    final int eRightNum;
    
    // House address lettering mumbo jumbo
    final String sLeftLetter;
    final String eLeftLetter;
    final String sRightLetter;
    final String eRightLetter;
    
    // The postal code at each side
    // (What is this even used for... post area borders?)
    final int rightZip;
    final int leftZip;
    
    // Highway turn-off number (?)
    final int turnoffNumber;
    
    // What type of zone is the road in (residential, industrial etc.)
    final ZoneType zone;
    final int speedLimit;
    final double driveTime;
    
    // Info related to driveability
    final String oneWay;
    final String fTurn;
    final String tTurn;
    
    // Probably unneeded
    //String leftParish;
    //String rightParish;
    
    /**
        Constructor taking a revised line of data and parsing it
        @param line The line of data to parse
    */
    public RoadPart(String line) {
        // TOKENIZE DATA SHIT
        Tokenizer.setLine(line);
        sourceID = Tokenizer.getInt();
        targetID = Tokenizer.getInt();
        type = Tokenizer.getInt();
        name = Tokenizer.getString();
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
        type = data.TYP;
        name = data.VEJNAVN;
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
            type,
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
}
