public class LocalVariablesCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= LocalVariables.assignAndReturn() == 5;
        pass &= LocalVariables.assignLater() == 6;
        pass &= LocalVariables.doubleAssignment() == 2;
        System.out.println(pass ? "+++ PASS" : "*** FAIL");
    }
}