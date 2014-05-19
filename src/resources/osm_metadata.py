# coding: utf-8
# Created by Jakob Lautrup Nysom @ May 14th 2014
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

class Converter:
    """A class for converting some of the OSM values to the Krak values recognized by the application"""
    
    def get_name(kwargs):
        """Returns the name of the road with the given metadata"""
        name = kwargs.get("name", "")
        if "," in name: # Fix that shit :u
            splat = name.split(",")
            name = splat[0] + "({0})".format("|".join(s.strip() for s in splat[1:]))
        return name
    
    def get_type(kwargs):
        """Returns the krak road type from an OSM road type"""
        roadtype = kwargs.get("highway","other")
        if "route" in kwargs:
            if kwargs["route"] == "ferry":
                return roadtypes['ferry']
        return roadtypes.get(roadtype, 0)
    
    def get_speedlimit(kwargs):
        """Returns the speed limit from the given limit and optional road type"""
        limit = speedlimits["default"]
        if "maxspeed" in kwargs:
            speed = kwargs["maxspeed"].split(" ")[0]
            try:
                limit = int(speed)
            except ValueError as e: 
                roadtype = kwargs.get("roadtype", "")
                if speed in speedlimits:
                    limit = speedlimits[speed]
                elif roadtype in roadspeeds:
                    limit = roadspeeds[roadtype]
        return limit
    
    def get_oneway(kwargs):
        """Returns whether the road with the given metadata is oneway"""
        return kwargs.get("oneway","") == "yes"
    
    