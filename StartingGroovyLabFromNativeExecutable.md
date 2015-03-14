# `Starting GroovyLab  from native executable ` #


`For both Linux64 and Windows64 you can start GroovyLab from the supplied native executables, i.e. for Linux64 ` **`LinuxGroovyLab`** ` and for Windows64` **`WinGroovyLab.exe`**.

`However, for Linux64, the ` **`LD_LIBRARY_PATH`** ` variable should be updated properly to include the path: ` **`$JDK_HOME/jre/lib/amd64/server`**, ` e.g. for my installation I have in .bashrc: `

`export LD_LIBRARY_PATH=$CUDA_HOME/lib64:/usr/lib:$JDK_HOME/jre/lib/amd64/server/:$LD_LIBRARY_PATH`