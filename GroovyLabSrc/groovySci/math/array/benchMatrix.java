
package groovySci.math.array;

import javax.swing.JOptionPane;

public class benchMatrix {

    public static void main(String [] args)
    {
        long start = System.currentTimeMillis();
        int NLoop=1000;
        int N=200;
        
        double [][] m = new double[N][N];
for (int reps=0; reps < NLoop; reps++)
  for (int r=0; r<N; r++)
   for (int c=0; c<N; c++)
      m[r][c] = NLoop*r*c;

long end = System.currentTimeMillis();

double  delay = (double)(end-start)/1000.0;
JOptionPane.showMessageDialog(null, "delay = "+delay);
    }
}
// 0.219 sec 