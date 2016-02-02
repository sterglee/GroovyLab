
package groovySci.math.plot.plotObjects;

import java.awt.*;
import java.io.*;

import javax.swing.*;

import groovySci.math.plot.*;
import groovySci.math.plot.render.*;

public class RasterImage implements Plotable{

    File source;
    Image img;
    double[] xyzSW, xyzSE,xyzNW;
	
    boolean visible = true;
    float alpha;
	
    public RasterImage(File _source, float _alpha, double[] _xyzSW, double[] _xyzSE, double[] _xyzNW) {
        source = _source;
        img =  Toolkit.getDefaultToolkit().getImage(source.getPath());
        xyzSW = _xyzSW;
        xyzSE = _xyzSE;
        xyzNW=_xyzNW;
        alpha = _alpha;
}
	
    @Override
    public void plot(AbstractDrawer draw) {
        if (!visible) return;
        draw.drawImage(img,alpha, xyzSW, xyzSE,xyzNW);
}

    @Override
    public void setVisible(boolean v) {
        visible = v;
}

    @Override
    public boolean getVisible() {
        return visible;
}
	
    @Override
    public void setColor(Color c) {
        throw new IllegalArgumentException("method not available for this Object: PlotImage");
}

    @Override
    public Color getColor() {
        return null;
}

}
