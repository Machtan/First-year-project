# coding: utf-8
# Created by Jabok @ May 9th 2014
import os, sys, math, struct, resource, shutil, yaml
from argparse import ArgumentParser

class Road:
    def __init__(self, name, zipcode, points, speedlimit, roadtype, 
            oneway):
        self.name = name
        self.zip = zipcode
        self.speedlimit = speedlimit
        self.roadtype = roadtype
        self.oneway = oneway
        self.points = points

class Point:
    def __init__(self, nid, x, y):
        self.nid = nid
        self.x = x
        self.y = y

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

def save_text(newroad, outfile=sys.stdout):
    mems = ["name","zip","speedlimit","roadtype","oneway"]
    text = mjoin(newroad, mems, ",")
    text += ","
    pmems = ["nid","x","y"]
    text += ",".join(mjoin(point, pmems, ",") for point in newroad.points)
    text += "\n"
    outfile.write(text)
    return text

def save_bin(newroad, outfile=None):
    # Big endian ( > )
    fmt = ">sHHB?"
    fmt += "Lff" * len(newroad.points)
    data = [bytearray(newroad.name, "utf-8"), newroad.zip, newroad.speedlimit, 
        newroad.roadtype, newroad.oneway]
    for point in newroad.points:
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

def convert_mapdata(roadfile, interfile, outfile, format):
    """Converts the map data in the given files and saves them to
    the outfile in the given format."""
    

def main():
    parser = ArgumentParser()
    parser.add_argument("roadfile",
        help="The file to read roads from")
    parser.add_argument("interfile",
        help="The file to read intersections from")
    parser.add_argument("outfile",
        help="The file to save everything to")
    parser.add_argument("-f", "--format", 
        help="""The format to convert the files to. 
        Options: text[default], binary""",
        default="text")
    args = parser.parse_args()
    if not args.format in formats:
        print("Please select a valid format. Options: \n- ")
        print("\n- ".join(form for form in formats))
    else:
        convert_mapdata(args.roadfile, args.interfile, args.outfile, args.format)  

class RoadType:
    highway = 0

def _save_ins(inters, folder, nid):
    fpath = os.path.join(folder, nid)
    if not os.path.exists(fpath):
        try:
            print("Saving dict {0}...".format(nid))
            with open(fpath, "w") as w:
                yaml.dump(inters, w)
        except KeyboardInterrupt as e:
            shutil.remove(fpath)
            raise e

def prepare_intersections(interfile, ins_per_file=1000, folder="intersections"):
    inters = {}
    if not os.path.exists(folder):
        os.mkdir(folder)
    with open(interfile, "r") as infile:
        nid = "0"
        for num, line in enumerate(infile, 1):
            nid, x, y = line.split(",")
            inters[int(nid)] = [float(x), float(y)]
            if (num % ins_per_file) == 0:
                fpath = os.path.join(folder, nid)
                _save_ins(inters, folder, nid)
                inters = {}
        if inters:
            _save_ins(inters, folder, nid)
            

def remove_intersections(folder="intersections"):
    shutil.rmtree(folder)
    print("'{0}' removed!".format(folder))

def make_seekable(interfile):
    """Makes an intersection file easily seekable (for binary search)"""
    interfile

def binfilesearch(value, dictnamelist, lo=0, hi=None):
    if hi==None: hi = len(dictnamelist)
    if lo == hi: return dictnamelist[lo]
    index = lo + (hi-lo)//2
    if dictnamelist[index] == value: return 
    

def memusage():
    usage = resource.getrusage(resource.RUSAGE_SELF).ru_maxrss
    units = {1e9: "GB", 1e6: "MB", 1e3: "KB"}
    if "darwin" in sys.platform:
        for amount, unit in sorted(units.items(), reverse=True):
            if usage > amount:
                return "{0} {1}".format(usage/float(amount), unit)
    else:
        return "{0} Units".format(usage)

if __name__ == '__main__':
    #main()
    p1 = Point(123789,300000000,1000000)
    p2 = Point(223459,12323240,14392387490)
    p3 = Point(323456,2230480,23485739850)
    interfile = "intersections.txt"
    print("Preparing intersections...")
    prepare_intersections(interfile)
    
    #binary = struct.pack(">Lff", p1.nid, p1.x, p1.y)
    #print("Binary:", binary)
    
    try:
        print("Memory usage: "+memusage())
        print("Waiting for interrupt... (Ctrl + C)")
        while True: pass
    except KeyboardInterrupt:
        #print("Removing intersections...")
        #remote_intersections()
        print("Closing...")
        
        
    
    r = Road("Dyssevej", 2000, [p1, p2, p3], 60, RoadType.highway, False)
    t = save_text(r)
    b = save_bin(r)
    print("len(text): {0}, len(bin): {1}".format(len(t), len(b)))