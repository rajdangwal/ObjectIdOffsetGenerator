import java.io.*;

public class Test
{
	public Obj o1;
	public Obj2 o2;

	public void funTest(Obj oTemp1, Obj2 oTemp2)
	{
		o1 = oTemp1; //This is a write on a field of @04 object. o1 is now object @02.

		o2 = oTemp2; //This is also a write on a field of @04 object. o2 is now object @03.

		o1.funObj(); //This is a read on a field of @04 object

		o2.funObj2(); //This is a read on a field of @04 object

		Obj ofun1 = new Obj(5); // This is object @05
		ofun1.funObj();

		Obj ofun2 = ofun1; //ofun2 is now object @05.
		ofun2.funObj();
	}

	public static void main(String args[])
	{


		Obj oTemp1 = new Obj(10); // This is object @02
			
		Obj2 oTemp2 = new Obj2(); // This is object @03

		int x = oTemp2.funObj2ReturnInt(5,5);

		Obj3 oTemp3 = new Obj3();
		oTemp3.objectOfObj2 = oTemp2;

		Obj4 oTemp4 = new Obj4();
		oTemp4.objectOfObj3 = oTemp3;

		Obj5 oTemp5 = new Obj5();
		oTemp5.objectOfObj4 = oTemp4;

//		Obj5 oTemp6 = new Obj5();
//		oTemp6.objectOfObj4 = oTemp4;
//
//		oTemp5.objectOfObj4.objectOfObj3.objectOfObj2 = Obj3.funReturnStaticObj2();

		Obj6 oTemp6 = new Obj6();
		oTemp6 = (Obj6) oTemp5; ////86
		
		
//		try
//		{
//			oTemp6 = new Obj6();
//			oTemp6.funObj6();
//			oTemp6 = new Obj6();
//			oTemp6.funObj5();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//		
//		Obj4 oTemp7 = oTemp6.funReturnObj4();
//		
//		
////		Not considering chain assignments
//		oTemp5.objectOfObj4.objectOfObj3 = oTemp4.objectOfObj3 = oTemp3;
		
//		 oTemp5.objectOfObj4 = oTemp6.objectOfObj4;
//		 oTemp5.objectOfObj4.objectOfObj3 = oTemp3;
//		 oTemp5.objectOfObj4.objectOfObj3.objectOfObj2 = new Obj2();
//
//		 Obj2 oTemp7 = oTemp3.funReturnObj2();////131
//
//		 oTemp7 = oTemp4.objectOfObj3.funReturnObj2();
//
//		 oTemp7 = oTemp5.objectOfObj4.objectOfObj3.funReturnObj2();
//
//		 oTemp6.objectOfObj4 = oTemp5.funReturnObj4();
//
//		 oTemp4.objectOfObj3 = oTemp5.objectOfObj4.funReturnObj3();
//
//		 oTemp4.objectOfObj3.objectOfObj2 = oTemp3.funReturnObj2();
//
//		 oTemp4.objectOfObj3.objectOfObj2 = oTemp4.objectOfObj3.funReturnObj2();		
//
//		 oTemp5.objectOfObj4.objectOfObj3.objectOfObj2 = oTemp5.objectOfObj4.objectOfObj3.funReturnObj2();



		 oTemp5.objectOfObj4.objectOfObj3.objectOfObj2 = oTemp4.objectOfObj3.funReturnObj2();

		 oTemp2 = oTemp4.objectOfObj3.objectOfObj2;

		 //oTemp2 = oTemp6.objectOfObj4.objectOfObj3.objectOfObj2;

		 oTemp2.funObj2();

		 oTemp3.objectOfObj2 = oTemp4.objectOfObj3.objectOfObj2;

		 oTemp6.objectOfObj4.objectOfObj3 = oTemp4.objectOfObj3;

		 oTemp6.objectOfObj4.objectOfObj3.objectOfObj2 = oTemp4.objectOfObj3.objectOfObj2;


		 if((oTemp5.objectOfObj4 = oTemp6.objectOfObj4)==(oTemp4 = oTemp5.objectOfObj4))
		 {
			 System.out.println("Done");
		 }
		//------------------------------------------------------------------------------------------------------

		//Test tObj = new Test(); // This is object @04
		//tObj.funTest(oTemp1, oTemp2); // Here no read of object because it is local.

		//Obj3 oTemp3 = new Obj3();

		//oTemp3.objectOfObj2 = oTemp2;

		//Obj3 oTemp4 = new Obj3();

		//oTemp3 = oTemp4;

		//oTemp4.objectOfObj2 = oTemp3.objectOfObj2;

		//oTemp3.funObj3();

		// Obj5 oTemp7 = new Obj5();

		// oTemp7.objectOfObj4 = new Obj4();
		// oTemp7.objectOfObj4.objectOfObj3 = new Obj3();
		// oTemp7.objectOfObj4.objectOfObj3.objectOfObj2 = new Obj2();

		// oTemp2 = oTemp7.objectOfObj4.objectOfObj3.objectOfObj2;

		// oTemp2.funObj2();





		// Obj5 oTemp5 = new Obj5();
		// oTemp5.objectOfObj4 = new Obj4();
		// oTemp5.objectOfObj4.objectOfObj3 = oTemp4; // Here the objectOfObj2 of objectOfObj3 will be pointing to corresponding object in hierarchy of oTemp4

		// oTemp5.objectOfObj4.objectOfObj3.objectOfObj2 = oTemp6.objectOfObj3.objectOfObj2;

	}
}

class Obj
{
	int i=0; // initialized when the object is created. This is a write on field of object @02 and @05.

	public Obj(int i)
	{
		this.i = i;
	}

	public void funObj()
	{
		//i++; // Read and Write on field of @02 object. Read and Write on field of @05 object.
		System.out.println("Hello");
		i = 5;
	}
}

class Obj2
{
	int j=0; // initialized when the object is created

	public void funObj2()
	{
		j--; // Read and Write on field of @03 object.
	}

	public int funObj2ReturnInt(int a, int b)
	{
		j = j + a + b;
		return j;
	}

}

class Obj3
{
	Obj2 objectOfObj2;

	public void funObj3()
	{
		objectOfObj2.funObj2();
	}

	public Obj2 funReturnObj2()
	{
		return this.objectOfObj2;
	}

	public static Obj2 funReturnStaticObj2()
	{
		return new Obj2();
	}
}

class Obj4
{
	Obj3 objectOfObj3;

	public void funObj4()
	{
		objectOfObj3.funObj3();
	}

	public Obj3 funReturnObj3()
	{
		return this.objectOfObj3;
	}
}

class Obj5
{
	Obj4 objectOfObj4;

	public void funObj5()
	{
		objectOfObj4.funObj4();
	}

	public Obj4 funReturnObj4()
	{
		return this.objectOfObj4;
	}
}

class Obj6 extends Obj5
{
	public void funObj6()
	{
		System.out.println("Function in Obj6");
	}
}