mkdir -p classes
mkdir -p classes/test

javac -d classes/test -g:{source,lines,vars} src/test/*.java

if javac -d classes -g:{source,lines,vars} -cp soot-trunk.jar src/*.java; then

	if java -cp soot-trunk.jar:classes SootMain Test; then
		echo "Successful"
	else
		echo "[!] Runtime Error";
	fi;
else
	echo "[!] Compilation Error";
fi;
