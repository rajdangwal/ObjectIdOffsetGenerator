
public class Test2
{
	public static void main(String args[])
	{

		ObjectClass obj1 = new ObjectClass();
		Child objChild = new Child(obj1);
		objChild.funBase();

		Base objBase = new Base();
		objBase.funBase();

		objBase = objChild;
	}
}


class Base
{
	ObjectClass obj;
	int i;
	String s;
	ObjectClass[] objArr;
	int[] iArr;

	public void funBase()
	{
		ObjectClass objLocal = new ObjectClass();
		objLocal.str = "ObjectClass object local to Base.funBase()";

		obj = new ObjectClass();
		obj.str = "First Object of ObjectClass";
		i = 10;
		s = new String("Rajendra");
		objArr = new ObjectClass[2];
		iArr = new int[2];

		objArr[0] = obj;
		objArr[1] = objArr[0];

		iArr[0] = 0;
		iArr[1] = 1;

		iArr[0] = iArr[1];

		try
		{
			obj = objLocal;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		System.out.println("funBase called. "+ "value of iArr[0] = "+ iArr[0]);


	}
}

class Child extends Base
{
	ObjectClass obj2;

	public Child(ObjectClass obj2)
	{
		this.obj2 = obj2;
	}

	public void funChild()
	{
		obj2.str = "ObjectClass object in Child";
		System.out.println("funChild called.");
	}
}

class ObjectClass
{
	String str;
}