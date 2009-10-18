public class TypeCastingCall
{
    public static void main(String[] args)
    {
        if (TypeCasting.castToString("abc") == "abc" &&
            TypeCasting.negativeThree() == -3)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}