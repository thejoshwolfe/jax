public class TryCatchCall
{
    public static void main(String[] args)
    {
        if (TryCatch.gimme5() == 5)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}