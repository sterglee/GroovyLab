# `Transform GroovySci scripts to standalone applications` #

`One of the important advantages of GroovyLab is that the scripts can be converted easily to standalone applications, i.e. applications that run on the plain Java platform, without requiring the GroovyLab environment. We illustrate the way to achieve that by means of an example.`

`Suppose that we have the following GroovySci script:`

```
 def  t = inc(0, 0.01, 10)
 def  x = sin(0.23*t)+9.8*cos(1.12*t)
 plot(t,x)
```

`We want to run that as a standalone application.`

`The first step is to choose from the` **`Application`** `menu the ` **`Create stand alone application from the GroovySci script code`** ` option that transforms the Script to standalone application. GroovyLab asks for the name of the class that will wrap the script code, suppose that we use e.g. ` _`plots.`_ `Also a shell script file named with an extension .sh on Unix like systems and .bat on Windows, is created automatically. That file can be used to execute the application directly from the operating system without GroovyLab.`

`GroovyLab compiles automatically the generated Groovy code that wraps our GroovySci script and produces a corresponding class file on disk. `

`The second (and final) step is to run our standalone application using the script file, e.g. `
```
sh plots.sh
```

`If we need to run our application (e.g. the ` _`plots`_ `application), to an arbitrary directory, we should be careful to copy the` _`plots.sh`_ `script, the` _`plots.class`_ `compiled class, and the ` _`lib`_ `directory also. The later directory is required, since it contains the GroovyLab's libraries and the Groovy .jar file. `