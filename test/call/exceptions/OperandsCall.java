public class OperandsCall
{
    public static void main(String[] args)
    {
        if (Operands.gimme5() == 5)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}