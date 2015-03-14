# Introduction #

`JavaFX comes with the ChartFX component that performs nice looking charting. In GroovyLab we develop an easier interface to utilize ChartFX for plotting. We demonstrate examples. This examples run the Java 8 based version of GroovyLab. JavaFX functionality can be exploited within the Swing based user interface of GroovyLab, but that requires provisions, such as considering the threading issues of the two environments. `


## `Line Plots` ##

```


 x = Inc(0, 0.01, 20)

 y = sin(0.56*x)

setlinePlotTitlefx("A sine function  plotted with JavaFX")

setlinePlotNamefx(" sin function")
lineplotfx(x, y)


```

## `Scatter Plots` ##

```


 x = Inc(0, 0.01, 20)

 y = sin(0.56*x)

setscatterPlotTitlefx("A sine function  plotted with JavaFX")

setscatterPlotNamefx(" sin function")
scatterplotfx(x, y)


```


## `Pie Charts ` ##

```




 languages = ["Scala", "Java", "Groovy", "C/C++", "C#"] as String []

speedIndex = [0.8, 0.82, 0.6, 0.95, 0.81] as double [] 

setpieChartTitlefx("Speed indexes of some languages")

setpieChartNamefx("Speed of languages")


pieChartfx(languages, speedIndex)

```

## `Bar Chart ` ##

```

 languages = ["Scala", "C/C++", "Java", "C#", "Groovy"] as String[]    // the items

 benchmarksPerformed = ["Array Access", "Loops", "Sieve"] as String []   // the attributes

 resultsOfBenches = [[0.8, 0.9, 0.6],  [0.9, 0.95, 0.91], [0.85, 0.92, 0.7],  [0.82, 0.9, 0.67],  [0.4, 0.45, 0.47]] as double [][]


setbarChartLabelsOfItems( languages)

setbarChartAttributeNames ( benchmarksPerformed )

setbarChartAttributeValues( resultsOfBenches )


barChartfx( )

```
