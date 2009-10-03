public class FancyCall
{
    public static void main(String[] args)
    {
        if (Fancy.gimme42("abc42efg") == 42)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}