package interfaces;

import classes.Datafile;
import classes.Model;

/**
 * ILoader is an interface for dataset loaders
 * @author Jakob Lautrup Nysom (jaln@itu.dk)
 * @version 12-Apr-2014
 */
public interface ILoader {
    public Model loadData(Datafile... files); // Loads a model from the given data files
    public Model loadData(IProgressBar bar, Datafile... files); // With a progress bar
}
