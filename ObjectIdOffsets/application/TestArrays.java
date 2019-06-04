
public class TestArrays
{
	public static ObjClass[] funReturnObjArr()
	{
		return new ObjClass[10];
	}
	public static void main(String args[])
	{

		int[] intArr = new int[10];
		int[] intArr2 = new int[20];

		ObjClass[] objArr = new ObjClass[10];
		ObjClass[] objArr2 = new ObjClass[20];
		
		ObjClass[][] objArr3D = new ObjClass[10][10];
		
		

		for(int i=0; i<10; i++)
		{
			intArr[i] = i;
			objArr[i] = new ObjClass();
		}

		for(int i=0; i<20; i++)
		{
			intArr2[i] = i;
			objArr2[i] = new ObjClass();
		}
		
		for(int i=0; i<10; i++)
		{
			for(int j=0; j<10; j++)
			{
				objArr3D[i][j] = new ObjClass();
			}
		}

		objArr = objArr2;
		
		intArr = intArr2;
		
		ObjClass[] objArr3 = new ObjClass[30];
		ArrayContainer AC = new ArrayContainer(objArr3);
		
		objArr2 = AC.objArr3;
		
		AC.objArr3 = objArr;
		
		ArrayContainer AC2 = new ArrayContainer(objArr2);
		
		AC2.objArr3 = AC.objArr3;

		ArrayContainerContainer ACC = new ArrayContainerContainer(AC2);
		
		objArr = ACC.AC.objArr3;
		
		AC.objArr3 = ACC.AC.objArr3;
		
		ArrayContainerContainer ACC2 = new ArrayContainerContainer(AC);
		
		ACC2.AC.objArr3 = ACC.AC.objArr3;
		
		objArr = funReturnObjArr();
		
		objArr = AC.funReturnObjArr();
		
		objArr = ACC.AC.funReturnObjArr();
		
		objArr[0].s = "Rajendra";
		
		AC.objArr3 = ACC2.AC.funReturnObjArr();
		
		ACC.AC = ACC2.AC;

	}
}


class ObjClass
{
	String s;

	public void fun()
	{
		System.out.println("Fun called.");
	}
}


class ArrayContainer
{
	ObjClass[] objArr3;
	
	public ArrayContainer(ObjClass[] objArr)
	{
		objArr3 = objArr;
		
	}
	
	public ObjClass[] funReturnObjArr()
	{
		return objArr3;
	}
	
	
}

class ArrayContainerContainer
{
	ArrayContainer AC;
	
	public ArrayContainerContainer(ArrayContainer AC)
	{
		this.AC = AC;
	}

}



