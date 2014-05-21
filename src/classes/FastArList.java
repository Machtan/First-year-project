package classes;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The FastArList class is a simpler and thus hopefully faster array-based list
 * (technically a Bag, but... whatever ;)
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 27-Mar-2014
 */
public class FastArList<T> implements Iterable<T>{
    
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
        System.arraycopy(arr, 0, newArr, 0, N);
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
        System.arraycopy(items, 0, arr, N, items.length);
        N += items.length;
    }
    
    public void addAll(FastArList list) {
        addAll((T[])list.arr);
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
        E[] result = inArr;
        if (inArr.length < N) {
            return (E[]) Arrays.copyOf(arr, N, inArr.getClass()); // Gotten from java.util.ArrayList
        }
        System.arraycopy(arr, 0, result, 0, N);
        return result;
    }
    
    public static void main(String[] args) {
        FastArList<Integer> list = new FastArList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.addAll(new Integer[]{4, 5, 6});
        Integer[] ins = list.toArray(new Integer[5]);
        //System.out.println("ints: "+ins);
        for (Integer i : ins) {
            //System.out.println("- "+i);
        }
        /*int[] ints = new int[list.size()];
        Object[] arr = list.toArray();
        for (int i = 0; i< list.size(); i++) {
            ints[i] = (int)arr[i];
        }*/
    }
    
    private class ArIter implements Iterator<T> {
        int index;
        public ArIter() {
            index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < FastArList.this.N;
        }

        @Override
        public T next() {
            return FastArList.this.arr[index++]; // Unsafe :u
        }

        @Override
        public void remove() {throw new UnsupportedOperationException("Removal not supported");}
    }

    @Override
    public Iterator<T> iterator() {
        return new ArIter();
    }
}
