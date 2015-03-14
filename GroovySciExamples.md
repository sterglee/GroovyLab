# Introduction #

`In this page we provide small examples of using GroovySci, in scientific applications. The code can be executed either:`

  * `by pasting it at the GroovyLabs's  command console `
  * `by pasting the code within the GroovyLab's programmer's editor, select the code and pressing F6`



## Henon Chaotic Map ##

`This small GroovySci script computes some iterations of the Henon chaotic map and plots them. `

```
x = 0d; y = 0d;
niters = 10000; 

alpha = 1.4d;
beta = 0.3d;
tic();
xy=new double[2][niters];
for (k=1; k<niters; k++)  {
  xp = x;yp=y;
  x = 1+yp-alpha *xp*xp;
  y = beta*xp;
  xy[0][k] = x;
  xy[1][k] = y;
};

scatterPlotsOn()    // plot points without connecting with lines
plot(xy);

```


## Baker map ##

```




x = (double)0.1; y = (double)0.22;
niters = getInt("How many iterations of the Ikeda map");
xall = new double[niters]
yall = new double[niters]

y13 = (double)1.0/(double)3.0
y23 = (double)2.0/(double)3.0
tic() 
for (k in 1..niters-1)  {
  xp = x; yp=y
 if (y<=0.5) {
   y = 2*yp
   x = y13*xp
}
else {
  x = y13*xp+y23
  y = 2*yp - 1
  }

  xall[k] = x
  yall[k] = y
}

tm = toc()
scatterPlotsOn()
figure(1)
plot(xall, yall, "time = "+tm)

```


## Plots with Latex Rendering ##

`We can have nice latex text on plots. Here is an example: `

```


// demonstrates plotting with LaTex labels

t = inc(0, 0.01, 10)
x = dot(exp(-0.12*t), log(1+t))
plot(t,x)

// the formula to be displayed 
lformula = "f(x) = \\exp^{-0.12*t} \\cdot \\log(1+t)"

// the size of the formula
labelSize = 20
// logical coordinates where LaTex formula will be displayed
labelx = 0.0 ; 
labely = 0.8   

// display the LaTex formula using logical coordinates
latexLabel(lformula, labelSize, labelx, labely)


```
## Ikeda Chaotic Map ##

```

// the Ikeda map
 R = 1;  C1 = 0.4; C2 = 0.9;  C3 = 6


niters = getInt("How many iterations of the Ikeda map");
x = new double[niters]
y = new double[niters]
x[0]=0.12; y[0]=0.2

tic() 
k=1
km=0
 tau=0.0;  sintau=0.0;  costau=0.0

while  (k< niters)  {
  km=k-1
  tau = C1-C3/(1+x[km]*x[km]+y[km]*y[km])
  sintau = sin(tau); costau = cos(tau);
  x[k] = R+C2*(x[km]*costau-y[km]*sintau)
  y[k] = C2*(x[km]*sintau+y[km]*costau)
 
  k++
}

tm = toc()
scatterPlotsOn()
figure(1)
plot(x, y, "time = "+tm)

```


## Demonstrating surface plots ##
```

// demonstrate a surface plot
 // below is the script code. It is wrapped to a class of name surfPlot by Groovy
 X = inc(-2, 0.2, 2);
 Y = inc(-2, 0.2, 2);
  
 x = X.getArray()[0];
 y = Y.getArray()[0];
 
 
z1 = Functions.f1(x, y);
z2 = Functions.f2(x, y);

figure3d(1);  surf(x, y, z1, "Surface Plot");
figure3d(2);  surf(x, y, z2, "Surface Plot 2");
figure3d(3);  surf(x, y, z1, "Surface Plot"); surf(x, y, z2, "Surface Plot 2");

class Functions {
	// function definition: z=cos(PI*x)*sin(PI*y)
	public static double f1(double x, double y) {
		double z = Math.cos(x * Math.PI) * Math.sin(y *Math.PI);
		return z;
	}
 
	// grid version of the function
	public static double[][] f1(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f1(x[i], y[j]);
		return z;
	}
 
	// another function definition: z=sin(PI*x)*cos(PI*y)
	public static double f2(double x, double y) {
		double z = Math.sin(x * Math.PI) * Math.cos(y * Math.PI);
		return z;
	}
 
	// grid version of the function
	public static double[][] f2(double[] x, double[] y) {
		double[][] z = new double[y.length][x.length];
		for (int i = 0; i < x.length; i++)
			for (int j = 0; j < y.length; j++)
				z[j][i] = f2(x[i], y[j]);
		return z;
	}
  }

 
```


## A cloud plot demo ##

```

     figure(1);
     N = 1000;
     cloud = new double[N][2];
     for (int i = 0; i < cloud.length; i++) {
			cloud[i][0] = N * Math.exp(Math.random() + Math.random());
			cloud[i][1] = N * Math.exp(Math.random() + Math.random());
     }
     slicesX = 2;
     slicesY = 2;
     plotname = "Demo2d cloud_plot";
     plot2d_cloud(cloud, slicesX, slicesY, plotname);
		 
```


## Perform some plots that test the VISAD interface, requires installation of Java 3D ##

```


//demonstrate Matlab-like plotting using VISAD
   // create signals
        t=inc(0, 0.01, 10); x = sin(3.4*t)
        y = sin(0.48*t)
        z = sin(5.7*x+0.2*y)

        vfigure(1);
        vsubplot(2,1,1);
        vplot(x, y, z)  // 3-D plot
        vsubplot(2,1,2);
        vplot(x, y, sin(9.8*z))

        vfigure(2);    
        vsubplot(2, 2, 1);   vplot(x, 8);  // plot with 8 point line
        vsubplot(2, 2, 2);   vplot(x)
        vsubplot(2, 2, 3);   vplot(sin(0.89*x))
        zz = z+cos(0.8*z)
        vsubplot(2, 2, 4);  vplot(z);
        vaddplot(zz)   // add the plot without erasing previous, i.e. in "hold on" state
        zzz = zz+ sin(3.4*zz)
        vaddplot(zzz, "zzx", "zzy", 5) 

    
        vfigure(3);    
        vsubplot(2, 2, 1);   vplotPoint(x);  // plot with 8 point line
        vsubplot(2, 2, 2);   vplot(x)
        vsubplot(2, 2, 3);   vplotXYPoints(x, y, "x", "y", 8)
        vsubplot(2, 2, 4);  vplot(z)
        vaddplot(zz)   // add the plot without erasing previous, i.e. in "hold on" state
        vaddplot(zzz, "zzx", "zzy", 5)

       
```


## A Continuous Wavelet Transform example ##

```
clear("all");


fs = 600; // 2050; 
dt = 1/fs; 
t =  inc(1, dt, 4);

PI2 = 2*PI;
y = sin(PI2*10*t)+4*     cos(PI2*4*t);
y = y+5*rand(y.size()[0], y.size()[1]);
y
N = y.size()[1]

fstart = 1;  // frequency to start
fmax = (double)fs/2;
maxNf = 20;


linlog = "log";
stepfac=16; 
df0=3;


ycwt = new wavelets.CWT(y,  fs, fmax, maxNf, linlog, stepfac, df0);

ed = ycwt.ed();  // energy density coefficients as a double[][] vector

edm = new Matrix(ed);
subsampledEdm = edm.resample(5, 1);  // subsample matrix before displaying it in contour plot
figure(1); 
subplot(2,1,1);
plot(y); title("signal");
subplot(2,1,2);
plot2d_scalogram(subsampledEdm, "scalogram");


```


## Plotting using a Matlab-like interface to the JFreeChart library ##

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


## Plotting Demonstrations ##

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


## And other plotting Demonstrations ##
```

// to run this demo copy and paste the code 

t=inc(-10, 0.01, 20);  // create a time axis
freq = 2; 
x=sin(freq*t);  // a simple sinusoid
y = 70* cos(0.4*t)+12*sin(0.7*t);

z = dot(y, 0.01*t);  // dot product
h = dot(z, sin(0.3*x));  //  if the operator * cannot perform matrix multiplication it tries to perform dot product

figure();  plot(t,x, "t -  sin(t)");

figNew = figure();  subplot(2,1,1);  plot(t, z, "t - t*x : A sine supeimposed on a linear inreasing curve");

subplot(2,1,2);  plot(z, h, "y .* 0.01*t, z .* sin(0.3*x)");


figure(); hold("off"); subplot(2,1,1);  hold("on");  plot(x,y, Color.RED); plot(x,z, Color.BLUE); 
subplot(2,1,2);   hold("off"); plot(x,y, Color.RED); plot(x,z, Color.BLUE);  
 title("Demo for hold(\"on\"): up subplot,  hold(\"off\"), bottom subplot");



sample = rand(3, 1000);
slices_x = 6; slices_y = 6; slices_z = 6; name="cloud";
 plot3d_cloud( sample, slices_x, slices_y, slices_z,  name);


```


## Various sine functions on the same plot ##

```

dt = 0.01;  // sampling frequency
xl = -10;  xu = 20;  // low and up limits
t=inc(xl, dt, xu);   // time axis
f11 = 0.23;  f12 = 3.7;  // two frquencies 
f21 = 0.25;  f22 = 3.9;  // slightly different frquencies   

x1 = sin(f11*t) + cos(f12*t);
x2 = sin(f21*t) + cos(f22*t);
figure(1);  hold("on"); plot(t, x1, Color.RED, "1st sine");
plot(t, x2, Color.GREEN, "2nd sine");
x12 = dot(x1, x2);
plot(t, x12, Color.BLUE, "x1 .* x2");



```


## `Using the Groovy's SwingBuilder to specify signal parameters and then perform processing of the signal` ##

```
// demonstrates using swing builder for getting signal parameters 
// and perform processing on that signal


swing = new SwingBuilder()
  frame = swing.frame(title:'Parameter Screen for signal') {
panel {     
        label('Sine Frequency')
        textField(id:'frequency',  columns:10)
        label('Noisy energy')
        textField(id:'noiseCoef', columns:10)

        button(text: 'Get Parameters', actionPerformed: {
        f1 =Double.valueOf(swing.frequency.text)
        noiseCoeff = Double.valueOf(swing.noiseCoef.text)
        closeAll()
        figure(1)
        t = inc(0, 0.02, 90)
        n = pow(2, 12)
        t= t.g(true, 1, n )
        x = sin(f1*t)+noiseCoeff*rand(1,n)
        y = sin(0.464*x)+0.78*cos(0.888*x)
        figure(1) 
        title("signal and its processed version")
        subplot(2,1,1); plot(x, Color.RED, "Signal"); 
        subplot(2,1,2); plot(y, Color.BLUE, "Processed Version");

      })
  }
}
frame.pack()
frame.show()

```

## Some simple signals with induced noise ##

```

t = inc(-5, 0.01, 10);
f1 = 0.34;
f2 = 0.56;
x = sin(f1*t)+5*cos(f2*t);
plot(t,x, "t-x plot");

y  = cos(x);
figure();   plot(x,y, "x-y plot");

sz=size(x);
xn = x+rand(sz[0], sz[1]);
fign = figure(); 
plot(t, xn, "noisy signal");

```


## A marked plot ##

```

t=inc(0, 1, 10)

x= sin(2.3*t)

plotMarks(t,x)
hold( "on" )
t2=inc(0, 0.1, 10)

x= sin(2.3*t2)

plot(t2,x) 

```
