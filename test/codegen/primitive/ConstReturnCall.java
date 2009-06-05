public class ConstReturnCall
{
    public static void main(String[] args)
    {
        if (ConstReturn.gimme7() == 7 && ConstReturn.iCanHas42() == 42)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}