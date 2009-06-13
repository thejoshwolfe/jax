public class FunctinoInvocationCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= StringLiteral.isFunctionWorks();
        if (!pass)
            System.out.println("*** FAIL");
        System.out.println(FunctionInvocation.pass());
    }
}