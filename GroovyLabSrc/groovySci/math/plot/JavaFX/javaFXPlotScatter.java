
package groovySci.math.plot.JavaFX;


import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.ScatterChart; 
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color; 
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class javaFXPlotScatter  {
    static double [] pointsX;     // the points on X axis
    static double [] pointsY;    // the points on Y axis
    static String  plotName = "Plot of values array";    // the plot name
    static String plotTitle = "JavaFX chart scatter plot";
        
    static int xloc = 200;
    static int yloc = 200; 
    static int xsize = 600;
    static int  ysize = 500;
    
    private static void initAndShowPlot() {
        JFrame frame = new JFrame(plotTitle);
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);
        frame.setLocation(xloc, yloc);
        frame.setSize(xsize, ysize);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        Platform.runLater(new Runnable() {
        @Override
         public void run() {
            initFX(fxPanel);
        }
    });
    }
    
    private static void initFX(JFXPanel fxPanel)  {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
        
        javafx.scene.chart.NumberAxis  myaxis1 = new javafx.scene.chart.NumberAxis();
        javafx.scene.chart.NumberAxis  myaxis2 = new javafx.scene.chart.NumberAxis();

        ScatterChart   sc = new ScatterChart(myaxis1, myaxis2);
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName(plotName);
        sc.setTitle(plotTitle);
        int N = pointsX.length;
        for (int k=0; k<N; k++) 
        series.getData().add(new XYChart.Data<Number,Number>(pointsX[k], pointsY[k]));

        sc.getData().add(series);
        
        BorderPane pane =  new BorderPane();
       
        pane.setCenter(sc);
        
        Scene myScene = new Scene(pane, Color.ALICEBLUE); 
        
        return myScene;
    }

    public static void scatterplotfx(double [] values) {
        int L = values.length;
        pointsX = new double[L];
        pointsY = values;
        for (int l = 0; l<L;  l++)
            pointsX[l] =l;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
    public static void scatterplotfx(double [] valuesX, double [] valuesY)  {
        pointsX = valuesX;
        pointsY = valuesY;
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
      public static void setscatterPlotTitlefx(String title) {
          plotTitle = title;
      }
      
      public static void setscatterPlotNamefx(String name) {
          plotName = name;
      }
      
}    
