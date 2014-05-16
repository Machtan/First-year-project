package interfaces;

/**
 * The StreamedContainer interface defines the interface of a class, which can
 * load data from streamedly, thus reducing RAM overhead.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 13-May-2014
 */
public interface StreamedContainer<T> {
    public void startStream();  // Called when the stream starts
    public void startStream(IProgressBar bar);
    public void add(T obj);     // Called every time an object is added
    public void endStream();    // Called when the stream ends
}
