package JFplot;

import org.jfree.chart.*
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.*
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.ui.*
import org.jfree.data.statistics.*;

import groovy.swing.SwingBuilder
import java.awt.*
import javax.swing.WindowConstants as WC
import javax.swing.*



/**
* Configuring JFreeChart charts is not hard, but it does clutter up your code. 
* Moreover, I tend to always want the same settings.  So this class just encapsulates
* the sort of standard version of a lot of charts I commonly create.  
*
*/ 
class Charts{
	
	static err = System.err
	
	/**
	* Saves a chart as PNG with fileName
	*/ 
	/*static saveChart(chart,fileName){
		err.println "Saving chart to $fileName..."
		def chartpanel = new ChartPanel(chart);
		ImageUtils.savePanelAsPNG(chartpanel,fileName)
		err.print "saving done."
	}*/
	
	/**
	* Displays chart in a window. 
	*/ 
	static showChart(chart){
		def title = chart.getTitle().getText()
		showChart(chart,title);
	}
			
	/***
	* Creates a window and displays the chart. Window has title chartTitle
	*/ 
	static showChart(chart,chartTitle){
		def swing = new SwingBuilder()
		def frame = swing.frame(title:chartTitle,
														defaultCloseOperation:WC.EXIT_ON_CLOSE,
														pack:true,show:true) {
			borderLayout()
			panel(new ChartPanel(chart),preferredSize: new java.awt.Dimension(500, 270),mouseWheelEnabled:true)
		}
		return(swing)		
	}
	 
  /**
  * Creates an XY series from a Groovy [:], aka LinkedHashMap
  */ 
  static createXYFromMap(LinkedHashMap data,String seriesName){
    XYSeries series1 = new XYSeries(seriesName);    
    for(x in data.keySet()){
      def y = data.get(x);
      series1.add(x,y);
    }        
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);
    
    return(dataset);
  }

	/**
	* Creates an XYSeries from a pair of collections X and Y
	*/ 
	static createXYFromCollections(Collection X,Collection Y,String seriesName){
		XYSeries series1 = new XYSeries(seriesName);    
		for(int i = 0;i < X.size();i++){
			def x = X[i];
			def y = Y[i];
			series1.add(x,y);
		}
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		return(dataset);
	}

	/**
	* Creates an XYSeries from a pair of DoubleVectors
	*/ 
	static createXYFromDoubleVectors(DoubleVector X,DoubleVector Y,String seriesName){
    XYSeries series1 = new XYSeries(seriesName);    
		for(int i = 0;i < X.size();i++){
			def x = X[i];
			def y = Y[i];			
      series1.add(x,y);
    }        
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series1);    
    return(dataset);
  }
  
  
  /**
  * Returns a default line chart with data in a LinkedHashMap. 
  */
  static lineChart(String title,LinkedHashMap data,int xsize,int ysize){            
    def xydata = createXYFromMap(data,"Series 1");    
    return(lineChart(title,xydata,xsize,ysize))
  }
  
	/**
	* Creats a line chart form data in an XYSeriesCollection
	*/ 
  static lineChart(String title,XYSeriesCollection xydata){
    
    // create the chart...
    JFreeChart chart = ChartFactory.createXYLineChart(
        title,      // chart title
        "X",                      // x axis label
        "Y",                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        true,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setBaseShapesVisible(true);
    renderer.setBaseShapesFilled(true);

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }
  
	/**
	* XY Plot from two collections. 
	*/ 
	static xyplot(String title,String xlabel,String ylabel,Collection x,Collection y){
		def xydata = createXYFromCollections(x,y,"Series 1")
		return(xyplot(title,xlabel,ylabel,xydata))
	}

	/**
	* XY Plot from two DoubleVectors
	*/ 
	static xyplot(String title,String xlabel,String ylabel,DoubleVector x,DoubleVector y){
		def xydata = createXYFromDoubleVectors(x,y,"Series 1")
		return(xyplot(title,xlabel,ylabel,xydata))
	}


	/**
	* XY Plot with generic X and Y labels. 
	*/ 
	static xyplot(String title,DoubleVector x,DoubleVector y){
		def xydata = createXYFromDoubleVectors(x,y,"Series 1")
		return(xyplot(title,"X","Y",xydata))
	}

	/**
	* XY Plot from LinkedHashMap data. 
	*/ 
	static xyplot(String title,LinkedHashMap data){
		def xydata = createXYFromMap(data,"Series 1")
		return(xyplot(title,"X","Y",xydata))
	}

	/**
	* XY Plot from XYSeriesCollection
	*/ 
  static xyplot(String title,XYSeriesCollection xydata){
    return(xyplot(title,"X","Y",xydata))
  }
  
	/**
	*	XY Plot from XYSeriesCollection
	*/ 
  static xyplot(String title,String xlabel,String ylabel, 
    XYSeriesCollection xydata){
    
    // Only show legend if there is more than one series. 
    def bShowLegend = false;
    if (xydata.getSeriesCount() > 1) bShowLegend = true;
    else bShowLegend = false;
    
    // create the chart...
    JFreeChart chart = ChartFactory.createScatterPlot(
        title,      // chart title
        xlabel,                      // x axis label
        ylabel,                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        bShowLegend,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);
    XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
    renderer.setBaseShapesVisible(true);
    renderer.setBaseShapesFilled(true);
    renderer.setBaseLinesVisible(false);

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }

	/***
	* Create a histogram from values in an arbitrary collection...
	*/ 						
	static hist(cName,values){
		
		def binmax = values.max()
		def binmin = values.min()
		
		//err.println "createHistogramFromValues"
		def series = new HistogramDataset()
		def valarray = new double[values.size()]
		values.eachWithIndex{v,i->valarray[i] = v as double}

		series.addSeries("Series1",valarray,50,binmin as double,binmax as double)
		def chartTitle = "$cName"
		def chart = Charts.hist(chartTitle,"","",series) 

		def titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD,12)
		def title = new org.jfree.chart.title.TextTitle(chartTitle,titleFont)
		chart.setTitle(title);

		return(chart)
	}
  
	/***
	* Create histogram with generic labels. 
	*/ 
  static hist(String title,HistogramDataset xydata){
    return(hist(title,"Count","X",xydata))
  }
  
	/***
	* Create histogram from HistogramDataset
	*/ 
  static hist(String title,String xlabel,String ylabel, HistogramDataset xydata){
    
    // Only show legend if there is more than one series. 
    def bShowLegend = false;
    if (xydata.getSeriesCount() > 1) bShowLegend = true;
    else bShowLegend = false;
    
    // create the chart...
    JFreeChart chart = ChartFactory.createHistogram(
        title,      // chart title
        xlabel,                      // x axis label... KJD: if null doesn't it take it from series???
        ylabel,                      // y axis label
        xydata,                  // data
        PlotOrientation.VERTICAL,
        bShowLegend,                     // include legend
        false,                     // tooltips
        false                     // urls
    );

    // get a reference to the plot for further customisation...
    XYPlot plot = (XYPlot) chart.getPlot();
    plot.setDomainPannable(true);
    plot.setRangePannable(true);

		if (xydata.getSeriesCount() > 1) plot.setForegroundAlpha(0.85f);

    def renderer = (XYBarRenderer) plot.getRenderer();
    renderer.setDrawBarOutline(false);
    renderer.setBarPainter(new StandardXYBarPainter());
    renderer.setShadowVisible(false);

    // change the auto tick unit selection to integer units only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    return chart;
  }


	/**
	* Create a dual histogram from two series. 
	*/ 
	static dualhistogram(values1,series1name,values2,series2name,cName){
		 return(dualhistogram(values1,series1name,Color.blue,values2,series2name,Color.green,cName,20))
	}

	/**
	* Create a dual histogram from two series. 
	*/ 
	static dualhistogram(values1,series1name,values2,series2name,cName,bins){
		 return(dualhistogram(values1,series1name,Color.blue,values2,series2name,Color.green,cName,bins))
	}


	/***
	* Create a histogram from two sets of values in arbitrary collection...
	*/ 						
	static dualhistogram(values1,series1name,color1,values2,series2name,color2,cName,bins){

		double binmax1 = values1.max()
		double binmax2 = values2.max()
		double binmin1 = values1.min()
		double binmin2 = values2.min()

		//err.println "createHistogramFromValues"
		def series = new HistogramDataset()
		def valarray1 = new double[values1.size()]
		values1.eachWithIndex{v,i->valarray1[i] = v as double}

		def valarray2 = new double[values2.size()]
		values2.eachWithIndex{v,i->valarray2[i] = v as double}

		def chartTitle = "$cName"
		series.addSeries(series1name,valarray1,100,binmin1,binmax1)
		series.addSeries(series2name,valarray2,100,binmin2,binmax2)
		def chart = Charts.hist(chartTitle,"","",series) 

		def titleFont = new java.awt.Font("SansSerif", java.awt.Font.BOLD,14)
		def title = new org.jfree.chart.title.TextTitle(chartTitle,titleFont)
		chart.setTitle(title);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.WHITE);  // For the plot
    plot.setForegroundAlpha(0.60f);

		def renderer = plot.getRenderer();
		renderer.setSeriesPaint(0, color1);
		renderer.setSeriesPaint(1, color2);

		return(chart)
	} 
}