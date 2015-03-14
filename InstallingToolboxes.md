# Introduction #

`Installation of .jar toolboxes is very important and allows convenient access to Java scientific libraries. In ` **`Glab`** ` we can easily install .jar toolboxes that remain installed for later working sessions. We illustrate installation of a toolbox by means of an example.`


# Example #

`Suppose that we want to install the ` **`jWave.jar`** `toolbox. We proceed by opening the ` **`GroovySci Toolboxes`** `tab. A frame titled ` **`GroovySci toolboxes`** `is displayed that allows convenient installation of toolboxes.`

`The ` **`Specify toolboxes`** `button allows to browse the file system and to specify our toolbox. The ` **`Append to classpath`** `button appends the toolbox to the classpath of the GroovyShell without attempting to scan its classes. It is sufficient for our purpose of using the classes of the toolbox, since in order to use a toolbox the only requirement is to be accessible from the GroovyShell's classpath. The `**`Import toolboxes`** `button in addition scans the toolbox classes and if the ` **`Retrieve also methods`** `checkbox is checked, acquires information about the toolbox classes with Java's reflection. That information is displayed graphically by means of a Swing's JTree. `

`Let now proceed in installing our jWave.jar toolbox. We follow these steps: `

  1. `With ` **`Specify toolboxes`** `we specify the jWave.jar file.`
  1. `We can append the toolbox to the classpath of a fresh GroovyShell that however keeps the binding of the variables of the previous one, with ` **`Append to classpath`** `button. Alternatively, we can use the `**`Import toolboxes`** `button that attempts to scan the toolbox classes, presenting with useful information about toolbox classes, and methods if the ` **`Retrieve also methods`** `checkbox is checked.`

`After installing the toolbox we can execute the example code for the jWave toolbox (see the Wiki page). `

`Now we can exit GroovyLab. A file ` **`jlabUserPaths.txt`** `is created on disk, that contains the line: `

**`/home/sp/Downloads/jWave.jar`**

`This line specifies to jlabgroovy that on startup should init the GroovyShell's classloader to include also the jWave.jar toolbox. The next time we run jlabgroovy we can use jWave.jar without installation.`