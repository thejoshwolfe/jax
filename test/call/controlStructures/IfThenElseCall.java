public class IfThenElseCall
{
    public static void main(String[] args)
    {
        if (IfThenElse.test1() && 
            IfThenElse.test2() &&
            IfThenElse.test3() &&
            IfThenElse.test4() &&
            IfThenElse.test5())
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}