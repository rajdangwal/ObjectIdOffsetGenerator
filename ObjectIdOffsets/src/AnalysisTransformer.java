import java.util.*;

import java.io.PrintStream;
import java.io.File;

import soot.*;

import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.shimple.internal.SPhiExpr;
import soot.tagkit.BytecodeOffsetTag;
import soot.toolkits.graph.*;
import soot.util.Chain;

public class AnalysisTransformer extends SceneTransformer {
	BriefBlockGraph basicBlockGraph; // CFG for Basic Blocks
	Set<String> ObjectIdOffsets;

	@Override
	protected void internalTransform(String phaseName, Map options) {
		try {
			G.v().out = new PrintStream(new File("/dev/null"));
		} catch (Exception e) {
		}

		Chain<SootClass> classChain = Scene.v().getApplicationClasses();
		for (SootClass sClass : classChain) {
			if (sClass.isConcrete()) {
				System.out.print("\n\nClass: " + sClass.getName());
				List<SootMethod> methodList = sClass.getMethods();
				for (SootMethod sMethod : methodList) {
					System.out.println("\nMethod: " + sMethod.getName() + " : ");

					getObjectIdOffsets(sMethod);
				}
			}
		}
	}

	protected void getObjectIdOffsets(SootMethod traverseMethod) {
		basicBlockGraph = new BriefBlockGraph(traverseMethod.retrieveActiveBody());

		Iterator<Block> blockGraphIterator = basicBlockGraph.iterator();

		boolean printShimpleOnly = false;

		while (blockGraphIterator.hasNext()) {
			Block b = blockGraphIterator.next();
			Iterator<Unit> blockIterator = b.iterator();
//System.out.println("BB : "+b.getIndexInMethod());
			while (blockIterator.hasNext()) {
				Unit u = (Unit) blockIterator.next();
				Stmt s = (Stmt) u;

//System.out.println("Stmt : " + s);
				if (printShimpleOnly) {
					System.out.println(s + " Type : " + s.getClass());
					if (s instanceof JAssignStmt) {
						Value vleft = ((JAssignStmt) s).getLeftOp();
						Value vright = ((JAssignStmt) s).getRightOp();
						System.out.println("Statement : " + s);
						System.out.println("Class of vleft: " + vleft.getClass());
						System.out.println("Type of vleft: " + vleft.getType());
						System.out.println("Class of vright: " + vright.getClass());
						System.out.println("Type of vright: " + vright.getType());
						BytecodeOffsetTag BOT = (BytecodeOffsetTag) u.getTag("BytecodeOffsetTag");
						// System.out.println("Offset : " + BOT.toString());
					}
				}

				else {
					if (s instanceof JAssignStmt) {
						BytecodeOffsetTag BOT = (BytecodeOffsetTag) u.getTag("BytecodeOffsetTag");
						Value vR = ((JAssignStmt) s).getRightOp();
						Value vL = ((JAssignStmt) s).getLeftOp();
						Type tR = vR.getType();

						if (vL instanceof JInstanceFieldRef) {
							if (tR instanceof ArrayType
									|| (tR instanceof RefType && !((RefType) tR).getSootClass().isJavaLibraryClass())) {
								//System.out.println("JInstanceFieldRef Identified : " + BOT.toString());
								System.out.println("-"+BOT.toString());
							}
						}

						else if (vL instanceof JArrayRef) {
							if (tR instanceof RefType || tR instanceof ArrayType) {
								//System.out.println("JArrayRef Identified : " + BOT.toString());
								System.out.println("-"+BOT.toString());
							}
						}

						else if (vL instanceof JimpleLocal) {
							if (!vL.toString().startsWith("$r") && !(vR instanceof SPhiExpr)) {
								if (tR instanceof ArrayType || (tR instanceof RefType
										&& !((RefType) tR).getSootClass().isJavaLibraryClass())) {
									//System.out.println("Non-Java Lib. Ref. Type or Array Type JimpleLocal Identified : "
									//		+ BOT.toString());
									System.out.println("-"+BOT.toString());
								}
							}
						}

					}
				}

//if(s instanceof JIdentityStmt)
//{
//BytecodeOffsetTag BOT = (BytecodeOffsetTag) u.getTag("BytecodeOffsetTag");
//System.out.println(s);
//}

			} // end of while

		} // end of outer while
	}

}