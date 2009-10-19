public class FloatingTypesCall
{
    public static void main(String[] args)
    {
        if (FloatingTypes._3_14f() - 3.14f == 0.0f &&
            FloatingTypes._3_14() - 3.14 == 0.0 &&
            FloatingTypes.gimmeThatFloat(1.5f) - 1.5f == 0.0 &&
            FloatingTypes.gimmeThatDouble(1.3) - 1.3 == 0.0)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}