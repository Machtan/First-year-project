# coding: utf-8
# Created by Jakob Lautrup Nysom @ May 16th 2014
"""
Datum 	        Equatorial          Polar Radius,       Flattening          Use
                Radius, meters (a) 	meters (b) 	        (a-b)/a 	
NAD83/WGS84 	6,378,137 	        6,356,752.3142 	    1/298.257223563 	Global

Central meridians:
32: 9°, 33: 15°
"""

import os, math #, utm
from roads import Inter

def merc_x(lon):
  r_major=6378137.000
  return r_major*math.radians(lon)
 
def merc_y(lat):
  if lat>89.5:lat=89.5
  if lat<-89.5:lat=-89.5
  r_major=6378137.000
  r_minor=6356752.3142
  temp=r_minor/r_major
  eccent=math.sqrt(1-temp**2)
  phi=math.radians(lat)
  sinphi=math.sin(phi)
  con=eccent*sinphi
  com=eccent/2
  con=((1.0-con)/(1.0+con))**com
  ts=math.tan((math.pi/2-phi)/2)/con
  y=0-r_major*math.log(ts)
  return y

print(os.getcwd())
sepchar = "@"
zones = dict()
def convert_nodes(road_file, out_file, progress_mark=1000):
    if road_file == out_file:
        raise Exception("road_file and out_file should not be the same!")
    print("Starting conversion...")
    with open(road_file, "r") as f, open(out_file, "w") as out:
        for num, line in enumerate(f, 1):
            meta, nodestring, drivetimes = line.split(sepchar)
            args = nodestring.split(",")
            nodes = []
            while len(args):
                nid = args.pop(0)
                lat = float(args.pop(0))
                lon = float(args.pop(0))
                if (not lon) or (not lat):
                    print("Bad args(lat/lon) {0} for node '{1}'".format((lat, lon), nid))
                
                #x, y, zone, t = utm.from_latlon(lat, lon)
                x, y, zone = merc_x(lon), merc_y(lat), 0
                if zone not in zones:
                    zones[zone] = {
                        "minX":1000000000, 
                        "minY":1000000000, 
                        "maxX":-1000000000, 
                        "maxY":-1000000000
                    }
                
                #print("{0} -> {1} zone {2}, t {3}".format((lat,lon), (x, y), zone, t))
                zones[zone]['minX'] = min(zones[zone]['minX'], x)
                zones[zone]['maxX'] = max(zones[zone]['maxX'], x)
                zones[zone]['minY'] = min(zones[zone]['minY'], y)
                zones[zone]['maxY'] = max(zones[zone]['maxY'], y)
                nodes += [nid, str(x), str(y)]
            newline = meta+sepchar+",".join(nodes)+sepchar+drivetimes
            out.write(newline)
            #print(newline)
            if (num % progress_mark) == 0:
                print("Converted {0} roads...".format(num))
        print("Converted {0} roads!".format(num))
        print("Bounds:")
        for zone, val in zones.items():
            print(zone, val)
    print("Saved the data to '{0}'".format(out_file))

# 1021175 1012107

"""
Møllehusene,0,0,50,0@571329,55.6345147,12.0729268,571328,55.634588,12.0722614,571327,55.6347107,12.0711266,545676,55.6346395,12.0706047@
Møllehusvej,0,0,50,0@546419,55.6390839,12.0686269,1015329269,55.6401221,12.068336,574928,55.6414063,12.0680057,570285,55.6424488,12.0677227,331345585,55.6431584,12.0674934,570284,55.6439174,12.0672676,570283,55.6450367,12.0669402,2122682936,55.6461209,12.0666271,1755113164,55.6465074,12.0665153,1296754626,55.646901,12.0664058,570288,55.6474838,12.0663322,2619629970,55.6475776,12.0663302,482455446,55.6476466,12.0663288,570289,55.6485489,12.0664006,570290,55.6498052,12.0664829@
"""

def main():
    file = "new_osm_roads.txt"
    #[x: 150955.1544851401 : 695916.7463075386] [y: 5838350.401229404 : 6877073.128247903]
    outfile = "converted_osm_roads.txt"
    
    # Helenevej,0,0,50,0@781055:    55.680145,12.5431602,                   792028: 55.6797419,12.5450247@
    # Helenevej,0,0,50,0@781055:    356604.29808851774,1386975.1433530462,  792028: 356561.5285135582,1387181.5934033373@
    lat, lon = 55.680145,12.5431602
    
    x,y,zone,t = utm.from_latlon(lat, lon)
    print("utm:    {0}".format((x,y)))
    x, y = merc_x(lon), merc_y(lat)
    print("osm: {0}".format((x,y)))
    
    #file = "osm_test_roads.txt"
    #outfile = "converted_test_roads.txt"
    convert_nodes(file, outfile)
    
    
    
            

if __name__ == '__main__':
    main()
