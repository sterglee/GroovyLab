package groovySci.math.plot.plots;

import java.awt.Color;

import javax.swing.JFrame;

import groovySci.math.plot.FrameView;
import groovySci.math.plot.Plot2DPanel;
import groovySci.math.plot.PlotPanel;
import groovySci.math.plot.render.AbstractDrawer;

public class CloudPlot2D extends Plot {
	double[][] NW,  NE,  SW,  SE;
	double[] width_constant = { -1, -1 };
	double[][] XY;
	float[] f;
	boolean fill_shape = true;

	public CloudPlot2D(String n, Color c, double[][] _XYcard, double wX, double wY) {
		super(n, c);
		splitXYf(_XYcard);
		width_constant = new double[] { wX, wY };

		build();
	}

	private void splitXYf(double[][] xycard) {
		XY = new double[xycard.length][2];
		f = new float[xycard.length];
		float normf = 0;
		for (int i = 0; i < xycard.length; i++) {
			XY[i][0] = xycard[i][0];
			XY[i][1] = xycard[i][1];
			f[i] = (float) xycard[i][2];
			normf += f[i];//Math.max(normf, f[i]);
		}
		for (int i = 0; i < f.length; i++) {
			f[i] = f[i] / normf;
	  }
	}

	private void build() {
		if (width_constant[0] > 0) {
            double widthConstant1 = width_constant[0]/2;
            double widthConstant2 = width_constant[1]/2;
			NW = new double[XY.length][];
			NE = new double[XY.length][];
			SW = new double[XY.length][];
			SE = new double[XY.length][];
			for (int i = 0; i < XY.length; i++) {
				NW[i] = new double[] { XY[i][0] - widthConstant1, XY[i][1] + widthConstant2};
				NE[i] = new double[] { XY[i][0] + widthConstant1, XY[i][1] + widthConstant2 };
				SW[i] = new double[] { XY[i][0] - widthConstant1, XY[i][1] - widthConstant2};
				SE[i] = new double[] { XY[i][0] + widthConstant1, XY[i][1] - widthConstant2};
			}
		}
	}

	public void plot(AbstractDrawer draw, Color c) {
		if (!visible)
			return;

		draw.canvas.includeInBounds(SW[0]);
		draw.canvas.includeInBounds(NE[XY.length - 1]);

		draw.setColor(c);
		draw.setLineType(AbstractDrawer.CONTINOUS_LINE);
		for (int i = 0; i < XY.length; i++) {
			if (f[i] > 0) {
				draw.fillPolygon(f[i], NW[i], NE[i], SE[i], SW[i]);
			}
		}
	}

	@Override
	public void setData(double[][] d) {
		splitXYf(d);
	}

	@Override
	public double[][] getData() {
		return XY;
	}

	public double[] isSelected(int[] screenCoordTest, AbstractDrawer draw) {
		for (int i = 0; i < XY.length; i++) {
			int[] screenCoord = draw.project(XY[i]);

			if ((screenCoord[0] + note_precision > screenCoordTest[0]) && (screenCoord[0] - note_precision < screenCoordTest[0])
					&& (screenCoord[1] + note_precision > screenCoordTest[1]) && (screenCoord[1] - note_precision < screenCoordTest[1]))
				return XY[i];
		}
		return null;
	}

    private static double[][] createCloud2dData() {
                double data[][]=new double[2][40];
                double x,y;
                for(int i=0;i<data.length;i++) {
                        for(int j=0;j<data[0].length;j++) {
                                x=(i-data.length/2.0)*3.0/data.length;
                                y=(j-data[0].length/2.0)*3.0/data[0].length;
                                data[i][j]=Math.exp(-x*x-y*y);
                        }
                }
                return data;
       }

/*
    public static void main(String[] args) {
		Plot2DPanel p = new Plot2DPanel();

		double[][] cloud = createCloud2dData();
		p.addCloudPlot("cloud", Color.RED, cloud, 2, 2);

		p.setLegendOrientation(PlotPanel.SOUTH);
		new FrameView(p).setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

*/
	public static void main(String[] args) {
		Plot2DPanel p = new Plot2DPanel();

        int N=8;
		double[][] cloud = new double[1000][2];
		for (int i = 0; i < cloud.length; i++) {
			cloud[i][0] = N * Math.exp(Math.random() + Math.random());
			cloud[i][1] = N * Math.exp(Math.random() + Math.random());
		}
		p.addCloudPlot("cloud", Color.RED, cloud, N, N);

		double[][] cloud2 = new double[1000][2];
		for (int i = 0; i < cloud2.length; i++) {
			cloud2[i][0] = 2 + Math.random() + Math.random();
			cloud2[i][1] = 2 + Math.random() + Math.random();
		}
		p.addCloudPlot("cloud2", Color.GREEN, cloud2, 2, 2);

		p.setLegendOrientation(PlotPanel.SOUTH);
		new FrameView(p).setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	} 

}
