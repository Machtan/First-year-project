package classes;

/**
 * The UTFLoader class is a variant of our general loader that handles text
 * files in the utf-8 encoding, instead of Western Latin-1.
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 28-Apr-2014
 */
public class UTFLoader extends Loader{
    // Overrides the static encoding parameter
    String encoding = "utf-8";
}
