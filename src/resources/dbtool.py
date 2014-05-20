# coding: utf-8
# Created by Jabok @ May 12th 2014
import sqlite3, os
from roads import Road, Inter
"""
c.execute('''CREATE TABLE stocks
             (date text, trans text, symbol text, qty real, price real)''')

c.execute("INSERT INTO stocks VALUES ('2006-01-05','BUY','RHAT',100,35.14)")
"""
# from http://code.activestate.com/recipes/137270-use-generators-for-fetching-large-db-record-sets/
def ResultIter(cursor, arraysize=1000):
    'An iterator that uses fetchmany to keep memory usage down'
    while True:
        results = cursor.fetchmany(arraysize)
        if not results:
            break
        for result in results:
            yield result

class IntersectionDB:
    def __init__(self, dbname, table="intersections"):
        self.dbname = dbname
        self.table = table
        exists = os.path.exists(dbname)
        self.conn = sqlite3.connect(dbname)
        self.cursor = self.conn.cursor()
        if not exists:
            print("Creating table...")
            self.prepare_table()
            print("Created!")

    def prepare_table(self):
        statement = '''CREATE TABLE {0} (id unsigned big int unique primary key, x float, y float)'''.format(self.table)
        self.cursor.execute(statement)

    def add(self, *inters, commit=True):
        if not len(inters): return
        ending = ("(?,?,?),"*len(inters))[:-1]
        statement = "insert or ignore into {0} values {1}".format(self.table, ending)
        vals = tuple()
        for inter in inters:
            vals += (inter.nid, inter.x, inter.y)
        self.cursor.execute(statement, vals)
        if commit:
            self.conn.commit()
    
    def get(self, nid):
        statement = "SELECT x, y FROM {0} WHERE id=?".format(self.table)
        self.cursor.execute(statement, (nid,))
        res = self.cursor.fetchone()
        if not res:
            print("Found no result for the node ID '{0}'".format(nid))
            return Inter(nid, 0, 0)
        else:
            return Inter(nid, *res)
    
    def close(self):
        self.conn.close()
    
    def destroy(self):
        self.close()
        shutil.remove(self.dbname)
    
    def __iter__(self):
        stmt = "select * from {0}".format(self.table)
        self.cursor.execute(stmt)
        for res in ResultIter(self.cursor):
            yield Inter(*res)

class RoadDB:
    def __init__(self, dbname, table="roads"):
        self.dbname = dbname
        self.table  = table
        exists      = os.path.exists(dbname)
        self.conn   = sqlite3.connect(dbname)
        self.cursor = self.conn.cursor()
        if not exists:
            print("Creating table...")
            self.prepare()
            print("Created!")
    
    def prepare(self):
        stmt = '''CREATE TABLE {0} (name text, type smallint, zipcode smallint, speedlimit int, oneway boolean, nodes text)'''.format(self.table)
        self.cursor.execute(stmt)
    
    def add(self, *roads, commit=True):
        if not len(roads): return
        ending = ("(?,?,?,?,?,?),"*len(roads))[:-1]
        stmt = "insert into {0} values {1}".format(self.table, ending)
        vals = tuple()
        for road in roads:
            vals += (road.name, road.type, road.zip, road.speedlimit, road.oneway, ",".join([str(node) for node in road.nodes]))
        self.cursor.execute(stmt, vals)
        if commit:
            self.conn.commit()
    
    def __iter__(self):
        stmt = "select * from {0}".format(self.table)
        self.cursor.execute(stmt)
        return ResultIter(self.cursor)
    
    def complete_roads(self, interdb):
        """WARNING: This is a generator which creates finished roads by 
        combining the roads of this database with intersections"""
        for road_args in self:
            final = Road(*road_args)
            nodes = []
            for node in final.nodes:
                nodes.append(interdb.get(node))
            final.nodes = nodes
            yield final
    
    def close(self):
        """Closes the database connection"""
        self.conn.close()
    
class RoadPartDB:
    """This is for the categorization of Krak road parts, as a temporary 
    step before converting them to actual roads"""
    
    def exists(self, roadname):
        """Whether the given road name exists in the database"""
        stmt = '''SELECT EXISTS(SELECT 1 FROM {0} WHERE name="{1}" LIMIT 1)'''.format(self.table, roadname)
        self.cursor.execute(stmt)
        val = self.cursor.fetchone()
        print("'{0}' {1}!".format(roadname, "exists" if val else "doesn't exist"))
        return val
    
    def append_roadpart(self, roadpart):
        edge = (roadpart.p1.id, roadpart.p2.id)
        stmt = '''update {0} set edges = edges || ";{1}" where name={2}'''.format(self.table, edge, roadpart.name)
    
    def add_roadpart(self, *roadparts):
        """Adds a road part to the database, classifying it properly"""
        for roadpart in roadparts: pass

def test_inter_db():
    dbname      = "inters.db"
    tablename   = "intersections"
    
    db = IntersectionDB(dbname, tablename)
    i = Inter(1000000, 100.32032030, 1119232.2345)
    j = Inter(2000000, 100.23032030, 1119232.2345)
    db.add(i, j, commit=False)
    
    inter   = db.get(1000000)
    inter2  = db.get(2000000)
    print("i =", i)
    print("inter =", inter)
    print("inter2=", inter2)
    
    print("inters:")
    for num, i in enumerate(db):
        if num == 100: break
        print("- {0}".format(i))
    db.close()

def test_road_db():
    dbname      = "roads.db"
    tablename   = "roads"
    
    db = RoadDB(dbname, tablename)
    r1 = Road("A-Vej", 10, 2090, 60, True, [1,2,3,4,5])
    r2 = Road("B-Vej", 11, 2091, 61, False, [11,12,13,14,15])
    r3 = Road("C-Vej", 12, 2092, 62, True, [21,22,23,24,25])
    print("Adding roads...")
    db.add(r1, r2, r3, commit=False)
    limit = 100
    test = tuple()
    for i in range(limit): test += (r1,)
    db.add(*test, commit=False)
    print("Added!")
    
    print("Roads")
    for road in db:
        print(road)
    
    interdb = IntersectionDB("inters.db", "intersections")
    print("Complete roads:")
    for road in db.complete_roads(interdb):
        print(road)
    
    db.close()

def main():
    #test_inter_db()
    test_road_db()
        
    

if __name__ == '__main__':
    main()
