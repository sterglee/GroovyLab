
package groovySci.math.plot.plots;

import groovySci.math.plot.render.*;

public abstract class LayerPlot extends Plot {
    Plot plot;
    public LayerPlot(String name, Plot p) {
        super(name, p.color);
        plot = p;
    }

    public double[] isSelected(int[] screenCoordTest, AbstractDrawer draw) {
        return null;
    }
}
