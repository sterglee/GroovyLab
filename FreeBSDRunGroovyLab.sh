java   -XX:+UseNUMA -XX:+UseParallelGC -XX:+UseCompressedOops -XX:+AggressiveOpts   -Djava.library.path=./libCUDA:.:./lib -Xss5m -Xms2000m -Xmx25000m -jar GroovyLab.jar
