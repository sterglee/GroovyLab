
package groovySci.math.plot.JavaFX;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.layout.BorderPane; 
import javafx.scene.paint.Color; 
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class javaFXPlotPie   {
    static String [] labelsOfItems;     // the String labels of items
    static double [] valuesOfItems;    // the values of items
    static String  plotName = "PieChart example";    // the plot name
    static String plotTitle = "JavaFX pie chart";
    static int xloc = 200;
    static int yloc = 200; 
    static int xsize = 600;
    static int  ysize = 500;

    // prepares the data for the PieChart
    private  static ObservableList<PieChart.Data> getChartData() {
        ObservableList<PieChart.Data> answer = FXCollections.observableArrayList();
        int Nitems = labelsOfItems.length;
        for (int cnt = 0; cnt < Nitems; cnt++) 
            answer.add(new PieChart.Data(labelsOfItems[cnt], valuesOfItems[cnt]));
        
        return answer;
    }

    // prepares the JavaFX plot in a Swing container
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

        PieChart  pieChart = new PieChart();
        pieChart.setData(getChartData());
        
        BorderPane pane =  new BorderPane();
       
        pane.setCenter(pieChart);
        
        Scene myScene = new Scene(pane, Color.ALICEBLUE); 
        
        return myScene;
    }

    public static void pieChartfx(String[]  labels, double [] values ) {
       labelsOfItems = labels;
       valuesOfItems = values;
           SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
              initAndShowPlot();
            }
        });
    }
    
      public static void setpieChartTitlefx(String title) {
          plotTitle = title;
      }      
      public static void setpieChartNamefx(String name) {
          plotName = name;
      }
      
}    

