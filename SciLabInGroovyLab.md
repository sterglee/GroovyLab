# Introduction #

**`SciLab`** ` is a powerful open source scientific programming environment. The recent versions of SciLab have efficient built-in operations, and a rich number of libraries and toolboxes. `

`We demonstrate how the GroovyLab user can execute scripts using SciLab.`


## `Installing SciLab support for Windows. ` ##

`The first step is to download and install SciLab for Windows. `

`Suppose it is installed on folder: ` _`d:\\SciLab`_ ` for example (as its is in my installation ).`

`The first step is to update the ` **`PATH`** ` environment variable, from the Control Panel, appending to it the directories:` **`d:\\SciLab\\bin`** ` and ` **`d:\\SciLab`** `(of course replacing d:\\SciLab with the directories of your own installation).`

`The second (and final!) step is to update the GroovyLab startup script by appending the same directories to the java.library.path, for example for my own system:`

`java     -server  -d64   -XX:+TieredCompilation -XX:+UseNUMA -XX:+UseParallelGC -XX:+UseCompressedOops -XX:+AggressiveOpts -Djava.library.path=d://SciLab//bin;d://SciLab;./lib;./libMedia64;./libCUDA;. -Xss5m -Xms3000m -Xmx9900m  -jar GroovyLab.jar`


`After these two simple steps, GroovyLab can utilize and cooperate with the SciLab engine!!`

## Examples ##

```




import org.scilab.modules.javasci.Scilab
import org.scilab.modules.types.ScilabType
import org.scilab.modules.types.ScilabDouble

sci = new Scilab()
sci.open()

a = new ScilabDouble(java.lang.Math.PI)
str = sci.put("a", a)
sci.exec("b = sin(a)")
b = sci.get("b")
b
sci.close()

```

## `SVD with SciLab` ##

```


// perform SVD using a SciLab - GroovyLab connection 

initSciLabConnection()    // init a connection wth SciLab


x = rand(500,500)  // create a random vector
s = "[ux, wx, vx] = svd(x); "    // string to evaluate in SciLab

tic()
scieval( s, ["x"], ["ux", "wx", "vx"])  // ["x"] is the list of input parameters from GroovyLab to SciLab
										  //  ["ux", "wx", "vx"] is the list of result parameters from SciLab to GroovyLab
tmsci = toc()

uxd = ux.getRealPart()  
wxd = wx.getRealPart()
vxd = vx.getRealPart()

sbz = uxd*wxd*t(vxd)-x    // should be zero
max(max(sbz))	

// test now time for Java SVD
tic()
svdx = svd(x)
tmj = toc()
	

```