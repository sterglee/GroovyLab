package groovySci.math.plot.plotObjects;

import groovySci.math.plot.render.*;


public interface Noteable {
    public double[] isSelected(int[] screenCoord, AbstractDrawer draw);
    public void note(AbstractDrawer draw);
}