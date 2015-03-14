# `GroovyLab: Easy and effective MATLAB-like scientific programming in Groovy ` #

## New Downloads ##
`Since Google disabled creating new downloads, new downloads can be obtained from:`

http://sourceforge.net/projects/groovylab/


**`GroovyLab is developed with JDK8, so make sure to have JDK8 installed.`**

**`Important tip: Be careful the path name at which GroovyLab is placed, to not contain special characters, such as spaces, Greek letters, symbols etc. That can cause failure to load properly.`**



`The ` **`GroovyLabAll*.zip`** `download contains both the sources and all the relevant libraries to build GroovyLab with ant. `

`To build GroovyLab is very simple: `

`1. Unzip GroovyLabAll.zip`

`2. Go to .\GroovyLabPr folder and build with ant, i.e.`

_`ant`_

`The executable is built in the dist folder`



## `Project Summary ` ##
`The  GroovyLab  environment aims to provide a MATLAB/Scilab like scientific computing platform that is supported by a scripting engine implemented in Groovy language. The GroovyLab user can work either with a MATLAB-like command console, or with a flexible editor based on the ` **`rsyntaxarea (http://fifesoft.com/rsyntaxtextarea/)`** `component, that offers more convenient code development. GroovyLab supports extensive plotting facilities and can exploit effectively a lot of powerful Java scientific libraries, as` _` JLAPACK`_ `,` _` Apache Common Maths`_ `,` _` EJML`_ `,` _` MTJ`_ `,` _` NUMAL translation to Java`_ `,` _` Numerical Recipes Java translation`_ `,` _` Colt`_ `etc.  Also, GroovyLab supports Computer Algebra based on the ` **`symja (http://code.google.com/p/symja/)`** `project. `

`GroovyLab scripts can be easily ` **`combined with MATLAB`**`, thus the scientist can combine the flexibility of Groovy  with the computational effectiveness of MATLAB. `

`GroovyLab provides the `**`GroovySci`** `compiled scripting engine which is based mainly on ` **`GroovyShell`** , `with extensions to provide MATLAB-like matrix operations.`

`GroovyLab is user friendly, and can be installed simply by unzipping the GroovyLabAll*.zip. Then we simply run:   ` **`java -jar GroovyLab.jar`** `or better, execute the proper script for your platform (e.g. RunGroovyLabServerLinux64.sh for Linux).`

`GroovyLab supports indy (invoke dynamic) since it utilizes the indy version of Groovy. Also, by default performs computations using doubles instead of BigDecimals for speed. These settings are configurable. Also, very convenient for scripting, is the import and code buffering facility, described in ` **`ScriptingWithImportBuffering`** `wiki page. `






## `Latest Changes - 08  - Mar - 2015  ` ##

**`Mar 08 - Run scripts for 32-bit platforms (i.e. Win32 and Linux32) are corrected`**

**`Feb 28 - Minor GUI improvement: cursor indicates wait when a computation is performed.`**

**`Feb 27 - Faster CCMath based native operations for Linux64 - Support for Raspberry Pi 2 improved. Execute on Pi as: java -jar GroovyLab.jar`**

**`Feb 23 - Support for FreeBSD, MacOS X, Solaris platforms, with of course the Java 8 prerequisite`**

**`Jan 31 - Some improvements on Matrix class routines, and at the interfacing with the GNU Scientific Library (GSL)`**

**`Dec 27 - Apache Common Maths new version *`**


**`Nov 17 - Support for the jeigen matrix library for the Linux64 platform  `**

**`Sep 03 - CUDA 6.5 support for Linux64, for Windows support is for CUDA 6.0`**

**`Aug 31 - JavaFX based plots in GroovyLab, see wiki page`**

**`Aug 26 - Requires Java 8 -  GroovyLab embeds within its source the Apache Common Maths and performs automatically the most useful imports. This approaches seems much convenient. See wiki page for examples. `**


**`Aug 15 - Very fast matrix multiplication using native BLAS and Java multithreading - native BLAS performs fast only for Linux 64 platforms `**


## `GroovyLab Advantages` ##

`The main advantages of GroovySci that equip it with great potential are: `
  1. `The flexibility of the Groovy language, that offer many opportunities to implement convenient high-level scientific operators. `
  1. `The vast Java based scientific libraries with excellent code for many application domains. These libraries are directly available from GroovySci with the dynamic loading of the corresponding Java toolboxes.`
  1. `The user friendly Matlab-like environment of GroovyLab and the high quality scientific plotting support. `
  1. `The syntax of Groovy is much like Java, and Java code can be easily mixed with Groovy, therefore engineers with Java background can become productive in short time.`
  1. `The speed of Groovy based scripting, with the invoke dynamic implementation, dynamic code comes generally close to Java code.`
  1. **`Tight integration with MATLAB.`** `Scientists familiar with MATLAB, can use it from GroovyLab and combine MATLAB scripts with Java like applications.`


## `Documentation for GroovyLab` ##

`We started to write documentation with a draft user guide and at the wiki pages of the project, please see them.`

`GroovyLab provides a lot of on-line help and demo examples. The philosophy of GroovyLab is to exploit high quality Java scientific software. Although any Java scientific library can be utilized as toolbox, GroovyLab includes by default some excellent Java scientific libraries. Work is in progress to utilize more effectively these libraries by designing Groovy based high level interfaces. These integrated libraries are:`
  1. `The Java translation of Numerical Recipes, Third Edition, C++ code by Huang Wen Hui`
  1. `The NUMAL library described in the book: ` _A Numerical Library in Java for Scientists & Engineers_, `Hang T. Lau, Chapman & Hall/CRC, 2004 `
  1. `The Efficient Matrix Java Library, http://code.google.com/p/efficient-java-matrix-library/`
  1. `The Matrix Toolkit for Java, http://code.google.com/p/matrix-toolkits-java/ `
  1. `The Parallel Colt Library, http://sites.google.com/site/piotrwendykier/software/parallelcolt`
  1. `Jsci - A Science API for Java, http://jsci.sourceforge.net/, provides mostly Wavelet Analysis Routines`
  1. `The JFreeChart plotting system, http://www.jfree.org/jfreechart/, provides plots, for some of which already exists Matlab-like interface`
  1. `The ViSAD Java Scientific Visualization library, http://www.ssec.wisc.edu/~billh/visad.html, includes documentation about the design and utilization of this sophisticated package. Up to now the Java interface can be used from jLab . We plan to provide a Matlab-like simpler interface soon. `
  1. `The jzy3D for scientific 3-D plots (http://www.jzy3d.org), provides an easy way to create superb 3-D plots based on Java OpenGL.`

## `Useful Books to use with GroovyLab` ##

`GroovyLab can be utilized and as an educational tool for Numerical Analysis, Computational Intelligence and Engineering courses. Since the GroovyLab editor can execute directly Java code, many Java code chunks can be easily executed. The following are some excellent scientific books that can be studied using GroovyLab for programming exercises:`
  1. `The NUMAL library described in the book: ` _A Numerical Library in Java for scientists & Engineers_, `Hang T. Lau, Chapman & Hall/CRC, 2004 `
  1. _Numerical Recipes in C++, Second (2002) and Third editions (2007),_ `William H. Press, Saul A. Teukolsky, William T. Vetterling, Brian P. Flannery, Cambridge University Press, 2002, A classic book on numerical analysis and programming, ` **`Numerical Recipes Code is translated in Java by Huang Wen Hui, therefore can be used from GroovyLab, see wiki page for examples. `**
  1. `Numerical Analysis :`  _Object-Oriented implementation of Numerical Methods: An Introduction with Java and Smalltalk_, `Didier H. Besset, Morgan Kauffmann, 2000`, `An excellent introductory book on numerical analysis methods, the routines of the book are available within the core of jLab`
  1. `Expert Systems:` _Constructing Intelligent Agents with Java, J. Bigus et.al, John Wiley and Sons, 1997_, `A very good book on artificial intelligence techniques with Java on which the JFES jLab toolbox is based `
  1. `Data Mining:` _Data Mining, Practical Machine Learning Tools and Techniques_, Ian H. Witten, Eibe Frank, Mark A. Hall, Morgan Kauffman, 2011, `An excellent book for developing data mining techniques using WEKA as a vehicle, WEKA can be used as jLab toolbox`
  1. `Neural Networks:` _Programming Neural Networks with ENCOG 2 in Java_,` Jeff Heaton, Heaton Research, 2011, An excellent book for practicing neural network techniques with the ENCOG system, that is tested and provided as jLab toolbox`
  1. `Direct Methods for Sparse Linear Systems: ` _`Timothy A. Davis`_` SIAM publishing 2006, Describes the CSparse algorithms and implementation on which the Sparse GroovyLab class is based`


## `jlabgroovy (GroovyLab)  Development discussion group Mailing List` ##

http://groups.google.com/group/jlabgroovy

## `GroovyLab Developer` ##

```
    Stergios  Papadimitriou 
    Technology Education Institute of East Macedonia and Thraki
    Dept of Informatics and Computer Engineering 
    Greece 
    email: sterg@teikav.edu.gr

```