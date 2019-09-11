# GroovyLab: Easy and effective MATLAB-like scientific programming in Groovy

## Installation

The installation of GroovyLab is very simple: 

  * *Step 1* Download and unzip the .zip download.
  
  * *Step 2* Run the proper script for your platform, i.e. the .sh scripts for Unix like systems and the .bat scripts for Windows. 


## Building with Netbeans and ant

The GroovyLab zip download contains both the sources and all the relevant libraries to build GroovyLab with ant.

To build GroovyLab is very simple:

 * Unzip the zipped file 
 
 * Go to .\GroovyLabPr folder and build with ant, i.e. type

*ant*

The executable is built in the dist folder

Of course, better is to use the Netbeans where you can load the corresponding project.



## Building with Gradle and support for fat jar that supports the DeepLearning4j library and Apache Spark

To build GroovyLab with *Gradle*, unzip the zipped file, go to the root folder where the contents are unzipped and type: 

 * gradle clean 
 
 * gradle build
 
 
 The executable should be placed in the build/libs folder
 
 You can also build a fat jar, the GroovyLab-all.jar, that supports the DeepLearning4j effective Java library for Deep Learning (it provides very fast native numerical processing operations). Also, it supports Apache Spark for Big Data Processing.
 The Gradle command is:
 
  * gradle fatJar



a fat jar is produced with all the DeepLearning4j and Apache Spark functionality!

Therefore we can run Deep Learning an d Apache Spark algorithms in scripting mode,
with either Groovy or the JShell of Java 9+.

Note that F6 executes the selected script code with GroovyShell,
and F7 with the JShell.

Java machine learning examples of the project DeepLearning4jExamples can be executed easily. 

For example, at the current source code of GroovyLab, at the folder groovySciExamples/Jshell.DeepLearning4j the application example:

SVMLightExampleJava.gsci

is adapted from these examples.  
  
## Project Summary

The GroovyLab environment aims to provide a MATLAB/Scilab like scientific computing platform that is supported by a scripting engine implemented in Groovy language. The GroovyLab user can work either with a MATLAB-like command console, or with a flexible editor based on the rsyntaxarea  component, that offers more convenient code development. GroovyLab supports extensive plotting facilities and can exploit effectively a lot of powerful Java scientific libraries, as JLAPACK , Apache Common Maths , EJML , MTJ , NUMAL translation to Java , Numerical Recipes Java translation , Colt etc. Also, GroovyLab supports Computer Algebra based on the symja (http://code.google.com/p/symja/) project.

GroovyLab scripts can be easily combined with MATLAB, thus the scientist can combine the flexibility of Groovy with the computational effectiveness of MATLAB.

GroovyLab provides the GroovySci compiled scripting engine which is based mainly on GroovyShell , with extensions to provide MATLAB-like matrix operations.


GroovyLab supports indy (invoke dynamic) since it utilizes the indy version of Groovy. 
Also, by default performs computations using doubles instead of BigDecimals for speed. 
These settings are configurable. Also, very convenient for scripting, is the import and 
code buffering facility, described in ScriptingWithImportBuffering wiki page.


## GroovyLab Advantages

The main advantages of GroovySci that equip it with great potential are:

* The flexibility of the Groovy language, that offer many opportunities 
to implement convenient high-level scientific operators. 1. The vast Java based
scientific libraries with excellent code for many application domains. These libraries 
are directly available from GroovySci with the dynamic loading of the corresponding Java 
toolboxes.

* The user friendly Matlab-like environment of GroovyLab and the high quality 
scientific plotting support. 

* The syntax of Groovy is much like Java, and Java code can 
be easily mixed with Groovy, therefore engineers with Java background can become productive 
in short time. 

*  The speed of Groovy based scripting, with the invoke dynamic implementation, 
dynamic code comes generally close to Java code. 


## Documentation for GroovyLab

We started to write documentation with a draft user guide and at the wiki pages of the project, please see them.

GroovyLab provides a lot 
of on-line help and demo examples. The philosophy of GroovyLab is to exploit high quality 
Java scientific software. Although any Java scientific library can be utilized as toolbox,
GroovyLab includes by default some excellent Java scientific libraries. Work is in progress 
to utilize more effectively these libraries by designing Groovy based high level interfaces. 
These integrated libraries are: 

1. The Java translation of Numerical Recipes, Third Edition, 
C++ code by Huang Wen Hui 1. The NUMAL library described in the book: A Numerical Library in 
Java for Scientists & Engineers, Hang T. Lau, Chapman & Hall/CRC, 2004 

2. The Efficient Matrix 
Java Library, http://code.google.com/p/efficient-java-matrix-library/ 

3. The Matrix Toolkit for 
Java, http://code.google.com/p/matrix-toolkits-java/ 

4. The Parallel Colt Library, http://sites.google.com/site/piotrwendykier/software/parallelcolt 


5. Jsci - A Science API for Java, http://jsci.sourceforge.net/, provides mostly Wavelet Analysis Routines 

6. The JFreeChart plotting system, http://www.jfree.org/jfreechart/, provides plots, 
for some of which already exists Matlab-like interface 


## jlabgroovy (GroovyLab) Development discussion group Mailing List

http://groups.google.com/group/jlabgroovy

## GroovyLab Developer

Stergios Papadimitriou

Technology Education Institute
of East Macedonia and Thraki

Dept of Informatics 
and Computer Engineering

Greece

email: sterg@teiemt.gr
