# Introduction #

`In this tutorial we demonstrate the more important parts of working with Glab. `

# The essentials of the User Interface #

`After initialization Glab presents two windows: ` **`a. The Programmer's editor window`** `that provides a `**`specialized editor`**`, along with the `**`output console`**, and **`b. The Main User Interface window`** `that provides things as the` **`command console window, the explorer, the history window, the main menu bar, and the toolbars`**.

`The user can execute GroovySci scripts in two ways: `
  1. `At the Command Console Window`
  1. `With the Glab programmer's editor`

`The command console window is convenient for single line command execution, i.e. we can write: `
```
t= inc(0, 0.01, 50); x = sin(0.5*t)+2.3*cos(2.3*t); plot(t,x)
```
`and execute directly this command by pasting it at the command window.`

`Contrary the Glab programmer's editor allows to execute program text, while editing it. `

`To execute program text: ` **`select the text and press F6.`**

`For example let execute the code: `

```
sm=0.0
for (k in 0..2000)
 sm += k
println("sm = "+sm)
```

`We should paste the code in the Glab's programmer's editor and then select it, and press F6.`

`Pressing ` **`F6 with no selected text, executes the current line.`**

`Also, another very useful keystroke is the ` **`F2 keystroke`** `that executes the code from the previously executed position up to the current position.`

`Additionally, Glab supports` **`code completion.`** ` Code completion is activated with the ` **`F1`** `key. Pressing F1 at the command console tries to help about the current identifier. In order to use code completion within the programmer's editor we should select the text and then press F1. For example, we can select "ff" and then press F1. Glab presents a large list of related methods starting with "ff", e.g. "fft", "fft2" etc. With a second F1 at each item of that list we obtain further detail on the related class using Java's reflection`

> ## Demo Programs ##

`Let now run some demo programs: `

## Wavelet Transformation ##
```

import wavelets.DaubW;
import static groovySci.math.array.Matrix.*
import static groovySci.math.plot.plot.*
import java.awt.*;
import javax.swing.*;
import groovySci.math.plot.*;
import groovySci.math.plot.render.*;


N=2000;
freq=0.12; 
a = new double[1][N];
origSignal = new double[1][N];

for (k in 0..N-1) {
  a[0][k] = Math.sin(freq*k);
  origSignal[0][k] = Math.sin(freq*k);
}

//  origSignal = a.clone();  // keep the original signal
 
  DaubW  daubWavelet = new DaubW();
     
  daubWavelet.daubTrans(a[0]);
 
Plot2DPanel plotP = new Plot2DPanel();
  
figure(1);  title("Signal and its Daubechies Wavelet Transform");
subplot(2,1,1) ;
plot(origSignal[0],  Color.RED, "original signal");  


subplot(2,1,2); plot(a[0],  Color.GREEN, "Daubechies Wavelet transformed"); 

```

`you can paste the code in the editor, then select the code and press F6, or pressing F2 while the cursor is at the end of the code, to execute the code, that performs a Wavelet transformation.`


## Plotting Demonstration ##

```
   
         t = inc(0, 0.01, 20);
         x = sin(0.2*t);
         figure(1); title("Demonstrating ploting multiple plots at the same figure");
        plot(t,x, Color.GREEN, "sin(0.2*t)");
// the xlabel(), ylabel() here refer to the axis of the current plot (i.e. PlotPanel object)
         xlabel("t-Time axis");
         ylabel("y=f(x) axis");
         y = sin(0.2*t)+5*cos(0.23*t);
         hold("on");
         plot(t,y, new Color(0, 0, 30), "sin(0.2*t)+5*cos(0.23*t)" );


          t = inc(0, 0.01, 20);
          y = sin(0.2*t)+5*cos(0.23*t);
          z = sin(1.2*t)+0.5*cos(0.23*t);
         fig = figure3d(2); plot(t,y, z,  Color.BLUE, "Ploting in 3-D");
// specify labels explicitly for the fig PlotPanel object
         fig.xlabel("t - Time axis ");
         fig.ylabel("y - sin(0.2*t)+5*cos(0.23*t)");
         fig.zlabel("sin(1.2*t)+0.5*cos(0.23*t);");
         title("A 3-D plot");


         zDot = dot(z, z);
         fig = figure3d(3); plot(t,y, zDot, Color.RED);
// specify labels explicitly for the fig PlotPanel object
         xlabel("x - Time axis ");
         ylabel("y - sin(0.2*t)+5*cos(0.23*t)");
         zlabel("(sin(1.2*t)+0.5*cos(0.23*t)) .* (sin(1.2*t)+0.5*cos(0.23*t))");

         zDot2 = dot(zDot, zDot);
         fig = figure3d(4);
         subplot3d(2,1,1); 
         plot(t,y, zDot, Color.YELLOW);
// specify labels explicitly for the fig PlotPanel object
         xlabel("x - Time axis ");
         ylabel("y - sin(0.2*t)+5*cos(0.23*t)");
         zlabel("(sin(1.2*t)+0.5*cos(0.23*t)) .* (sin(1.2*t)+0.5*cos(0.23*t))");
        subplot3d(2,1,2);
        plot(t,y, zDot, Color.MAGENTA);
         
// specify labels explicitly for the fig PlotPanel object
         xlabel("x - Time axis ");
         ylabel("y - sin(0.2*t)+5*cos(0.23*t)");
         zlabel("zDot");

```

## Plotting points ##
```



         t=inc(0, 1, 10)
        
         x= sin(2.3*t)

         color = Color.BLUE 
         font = new java.awt.Font("Arial", java.awt.Font.BOLD, 12)
         skipPoints = 1
         ch  = (char)'x'
         plotPoints("", color,  (char)'x', font,  skipPoints,  t,x)
          hold( "on" )
          t2=inc(0, 0.1, 10)
        
        
          x2= sin(2.3*t2)
        
          plot(t2,x2)

          color = Color.GREEN
          plotPoints("dense", color, (char)'#', font, skipPoints, t2, x2)  
        
```


## Plotting with the JFreeChart Library ##
```
jfigure(1)
t = inc(0, 0.01, 10);   x = sin(0.23*t)
lineSpecs = "."
jplot(t,x, lineSpecs)
jtitle("drawing multiple line styles")
jhold(true)  // hold axis
lineSpecs = ":r+"
jplot(t, 0.1*cos(9.8*x), lineSpecs)
// redefine the color of line 2
jlineColor(2, Color.BLUE)

jfigure(2)
jsubplot(222)
x11 = sin(8.23*t)
jplot(t,x11)
jhold(true)
lineSpecs = ":g"
jplot(t,sin(5*x11), lineSpecs)
jsubplot(223)
lineSpecs = ":r"
jplot(t,x11, lineSpecs)


// create a new figure and preform a plot at subplot 3,2,1
 nf = jfigure()
 jsubplot(3,2,1)
 t2 = inc(0, 0.01, 10);  x2 = sin(3.23*t2)+2*cos(0.23*t2)
 jplot(t2,x2, ".-")
jsubplot(3,2,3)
x3 = cos(2.3*t2)+9*sin(4.5*t2)
jplot(t2, x3)
jlineColor(1, Color.RED)
jsubplot(3,2,5)
x4 = cos(12.3*t2)+9*sin(2.5*t2)
jplot(t2, x4+x3)
jlineColor(1, Color.GREEN)
jsubplot(3,2,6)
jplot(t2, 6*x4+x3)
jtitle("6*x4+x3")
jlineColor(1, Color.BLUE)

// now plot again at figure 2
jfigure(2)  // concetrate on figure 2
jhold(true);
jsubplot(2, 2, 1)
vr = rand(1, 2000)
jplot(vr)
jtitle("A Random Vector")
td = t.getv()
jsubplot(224)
jplot(td, sin(1.34*td))
jplot( td, sin(3.6*td))
jtitle("Multiple Plots")



// demonstrate PieDataChart

c = new String[3];  c[0] = "Class1"; c[1] =  "Class2"; c[2] = "Class3"
v = new double[3]; v[0]=5.7; v[1] = 9.8;  v[2] = 3.9
pieChartName = "Test Pie Chart"
myPie = jplot(pieChartName, c, v)

```