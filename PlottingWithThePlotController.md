# Introduction #


`Plotting routines tend to be complicated and can easily confuse the user. Therefore, we start to develop more convenient, object oriented plotting routines. These routines keep state information and permit the user to easily control the properties of the plots. `

`We can now test the plot controller object as: `

## `Example 1` ##

```

   

    // Illustrates how to  construct a PlotController object to facilitate the plotting
 po = new PlotController()
    
    // construct some signals
  x = vinc(0, 1, 100)
  y1 = sin(0.45*x)
  y2 = sin(0.778*x)+0.2*cos(3.4*x)
  y3 = cos(y1+y2)
    
    // construct the first plot object
    closeAll()  // close any previous figure
    po.setX(x.getv())  
    po.setY(y1.getv())
    po.setColor(Color.GREEN)  // use GREEN color for plotting
    po.setplotTitle2D(" Demonstrating 2-D plots")
    po.setDottedLine()
    po.mkplot()   // plot the first signal
    
    po.setContinuousLine();   // set now to continous line plotting
    po.setColor(Color.RED)
    po.setlineWidth(15)   // set to thicker width
    // redefine the new signals for plotting
    hold("on"); 
    po.setY(y2)   // we change only the Y signal

    figure(); po.mkplot()
    
    z = cos(4.5*x)
    po.setZ(z)
    
    po.setColor(Color.BLUE)  // change the plot color
    po.setlineWidth(1)  // set the line width to 1
    po.mkplot3D()
        
```



## `Example 2` ##

```

    // Illustrates how to  construct a PlotController object to facilitate the plotting
 po = new PlotController()


// create some artificial signals
 x = (0..100).step(0.1)
 y = sin(0.23*x)

 // set the signals
 po.setX(x)
 po.setY(y)
 po.setxlabel("time-axis")
 po.setylabel("signal")
 
 po.mkplot()   // make a 2D plot

 
    
```

## `Example 3` ##

```

    // Illustrates how to  construct a PlotController object to facilitate the plotting
 po = new PlotController()


// create some artificial signals
 x = (0..100).step(0.1)
 y = sin(0.23*x)
 z = cos(0.78*x)

 // set the signals
 po.setX(x)
 po.setY(y)
 po.setZ(z)
 
 po.setxlabel("time-axis")
 po.setylabel("y-signal")
 po.setzlabel("z-label")

 po.setColor(Color.GREEN)
 po.mkplot3D()   // make a 3D plot

 
    
```