
export LD_LIBRARY_PATH=$PWD/lib:$PWD/defaultToolboxes:$LD_LIBRARY_PATH
java  -XX:+UseNUMA -XX:+UseParallelGC -XX:+UseCompressedOops    -Djava.library.path=.:./lib:./defaultToolboxes  -Xss5m -Xms2000m -Xmx25000m -jar GroovyLab.jar
