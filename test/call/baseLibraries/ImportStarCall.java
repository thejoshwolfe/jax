public class ImportStarCall
{
    public static void main(String[] args)
    {
        if (ImportStar.gimme42() == 42)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}