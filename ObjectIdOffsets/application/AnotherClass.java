
public class AnotherClass
{
	static int a;
	
//  public void printInstrumented(int i)
//  {
//  	System.out.println(i);
//  
//  }

    public AnotherClass()
    {
    	a = 10;
    }
    
    public void fun1()
    {
    	int a = 10;
    	int b = 20;
    	System.out.println("a + b = " + a+b);
    	if (a > 50) {fun1();} 
    	else { return;}
    	System.out.println("First");
    }
    
    public static void main(String args[])
    {
    	System.out.println("a = " + a);
    	AnotherClass aObj = new AnotherClass();
    	aObj.fun1();
    	System.out.println("a = " + a);
    	
    	A obj1 = new A();
    	obj1.funA();
    	
    	B obj1B = new B();
    	obj1.objB = obj1B;
    	
    	A obj2 = obj1;
    	obj2.funA();
    	
    	obj2.objB.funB();
    	//int x = 10;
    	//System.out.println("Value of x is " + x);
    }

}

class A
{
	int x = 5;
	B objB;
	
	public void funA()
	{
		System.out.println(x);
	}
}

class B
{
	int y = 10;
	
	public void funB()
	{
		System.out.println(y);
	}

    public int funB2(int a, int b)
    {
        return y+a+b;
    }
}
