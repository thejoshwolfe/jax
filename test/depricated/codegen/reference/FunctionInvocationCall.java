public class FunctionInvocationCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= FunctionInvocation.isFunctionWorks();
        if (!pass)
            System.out.println("*** FAIL");
        System.out.println(FunctionInvocation.pass());
    }
}