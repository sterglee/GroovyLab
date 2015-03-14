# Introduction #

`It is easy to construct independent applications in Java/Groovy that use GroovyLab functionality (i.e. "groovySci"). We describe here the required steps for the Netbeans framework, similar will be the process for other IDEs.`

`The first step is to download the archive "GroovySciLibsForStandAloneApplications.zip" and to extract these files.`

`The next and final step is to create a Netbeans Java project that uses the extracted .jar files as Compile/Run time libraries. `

`Then we can develop Java / Groovy applications that use groovySci. We present below a simple Java example. `

`The archives contain also the sources of simple examples. `

## Java groovySci example ##

`The following example illustrates the use of groovySci from Java. Use all the .jar files extracted from "GroovySciLibsForStandAloneApplications.zip" and define GroovySciPlots as the project's Main Class. Then you can execute the application with `

` java -jar GroovySciPlots.jar `

```

package plotsGroovySci;


import groovySci.math.array.Vec;
import static groovySci.math.array.Vec.*;
import groovySci.math.plot.plot.*;
import static groovySci.math.plot.plot.*;


public class GroovySciPlots {

    public static void main(String [] args) {
      int N = 1000;
     Vec v =  vrand(N);
    
     plot(v);

 }
}

```