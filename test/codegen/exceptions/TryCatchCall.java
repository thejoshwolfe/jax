public class TryCatchCall
{
    public static void main(String[] args)
    {
        if (TryCatch.gimme5() == 3)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}