
package groovySci;
/*

import javafx.application.Application;
import javafx.scene.Scene; 
import javafx.scene.chart.LineChart; 
import javafx.scene.chart.XYChart;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage; 

public class javaFXPlotLine extends Application {

    static double [] points;
    
    @Override public void start(Stage stage) {
        //Label message = new Label("Line Plot with JavaFX");
       // message.setFont(new Font(100));
        
        //Button red = new Button("Red");
        //red.setOnAction(  event -> message.setTextFill(Color.RED) );
        
        javafx.scene.chart.NumberAxis  myaxis1 = new javafx.scene.chart.NumberAxis();
        javafx.scene.chart.NumberAxis  myaxis2 = new javafx.scene.chart.NumberAxis();

        LineChart  lc = new LineChart(myaxis1, myaxis2);
        XYChart.Series<Number,Number> series = new XYChart.Series<Number,Number>();
        series.setName("Data Series 1");
        int N = points.length;
        for (int k=0; k<N; k++) 
        series.getData().add(new XYChart.Data<Number,Number>((double)k, points[k]));

        lc.getData().add(series);
        
        BorderPane pane =  new BorderPane();
       // pane.setTop(message);
        //pane.setBottom(red);
        pane.setCenter(lc);
        Scene myScene = new Scene(pane); 
        stage.setScene(myScene);
        
        stage.setTitle("JavaFX based line plot"); 
        stage.show(); 
    }

    
    public static void plot(double [] values) {
        points = values;
        Application.launch();
    }
}    
*/
