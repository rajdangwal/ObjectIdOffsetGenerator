import soot.*;

public class SootMain
{

	public static void main(String args[])
	{

		if(args.length==0)
		{
			System.out.println("Usage: java SootMain class_to_analyze");
			System.exit(0);
		}
		else
		{
			System.out.println("Analyzing class: "+args[0]);
		}

		String mainClass, classPath;

		mainClass = args[0];

		classPath = "bin/application:/usr/local/java/jdk1.7.0_80/jre/lib/rt.jar:/usr/local/java/jdk1.7.0_80/jre/lib/jce.jar";
		//for shimple transformation you need to add rt.jar and jce.jar.

		//classPath = "classes/test";

		String sootArgs[] = {"-cp", classPath, "-pp", "-coffi", "-keep-bytecode-offset", "-main-class", mainClass, "-ws", "-process-dir", "bin/application", "-f", "S", "-p", "jb", "use-original-names", mainClass};

		AnalysisTransformer analysisTransformer = new AnalysisTransformer();

		PackManager.v().getPack("wstp").add(new Transform("wstp.cggenerator", analysisTransformer));

		soot.Main.main(sootArgs);


	}
}