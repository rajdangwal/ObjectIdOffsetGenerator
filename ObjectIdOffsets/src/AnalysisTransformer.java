import java.util.*;

import java.io.PrintStream;
import java.io.File;

import soot.*;
import soot.JastAddJ.ReferenceType;
import soot.baf.VirtualInvokeInst;
import soot.jimple.AnyNewExpr;
import soot.jimple.ConcreteRef;
import soot.jimple.FieldRef;
import soot.jimple.Ref;
import soot.jimple.RetStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JNewExpr;
import soot.jimple.internal.JReturnStmt;
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
				if (printShimpleOnly)
					System.out.println(s + " Type : " + s.getClass());

//if(s instanceof JIdentityStmt)
//{
//BytecodeOffsetTag BOT = (BytecodeOffsetTag) u.getTag("BytecodeOffsetTag");
//System.out.println(s);
//}

				else {
					if (s instanceof JAssignStmt) {

						BytecodeOffsetTag BOT = (BytecodeOffsetTag) u.getTag("BytecodeOffsetTag");
// System.out.println("Initial Offset : " + BOT.toString());
						Value vR = ((JAssignStmt) s).getRightOp();
						Value vL = ((JAssignStmt) s).getLeftOp();
						Type tR = vR.getType();
						//if (!vR.toString().startsWith("Phi("))
							//System.out.println("Assign stmt : " + s);
//Class cR = vR.getClass();
//Type tL = vL.getType();
//Class cL = vL.getClass();
						boolean foundDestination = false;
						boolean foundSource = false;
						String source = "", localSource = "";
						String destination = "", localDestination = "";

						if (tR instanceof RefType) {
							if (!(((RefType) tR).getSootClass().isJavaLibraryClass())) {
								if (vR instanceof JNewExpr) {
									//// for statements like O1 = new Obj();
									source = vR.toString();
									destination = "";
									foundSource = true;
									localDestination = vL.toString();
									while (!foundDestination) {
										Unit insideUnit = null;
										Stmt insideStmt = null;
										if (blockIterator.hasNext()) {
											insideUnit = (Unit) blockIterator.next();
											insideStmt = (Stmt) insideUnit;
										}
										//System.out.println("Inside stmt 1 : " + insideStmt);
										BOT = (BytecodeOffsetTag) insideUnit.getTag("BytecodeOffsetTag");
										if (insideStmt instanceof JAssignStmt) {
											Value insideVR = ((JAssignStmt) insideStmt).getRightOp();
											Value insideVL = ((JAssignStmt) insideStmt).getLeftOp();
											if (insideVR.toString().equals(localDestination)) {
												destination = insideVL.toString();
												foundDestination = true;
											}
										} else if (insideStmt instanceof JReturnStmt) {
											//// similarly check for anonymous objects
											//// also for this situation final offset should not be considered.
											foundDestination = true;
											System.out.println("New object created but not assigned to any reference.");
										}
									}

									if (!destination.equals("")) {
										System.out.println("Assignment of new: " + destination + " = " + source);
										System.out.println("Final Offset : -" + BOT.toString());

									}
								}

								else if (!vL.toString().startsWith("$r") && !vR.toString().startsWith("$r")) {
									//// for statements like O1 = O2 or O1.O2 = O3 or O1 = O2.O3
									source = vR.toString();
									if (source.startsWith("virtualinvoke"))
										source = source.substring(14);
									else if (source.startsWith("staticinvoke"))
										source = source.substring(13);
									destination = vL.toString();
									if (!vR.toString().startsWith("Phi(")) {
										System.out.println("Simple assignment: " + destination + " = " + source);
										System.out.println("Final Offset : -" + BOT.toString());
									}
								}

								else {
									//// if both sides are not free of "$r" e.g. $r0 = O1 or $r0 = O1.O2
									localSource = vR.toString();
									localDestination = vL.toString();
									if (vR.toString().startsWith("Phi(")) {
										foundDestination = true;
										foundSource = true;
									}
									while (!(foundSource && foundDestination)) {
										Unit insideUnit = null;
										Stmt insideStmt = null;
										if (blockIterator.hasNext()) {
											insideUnit = (Unit) blockIterator.next();
											insideStmt = (Stmt) insideUnit;
										}
										if (insideStmt instanceof JAssignStmt) {
											//// if next statement is also a JAssignStmt
											//System.out.println("Inside stmt 2 : " +insideStmt);
											BOT = (BytecodeOffsetTag) insideUnit.getTag("BytecodeOffsetTag");
											Value insideVR = ((JAssignStmt) insideStmt).getRightOp();
											Value insideVL = ((JAssignStmt) insideStmt).getLeftOp();
											if (insideVL.toString().startsWith(localDestination)) {
												//// if LHS of next statement starts with $r0 or anything like that
												//// e.g $r0.O3 = O4
												int indexOfDot = insideVL.toString().indexOf(".");
												destination = localSource + "."
														+ insideVL.toString().substring(indexOfDot + 1);
												source = insideVR.toString();
												foundSource = true;
												foundDestination = true;
											} else if (insideVR.toString().startsWith(localDestination)
													|| (insideVR.toString().startsWith("virtualinvoke")
															&& insideVR.toString().contains(localDestination))) {
												//// if RHS of next statement starts with $r0 or anything like that
												//// e.g. $r1 = $r0.O4 or $r1 = virtualinvoke $r0....
												String furtherField = "";
												int indexOfDot = insideVR.toString().indexOf(".");
												if (indexOfDot != -1)
													furtherField = "." + insideVR.toString().substring(indexOfDot + 1);
												localSource = localSource + furtherField;
												if (localSource.startsWith("virtualinvoke"))
													localSource = localSource.substring(14);
												else if (localSource.startsWith("staticinvoke"))
													localSource = localSource.substring(13);
												localDestination = insideVL.toString();

												if (!localDestination.toString().startsWith("$r")) {
													//// If next statement is of type O5 = $r0.O5
													source = localSource;
													destination = localDestination;
													foundSource = true;
													foundDestination = true;
												} else if (destination != "" && indexOfDot == -1) {
													//// If next statement is of type $r1.O3 = $r0 //This will be final
													//// statement for
													//// some assignment.
													source = localSource;
													destination = destination + "." + insideVL.toString()
															.substring(insideVL.toString().indexOf(".") + 1);
													//// System.out.println("Source : " + source + "\nDest. : " +
													//// destination);
													foundSource = true;
													foundDestination = true;
												}
											}

											else {
												//// Next statement is free of LocalDestination both on LHS and RHS.
												//// destination is set here which will be checked above.
												//// for assignments like O1.O2.O3 = O4.O5
												destination = localSource;
												localSource = insideVR.toString();
												if (localSource.startsWith("virtualinvoke"))
													localSource = localSource.substring(14);
												else if (localSource.startsWith("staticinvoke"))
													localSource = localSource.substring(13);
												localDestination = insideVL.toString();
												//// System.out.println("dest : " + destination + " locSrc : " +
												//// localSource
												//// + " locDest : " + localDestination);
											}
										}

										else {
											if (insideUnit instanceof JInvokeStmt) {
												if (((JInvokeStmt) insideUnit).getInvokeExpr().toString()
														.startsWith("specialinvoke")) {
													//// To ignore the specialinvoke statement to handle assignments
													//// like
													//// O1.O2.O3 = new Obj()
													System.out.println(
															"Ignoring the specialinvoke statement after new object assignment");

												} else {
													//// To ignore the function invoke statements after assignment of
													//// obj
													//// to a temporary.
													foundSource = true;
													foundDestination = true;
													System.out.println("Function call Assignment: " + insideStmt);
												}
											} else if (insideStmt instanceof JReturnStmt) {
												System.out.println("Ignoring Return object statement");
												foundSource = true;
												foundDestination = true;
											}

											else {
												System.out.println(s);
												System.out.println("Unknown statement detected after assignment.");
											}

										}

									}
									if (!destination.equals("")) {
										System.out.println("Complex Assignment: " + destination + " = " + source);
										System.out.println("Final Offset : -" + BOT.toString());
									}
								}
							} else {
								System.out.println("JavaLibraryClassObject Assignment " + s);
							}

						} else {
							if (!vR.toString().startsWith("Phi("))
								System.out.println("Primitive Assignment " + s);
						}

					} // end of if(s instanceof JAssignStmt)

				} // end of if(!printShimpleOnly)*/

			} // end of while

		} // end of outer while
	}

}