package krak;

import classes.Utils;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;

/**
 * Parse Krak data files (kdv_node_unload.txt, kdv_unload.txt).
 *
 * Customize to your needs by overriding processNode and processEdge. 
 * See example in main. 
 *
 * Original author Peter Tiedemann petert@itu.dk; 
 * updates (2014) by Søren Debois, debois@itu.dk
 */
public class KrakLoader {

    /**
     * Loads a set of edges from a krak-formatted input file and returns them.
     * This throws a RuntimeException if the file cannot be read.
     * @param edgeFilePath The path to the krak edge info file.
     * @return A list of the constructed edges
     */
    public static EdgeData[] loadEdges(String edgeFilePath) {
        System.out.println("Loading edge data...");
        ArrayList<EdgeData> edges = new ArrayList<>();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(Utils.getFile(edgeFilePath)));
            br.readLine(); // Again, first line is column names, not data.
        
            String line;
            while ((line = br.readLine()) != null) {
                edges.add(new EdgeData(line));
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load edge data from '"+edgeFilePath+"'");
        }

        DataLine.resetInterner();
        System.gc();
        System.out.println("Edge data loaded!");
        return edges.toArray(new EdgeData[0]);
    }
    
    /**
     * Loads a set of nodes from a krak-formatted input file and returns them.
     * This throws a RuntimeException if the file cannot be read.
     * @param nodeFilePath The path to the krak node info file.
     * @return A list of the constructed nodes
     */
    public static NodeData[] loadNodes(String nodeFilePath) {
        System.out.println("Loading node data...");
        ArrayList<NodeData> nodes = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(Utils.getFile(nodeFilePath)));
            br.readLine(); // First line is column names, not data.

            String line;
            while ((line = br.readLine()) != null) {
                nodes.add(new NodeData(line));
            }
            br.close();
        } catch (IOException ex) {
            throw new RuntimeException("Could not load node data from '"+nodeFilePath+"'");
        }
        DataLine.resetInterner();
        System.gc();
        System.out.println("Node data loaded!");
        return nodes.toArray(new NodeData[0]);
    }

    /**
     * Example usage. You may need to adjust the java heap-size, i.e., -Xmx256M
     * on the command-line.
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Testing Krak Loader....");
        // If your machine slows to a crawl doing inputting, try
        // uncommenting this. 
        // Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        // Invoke the loader class.
        EdgeData[] edges = KrakLoader.loadEdges("krak/kdv_unload.txt");
        NodeData[] nodes = KrakLoader.loadNodes("krak/kdv_node_unload.txt");

        // Check the results.
        System.out.printf("Loaded %d nodes, %d edges\n",
                nodes.length, edges.length);
        MemoryMXBean mxbean = ManagementFactory.getMemoryMXBean();
        System.out.printf("Heap memory usage: %d MB%n",
                mxbean.getHeapMemoryUsage().getUsed() / (1000000));
    }
}
