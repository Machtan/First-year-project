package classes;

import java.lang.reflect.Array;

/**
 * The FastArList class is a simpler and thus hopefully faster array-based list
 * (technically a Bag, but... whatever ;)
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 27-Mar-2014
 */
public class FastArList<T> {
    
    private T[] arr;
    private int N;
    /**
     * Constructor for the FastArList class
     * @param initialSize The initial size of the list (use if you know it ;)
     */
    public FastArList (int initialSize) {
        if (initialSize < 1) { throw new RuntimeException("BAD INITIAL SIZE!");} 
        int size = 1; // Ensure that size is a factor of two :)
        while (size < initialSize) {
            size *= 2;
        }
        arr = (T[]) new Object[size];
        N = 0;
    }
    
    /**
     * Constructor for the FastArList class
     */
    public FastArList () {
        this(1);
    }
    
    /**
     * Returns a resized version of the given array
     * @param arr The array
     * @param newSize Its new size
     * @return The components of the given array in an array with the new size
     */
    private T[] resized(T[] arr, int newSize) {
        T[] newArr = (T[])new Object[newSize];
        int iterations;
        if (newSize > arr.length) { // Make it larger
            iterations = arr.length;
        } else { // Make it smaller
            iterations = newSize;
        }
        for (int i = 0; i < iterations; i++) {
            newArr[i] = arr[i];
        }
        return newArr;
    }
    
    /**
     * Resizes the list to the given size
     * @param newSize The new size of the list
     */
    private void resize(int newSize) {
        arr = resized(arr, newSize);
    }
    
    /**
     * Adds a single item to the list
     * @param item The item to add
     */
    public void add(T item) {
        if (N == arr.length) {
            resize(arr.length*2);
        }
        arr[N++] = item;
    }
    
    /**
     * Adds all elements from the given array to this list
     * @param items The array to add from
     */
    public void addAll(T[] items) {
        int size = arr.length; // Resize to fit the new items if needed
        while (size < (N+items.length)) {
            size *= 2;
        }
        if (size != arr.length) {
            resize(size);
        }
        for (int i = 0; i < items.length; i++) {
            arr[N++] = items[i];
        }
    }
    
    /**
     * Returns the length of the list
     * @return the length of the list
     */
    public int size() {
        return N;
    }
    
    /**
     * Returns a copy of the list as an array
     * @return a copy of the list as an array
     */
    public <E> E[] toArray(E[] inArr) {
        if (inArr.length < N) {
            inArr = (E[]) Array.newInstance(inArr.getClass(), N);
        }
        for (int i = 0; i < N; i++) {
            inArr[i] = (E)arr[i];
        }
        return inArr;
    }
    
    public static void main(String[] args) {
        FastArList<Integer> list = new FastArList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        Integer[] ins = list.toArray(new Integer[3]);
        /*int[] ints = new int[list.size()];
        Object[] arr = list.toArray();
        for (int i = 0; i< list.size(); i++) {
            ints[i] = (int)arr[i];
        }*/
        System.out.println("ints: "+ins);
    }
}
