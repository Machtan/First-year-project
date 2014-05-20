# coding: utf-8
# Created by Jakob Lautrup Nysom @ May 14th 2014
import codecs
import sys
import os
from roads import Road, Inter, Loader
from dbtool import IntersectionDB
from argparse import ArgumentParser

def create_intersection_database(interfile, dbname, cachelimit=200):
    """Creates a database with all the intersections from the given file"""
    print("Preparing intersection database...")
    
    db = IntersectionDB(dbname)
    total = 0
    inters = []
    
    with open(interfile, "r") as infile:
        for inter in infile:
            inters.append(Inter(*inter.split(",")))
            if len(inters) == cachelimit:
                total += cachelimit
                db.add(*inters)
                inters = []
                print("Added {0} intersections...".format(total))
        total += cachelimit
        db.add(*inters)
        inters = []
        print("Added {0} intersections!".format(total))
        
    print("Finished creating the database!")
    return db

#"546320,546068,8,,0,0,0,0,,,,,0,0,0,0,30,0.135,n,,"
def create_unfinished_road(line):
    """Creates an unfinished road from the given line of text"""
    args        = line.split(",")
    nodes       = args[0:2]
    roadtype    = args[2]
    name        = args[3]
    oneway      = args[-3]
    zipcode     = args[13]
    speedlimit  = args[-5]
    drivetimes  = [args[-4]]
    if oneway == "tf":      # To -> From 
        oneway  = True
        nodes.reverse()
    elif oneway == "ft":    # From -> To
        oneway  = True
    elif oneway == "n":
        roadtype = 0        # Unclassified
        oneway = False
    else:
        oneway = False
    return Road(name, roadtype, zipcode, speedlimit, oneway, nodes, drivetimes)

def convert_krak(interfile, roadpartfile, outfile, rebuild_database, encoding="Latin-1", 
        interdbfile="inters.db", saveformat="text", progress_mark=1000):
    """Converts the given krak data to a file in the new format"""
    if not os.path.exists(interdbfile) or rebuild_database:
        create_intersection_database(interfile, interdbfile)
    db = IntersectionDB(interdbfile)
    print("Converting roads...")
    with codecs.open(roadpartfile, "r", encoding=encoding) as parts, open(outfile, "w") as out:
        for num, part in enumerate(parts, 1):
            road = create_unfinished_road(part)
            nodes = []
            for node in road.nodes:
                nodes.append(db.get(node))
            road.nodes = nodes
            Loader.save(road, out, saveformat)
            if (num % progress_mark) == 0:
                print("- Converted {0}...".format(num))
                
        print("- Converted {0}!".format(num))
    print("Finished Converting!")
    print("Saved output to '{0}'".format(outfile))

def main():
    parser = ArgumentParser()
    parser.add_argument("intersection_file", help="The file to load krak intersections (in group F's format) from")
    parser.add_argument("road_part_file", help="The file to load krak road parts (in group F's format) from")
    parser.add_argument("outfile", help="The file to save the roads in the new format to")
    parser.add_argument("--rebuild_database", "-r", action="store_true", default=False, 
        help="Whether the intersection database should be rebuilt")
    args = parser.parse_args()
    convert_krak(args.intersection_file, args.road_part_file, args.outfile, 
        args.rebuild_database)
    
if __name__ == '__main__':
    #sys.argv += ["intersections.txt", "roads.txt", "new_krak_roads.txt"]
    main()