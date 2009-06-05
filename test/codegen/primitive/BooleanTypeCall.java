public class BooleanTypeCall
{
    public static void main(String[] args)
    {
        if (BooleanType.thisIsTrue() && !BooleanType.thisIsFalse())
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}