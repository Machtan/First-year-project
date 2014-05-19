# coding: utf-8
# Created by Jabok @ May 9th 2014
import os, sys, math, struct, resource, shutil, yaml
from argparse import ArgumentParser

class Road:
    def __init__(self, name, roadtype, zipcode, speedlimit, oneway, nodes, drivetimes=[]):
        self.name       = name
        self.type       = roadtype
        self.zip        = zipcode
        self.speedlimit = speedlimit
        self.oneway     = oneway
        self.drivetimes = drivetimes
        if type(nodes) == str:
            self.nodes = [str(node) for node in nodes.split(",")]
        else:
            self.nodes = nodes
    
    def __str__(self):
        return "{0} {2} ({1})[{3} km/t] {4} {5}".format(self.name, 
            self.type, self.zip, self.speedlimit, self.oneway, self.nodes)

class Inter:
    def __init__(self, nid, x, y):
        self.nid = nid
        self.x = x
        self.y = y
    
    def __str__(self):
        return "{0}: {1}".format(self.nid, (self.x, self.y))
    
    def __repr__(self):
        return str(self) # Not canonical, but good for list debugging

tf = {True, False}
def mjoin(obj, mems, sep):
    text = ""
    for num, mem in enumerate(mems):
        if num != 0:
            text += ","
        val = getattr(obj, mem)
        if val in tf: # Represent bools as 0/1
            text += str(int(val))
        else:
            text += str(val)
    return text

class FormatException(Exception):
    def __init__(self, saveformat):
        super().__init__("Unrecognized save format: '{0}'".format(saveformat))

sepchar = "@" # Change in the loader as well!
class Loader:
    def save_text(road, outfile=sys.stdout):
        """Saves the text representation of a road to a file"""
        mems = ["name","type", "zip","speedlimit","oneway"]
        text = mjoin(road, mems, ",")
        pmems = ["nid","x","y"]
        text += sepchar+",".join(mjoin(point, pmems, ",") for point in road.nodes)
        text += sepchar+",".join(str(drivetime) for drivetime in road.drivetimes)
        text += "\n"
        outfile.write(text)
        return text

    def save_bin(road, outfile=None):
        """Returns the binary representation of a road to a file"""
        # Big endian ( > )
        fmt = ">sHHB?"
        fmt += "Lff" * len(road.nodes)
        data = [bytearray(road.name, "utf-8"), road.zip, road.speedlimit, 
            road.type, road.oneway]
        for point in road.nodes:
            data += [point.nid, point.x, point.y]
        
        flen = len(fmt)-1
        dlen = len(data)
        #print("flen/dlen: {0}/{1} \nfmt/data: {2}/\n{3}".format(flen, dlen, fmt, data))    
    
        binary = struct.pack(fmt, *data)
        if not outfile:
            print(str(binary))
        else:
            outfile.write(binary)
        return binary
    
    # Defines the function for the format options
    formats = {
        "text":     save_text,
        "binary":   save_bin
    }
    
    def save(road, file, saveformat):
        """Returns the value of the road for saving in the given format"""
        if not saveformat in Loader.formats:
            raise FormatException(saveformat)
        return Loader.formats[saveformat](road, file)

class RoadType:
    highway = 0

def memusage():
    usage = resource.getrusage(resource.RUSAGE_SELF).ru_maxrss
    units = {1e9: "GB", 1e6: "MB", 1e3: "KB"}
    if "darwin" in sys.platform:
        for amount, unit in sorted(units.items(), reverse=True):
            if usage > amount:
                return "{0} {1}".format(usage/float(amount), unit)
    else:
        return "{0} Units".format(usage)

def main():
    p1 = Inter(123789,300000000,1000000)
    p2 = Inter(223459,12323240,14392387490)
    p3 = Inter(323456,2230480,23485739850)
    
    r = Road("Dyssevej", RoadType.highway, 2000, 60, False, [p1, p2, p3])
    t = Loader.save_text(r)
    b = Loader.save_bin(r)
    print("len(text): {0}, len(bin): {1}".format(len(t), len(b)))
    print(t)
    print(b)

if __name__ == '__main__':
    main()