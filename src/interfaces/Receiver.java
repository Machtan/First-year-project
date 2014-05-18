package interfaces;

/**
 * The Receiver interface is an interface for asynchronously receiving 
 * objects. The streaming methodology makes this very useful and/or necessary.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 17-May-2014
 */
public interface Receiver<T> {
    void receive(T obj);
}
