public class ImportCall
{
    public static void main(String[] args)
    {
        if (Import.gimme42() == 42)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}