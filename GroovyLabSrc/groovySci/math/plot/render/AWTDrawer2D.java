package groovySci.math.plot.render;

import groovySci.math.plot.canvas.*;

public class AWTDrawer2D extends AWTDrawer {

	public AWTDrawer2D(PlotCanvas _canvas) {
		super(_canvas);
		projection = new Projection2D(this);
	}

}
