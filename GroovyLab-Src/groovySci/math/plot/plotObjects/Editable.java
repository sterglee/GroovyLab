package groovySci.math.plot.plotObjects;

import groovySci.math.plot.render.*;


public interface Editable {
    public double[] isSelected(int[] screenCoord, AbstractDrawer draw);
    public void edit(Object editParent);
    public void editnote(AbstractDrawer draw);
}
