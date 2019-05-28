rm -r bin
rm -r sootOutput
mkdir -p bin
mkdir -p bin/application

javac -d bin/application -g:{source,lines,vars} application/test/*.java

javac -d bin -g:{source,lines,vars} -cp soot-trunk.jar src/*.java
