# coding: utf-8
# Created by Jakob Lautrup Nysom @ April 20th 2014
import xml.sax
import yaml
import os

roadtypes = {
    "motorway": 1,      # "Highway"
    
    "primary": 3,       # "PrimeRoute"
    "trunk": 3,         # "PrimeRoute"
    "primary_link": 3,
    "trunk_link": 3,
    
    "secondary": 0,     
    "tertiary": 0,      
    
    "path": 8,          # "path"
    "pedestrian": 8,    # "path"
    "cycleway": 8,      # "path"
    "footway": 8,       # "path"
    "steps": 8,         # "path"
    "track": 8,         # "path"
    "living_street": 8, # Basically pedestrian-friendly roads
    "bridleway": 8,     # For horses :u
    
    "motorway_link": 31,    # "highwayxit"
    "ferry": 80,            # "ferry"
    "unclassified": 0,      # "other"
}

speedlimits = {
    "dk:rural": 80,
    "dk:urban": 50,
    "dk:motorway": 130,
    "default": 50,
}

roadspeeds = {
    "motorway": 130,
    "primary": 80,
    "secondary": 80,
    "tertiary": 80,
    "unclassified": 80,
}

"""
OSM 'highway' values
- highway: unclassified
- highway: tertiary
- highway: secondary
- highway: residential
- highway: primary
- highway: footway
- highway: motorway_link
- highway: path
- highway: service
- highway: pedestrian
- highway: motorway
- highway: construction
- highway: trunk
- highway: cycleway
- highway: living_street
- highway: track
- highway: steps

Speed limits:
    highway=motorway - 130 km/h
    highway=primary - 80 km/h
    highway=secondary - 80 km/h
    highway=tertiary - 80 km/h
    highway=unclassified - 80 km/h
"""

class RoadPart:
    def __init__(self, node1, node2, **kwargs):
        self.node1 = node1
        self.node2 = node2
        self.name = kwargs.pop("name","")
        
        if "," in self.name: # Fix that shit :u
            splat = self.name.split(",")
            self.name = splat[0] + "({0})".format("|".join(s.strip() for s in splat[1:]))
        
        roadtype = kwargs.pop("highway","other")
        self.roadtype = roadtypes.get(roadtype, 0)
        self.zonetype = 0 # TEMP (zone types currently unused)
        self.oneway = kwargs.pop("oneway","") == "yes"
        self.speed_limit = speedlimits["default"]
        if "maxspeed" in kwargs:
            speed = kwargs["maxspeed"].split(" ")[0]
            try:
                self.speed_limit = int(speed)
            except ValueError as e: 
                if speed in speedlimits:
                    self.speed_limit = speedlimits[speed]
                elif roadtype in roadspeeds:
                    self.speed_limit = roadspeeds[roadtype]
        
        # Krak stuff
        self.sLeftNum = 0
        self.eLeftNum = 0
        self.sRightNum = 0
        self.eRightNum = 0
        self.sLeftLetter = ""
        self.eLeftLetter = ""
        self.sRightLetter = ""
        self.eRightLetter = ""
        self.rightZip = 0
        self.leftZip = 0
        self.turnoffNumber = 0
        self.driveTime = 0 # unsupported
        self.fTurn = ""
        self.tTurn = ""
    
        #self.attrs = kwargs
    
    def __str__(self):
        return ",".join(str(mem) for mem in [
            self.node1,
            self.node2,
            self.roadtype,
            self.name,
            self.sLeftNum,
            self.eLeftNum,
            self.sRightNum,
            self.eRightNum,
            self.sLeftLetter,
            self.eLeftLetter,
            self.sRightLetter,
            self.eRightLetter,
            self.rightZip,
            self.leftZip,
            self.turnoffNumber,
            self.zonetype,
            self.speed_limit,
            self.driveTime,
            "", #self.oneway, # Currently omitted
            self.fTurn ,
            self.tTurn ,
        ])+"\n"

class OSMWay:
    #valid_attrs = 
    def __init__(self):
        self.attrs= {}
        self.nodes = []
        
    def add_attr(self, attr, val):
        self.attrs[attr] = val
    
    def add_node(self, node):
        self.nodes.append(node)
    
    def get_parts(self):
        parts = []
        last = len(self.nodes)-1
        for num, node in enumerate(self.nodes):
            if num == last: break
            parts.append(str(RoadPart(node, self.nodes[num+1], **self.attrs)))
        return parts
        
class OSMHandler(xml.sax.ContentHandler):
    def __init__(self, inter_outfile, road_outfile, progress_file, progress=0, progress_mark=10000):
        self.current_road   = None
        self.loaded         = 0
        self.prev_loaded    = progress
        self.loadcount      = 0 # count for when to print progress marks
        self.roads_loaded   = 0 # new roads
        self.inters_loaded  = 0 # new intersections
        self.inter_outfile  = inter_outfile
        self.road_outfile   = road_outfile
        self.progress_mark  = progress_mark
        self.progress_file  = progress_file
        print("Created handler with {0} previously loaded objects".format(self.prev_loaded))
    
    def has_new_data(self):
        """Returns whether the handler has loaded new objects 
        (based on the loaded progress count)"""
        return self.loaded >= self.prev_loaded
    
    def startElement(self, name, attrs):
        """Callback at the beginning of a parsed tag"""
        
        # ======== Road parsing =======
        if name == "way": # Road
            self.current_road = OSMWay()
        elif name == "nd": # Member node
            self.current_road.add_node(attrs["ref"])
        elif name == "tag": # A member tag
            if self.current_road: # Of a road
                self.current_road.add_attr(attrs["k"], attrs["v"])
        
        # ===== Intersection parsing ==
        elif name == "node": # A node, anyway
            self.parsed_intersection(attrs.get("id", None), attrs.get("lat",0), attrs.get("lon",0))
            
    def endElement(self, name):
        """Callback at the end of a parsed tag"""
        if name == "way":
            self.parsed_road(*self.current_road.get_parts())
            self.current_road = None
    
    def add_progress(self, addition=0):
        """Adds more loaded objects to the handler and prints the number of loaded 
        objects after if it has loaded the number denoted by 'progress_mark'"""
        self.loaded += addition
        self.loadcount += addition
        
        if self.progress_file:
            if self.loaded > self.prev_loaded:
                self.progress_file.seek(0)
                self.progress_file.write(str(self.loaded)+"\n")
        
        if self.loadcount >= self.progress_mark:
            print("- Parsed: {0}".format(self.loaded))
            self.loadcount -= self.progress_mark
    
    def parsed_road(self, *parts):
        if self.has_new_data():
            self.road_outfile.writelines(parts)
            self.roads_loaded += 1
        self.add_progress(1)
        
    def parsed_intersection(self, nid, x, y):
        """Called when a new intersection has been parsed"""
        if self.has_new_data():
            self.inter_outfile.write("{0},{1},{2}\n".format(nid, x, y))
            self.inters_loaded += 1
        self.add_progress(1)

def parse_files(source_file, progress_file, inter_outfile, road_outfile):
    with open(inter_outfile, "a") as iout, open(road_outfile,  "a") as rout:
        progress = 0
        if os.path.exists(progress_file):
            print("Loading progress from '{0}'...".format(progress_file))
            with open(progress_file, "r") as p:
                text = p.read().strip()
                if text.isalnum():
                    progress = int(text)
        
        with open(progress_file, "w") as pout:
            parser = xml.sax.make_parser()
            handler = OSMHandler(iout, rout, pout, progress)
            print("Starting parse with {0} previously loaded objects".format(handler.prev_loaded))
            parser.setContentHandler(handler)
            try:
                parser.parse(source_file)
            except KeyboardInterrupt:
                print("Interrupted!")
        
            print("Loaded {0} roads and {1} intersections".format(
                handler.roads_loaded,
                handler.inters_loaded
            ))

def clear_files(*files):
    print("Clearing files...")
    for fname in files:
        if os.path.exists(fname): 
            os.remove(fname)

def main():
    filename        = "denmark-latest.osm"
    progressfile    = "progress.txt"
    inter_outfile   = "osm_intersections.txt"
    road_outfile    = "osm_roads.txt"
    clear           = 0
    
    parse_files(filename, progressfile, inter_outfile, road_outfile)
   
    
if __name__ == '__main__':
    main()
    #print(RoadPart(100, 1000, name="Møllevej, dumt, navn", oneway="yes", maxspeed=40, highway="primary"))
    # 546404,546334,6,Christiansø,0,0,0,0,,,,,3740,3740,0,0,30,0.103,,,
