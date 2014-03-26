package interfaces;

/**
 * The IProgressBar interface <More docs goes here>
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 26-Mar-2014
 */
public interface IProgressBar {
    public void update(int addition);
    public void setTarget(String text, int target);
    public void close();
}
