# coding: utf-8
# Created by Jakob Lautrup Nysom @ May 14th 2014
import os
import sys
import xml.sax
from dbtool import IntersectionDB, Inter, RoadDB, Road
from roads import Loader
from osm_metadata import Converter
from projection_helper import merc_x, merc_y
from argparse import ArgumentParser

class OSMWay:
    def __init__(self):
        self.attributes = {}
        self.nodes      = []
    
    def add_attribute(self, key, value):
        self.attributes[key] = value
    
    def add_node(self, node):
        self.nodes.append(node)
    
    def create_unfinished_road(self):
        """Creates an unfinished road from the OSM way"""
        roadname = Converter.get_name(self.attributes)
        roadtype = Converter.get_type(self.attributes)
        roadzip  = 0 # This needs to be found in some nodes, sometime
        limit    = Converter.get_speedlimit(self.attributes)
        oneway   = Converter.get_oneway(self.attributes)
        return Road(roadname, roadtype, roadzip, limit, oneway, self.nodes)

class ParseEndException(Exception):
    def __init__(self, msg):
        super().__init__(msg)

def is_valid(road):
    if "highway" in road.attributes:
        return True
    if "route" in road.attributes:
        if road.attributes["route"] == "ferry":
            return True
    return False

class OSMHandler(xml.sax.ContentHandler):
    def __init__(self, interdbname, roaddbname, add_nodes=True, add_roads=True, node_limit=300, road_limit=100, limit=-1, progress_mark=3000):
        self.interdb    = IntersectionDB(interdbname)
        self.roaddb     = RoadDB(roaddbname)
        self.parsed     = 0
        self.limit      = limit
        self.nodes      = [] # Used for storing nodes before transferring
        self.roads      = [] # Used for storing nodes before transferring
        self.add_nodes  = add_nodes
        self.add_roads  = add_roads
        self.road_limit = road_limit
        self.node_limit = node_limit
        self.progress_mark  = progress_mark
        self.current_road   = None    
    
    def parsed_node(self, node):
        """Called when a node has been parsed"""
        self.nodes.append(node)
        if len(self.nodes) >= self.node_limit:
            self.interdb.add(*self.nodes)
            self.nodes = []
            print("Sent {0} nodes to '{1}' ({2})".format(self.node_limit, 
                self.interdb.dbname, self.parsed))
    
    def parsed_road(self, road):
        """Called when a road has been parsed"""
        self.roads.append(road)
        if len(self.roads) >= self.road_limit:
            self.roaddb.add(*self.roads)
            self.roads = []
            print("Sent {0} roads to '{1}' ({2})".format(self.road_limit, 
                self.roaddb.dbname, self.parsed))
    
    def parsed_one(self):
        """Jus a limiter"""
        self.parsed += 1
        if self.parsed == self.limit:
            raise ParseEndException("Parsed the limit of '{0}' objects".format(limit))
        if (self.parsed  % self.progress_mark) == 0:
            print("Parsed {0} entities...".format(self.parsed))
    
    def startElement(self, name, attrs):
        """Callback at the beginning of a parsed tag"""
        if name == "way": 
            self.current_road = OSMWay()
            
        elif name == "nd": 
            # Member node
            if self.current_road:
                self.current_road.add_node(attrs["ref"])
            
        elif name == "tag": 
            # A member tag (extra data on 'anything')
            if self.current_road: # Of a road
                self.current_road.add_attribute(attrs["k"], attrs["v"])
        
        elif name == "node": 
            # A node (intersection)
            if self.add_nodes:
                if "lat" in attrs:
                    self.parsed_node(Inter(attrs["id"], attrs["lon"], attrs["lat"]))
            self.parsed_one()
            
    def endElement(self, name):
        """Callback at the end of a parsed tag"""
        if name == "way":
            # Only add it if it actually has nodes in it
            if self.current_road:
                if len(self.current_road.nodes):
                    if self.add_roads and is_valid(self.current_road):
                        self.parsed_road(self.current_road.create_unfinished_road())
                self.current_road = None
            self.parsed_one()
    
    def endDocument(self):
        """Callback once parsing finishes"""
        print("Wrapping up...")
        # Send the remaining nodes
        if len(self.nodes): 
            self.interdb.add(*self.nodes)
            print("Sent {0} nodes to '{1}".format(len(self.nodes), self.interdb.dbname))
            self.nodes = []
            
        # Send the remaning roads
        if len(self.roads):
            self.roaddb.add(*self.roads)
            print("Sent {0} roads to '{1}".format(len(self.roads), self.roaddb.dbname))
            self.roads = []
        
        # Clean up :3
        self.roaddb.close()
        self.interdb.close()
        print("Sent all remaining entities")
    
def create_databases(osmfilename, interdbname, roaddbname):
    """Creates databases of roads and intersections from the given OSM file
    and target names"""
    print("Creating databases...")
    try:
        if os.stat(interdbname).st_size == 689635328: # The finished size
            print("The intersection DB is fine, skipping its creation...")
            add_nodes = False
        else:
            add_nodes = True
    except FileNotFoundError:
        add_nodes = True
        
    parser = xml.sax.make_parser()
    handler = OSMHandler(interdbname, roaddbname, add_nodes=add_nodes)
    parser.setContentHandler(handler)
    try:
        parser.parse(osmfilename)
    except KeyboardInterrupt:
        print("Interrupted!")
        print("Removing the unfinished database files...")
        os.remove(interdbname)
        os.remove(roaddbname)
    print("Finished creating databases!")
    
def save_roads(interdbname, roaddbname, outfilename, progress_mark=25):
    """Saves the roads from the given databases to the out file"""
    print("Converting roads...")
    interdb = IntersectionDB(interdbname)   
    roaddb = RoadDB(roaddbname)
    with open(outfilename, "w") as out:
        num = 0
        for num, road in enumerate(roaddb.complete_roads(interdb), 1):
            Loader.save(road, out, "text")
            if (num % progress_mark) == 0:
                print("- Converted {0}...".format(num))
        print("- Converted {0}!".format(num))
        
    print("Finished Converting!")
    print("Saved output to '{0}'".format(outfilename))

def convert_osm(osmfilename, outfilename, interdbname, roaddbname, 
        clear_databases):
    """Attempts to convert the data from the given OpenStreetMap XML file to the 
    format recognized the First-Year-Project application of group F"""
    if (not clear_databases) and os.path.exists(interdbname) and os.path.exists(roaddbname):
        print("Reusing existing databases...")
    else:
        create_databases(osmfilename, interdbname, roaddbname)
    
    save_roads(interdbname, roaddbname, outfilename)


def main():
    parser = ArgumentParser()
    parser.add_argument("osm_file_name", help="The OSM XML file to convert")
    parser.add_argument("out_file_name", help="The file to save the converted roads to")
    parser.add_argument("intersection_database_name", 
        help="The name of the database to store the intersections in, in the intermediate step")
    parser.add_argument("road_database_name", 
        help="The name of the database to store the roads in, in the intermediate step")
    parser.add_argument("--clear_databases", "-c", action="store_true", default=False, 
        help="""Whether the program should clear and rebuild the databases. Default: False""")
    args = parser.parse_args()
    convert_osm(args.osm_file_name, args.out_file_name, 
        args.intersection_database_name, args.road_database_name, 
        args.clear_databases)

if __name__ == '__main__':
    osmfilename = "denmark-latest.osm"
    outfilename = "osm_roads.txt"
    interdbname = "osm_inters.db"
    roaddbname  = "osm_roads.db"
    
    #osmfilename = "map.osm"
    #outfilename = "gladsaxe.txt"
    
    sys.argv += [osmfilename, outfilename, interdbname, roaddbname, "-c"]
    main()
