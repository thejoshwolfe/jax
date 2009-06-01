public class LocalVariablesCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= LocalVariables.assignAndReturn() == 5;
        System.out.println(pass ? "+++ PASS" : "*** FAIL");
    }
}