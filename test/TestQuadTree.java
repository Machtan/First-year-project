/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import classes.QuadTree;
import classes.Rect;
import classes.Road;
import interfaces.IProgressBar;
import interfaces.QuadNode;
import interfaces.StreamedContainer;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author jakoblautrupnysom
 */
public class TestQuadTree {
    
    public TestQuadTree() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    private static class TestNode extends Rect implements QuadNode {
        public TestNode(float x, float y, float w, float h) {
            super(x, y, w, h);
        }
    }
    
    private static class ResultHelper<T> implements StreamedContainer<T>{
        public ArrayList<T> results = new ArrayList<>();;
        @Override
        public void startStream() {}
        @Override
        public void startStream(IProgressBar bar) {}
        @Override
        public void add(T obj) {
            results.add(obj);
        }
        @Override
        public void endStream() {}
    }
    
    public QuadTree A;
    public QuadTree B;
    public QuadTree C;
    public QuadTree D;
    public QuadTree E;
    public QuadTree F;
    @Before
    public void setUp() {
        A = new QuadTree(new Rect(0,0,10,10),(short)10,(short)10);
        
        B = new QuadTree(new Rect(0,0,10,10),(short)1,(short)2);
        for (int i = 0; i < 10; i++) {
            B.add(new TestNode(0,0,1,1)); // SW
        }
        B.add(new TestNode(4,0,2,10)); // Inside the vertical delimiter
        
        C = new QuadTree(new Rect(0,0,10,10),(short)10,(short)10);
        for (int j = 0; j < 2; j++) {
            C.add(new TestNode(0,0,1,1)); // SW
        }
        C.add(new TestNode(4,0,2,10)); // Vertical delimiter
        C.add(new TestNode(0,4,10,2)); // Horizontal delimiter
        
        D = new QuadTree(new Rect(0,0,10,10),(short)10,(short)10);
        D.add(new TestNode(4,0,2,10)); // Vertical delimiter
        
        E = new QuadTree(new Rect(0,0,10,10),(short)10,(short)10);
        E.add(new TestNode(0,0,10,10));
        
        F = new QuadTree(new Rect(0,0,10,10),(short)1,(short)2);
        for (int k = 0; k < 2; k++) {
            F.add(new TestNode(0,0,1,1)); // SW
        }
    }
    
    /* == C ==
    2 keys in edgeCases, where both elements collide with the quad's area, 
    but not with the given area. The quad is a bottom quad with 2 elements in 
    nodeList, none of the elements collide with the given area*/
    
    @After
    public void tearDown() {
    }
    
    /*
    No keys in edgeCases, bottom quad
    1 key in edgeCases, where r is the same as area, where we have  an element that collides with the given area, not the bottom quad, no nodes in NodeList
    2 keys in edgeCases, where both elements collide with area, where the element doesn't collide with the given area, a bottom quad, 2 elements in nodeList, none of the elements collide with the given area
    1 key in edgeCases, where r is different from area, and the quad is at the bottom, no elements in nodeList
    Quad is at the bottom, 1 element in nodeList, the node collides with the given area
    Not bottom, only one subquad collides with the area
    */

    @Test
    public void testA() {
        ResultHelper result = new ResultHelper();
        A.getIn(new Rect(0,0,10,10), result);
        assertEquals(result.results.size(), 0);
    }
    
    @Test
    public void testB() {
        ResultHelper result = new ResultHelper();
        B.getIn(new Rect(4,0,2,10), result);
        assertEquals(result.results.size(), 1);
    }
    
    @Test
    public void testC() {
        ResultHelper result = new ResultHelper();
        C.getIn(new Rect(9,9,1,1), result);
        assertEquals(result.results.size(), 0);
    }
    
    @Test
    public void testD() {
        ResultHelper result = new ResultHelper();
        D.getIn(new Rect(9,9,1,1), result);
        assertEquals(result.results.size(), 0);
    }
    
    @Test
    public void testE() {
        ResultHelper result = new ResultHelper();
        E.getIn(new Rect(9,9,1,1), result);
        assertEquals(result.results.size(), 1);
    }
    
    @Test
    public void testF() {
        ResultHelper result = new ResultHelper();
        F.getIn(new Rect(9,9,1,1), result);
        assertEquals(result.results.size(), 0);
    }
}
