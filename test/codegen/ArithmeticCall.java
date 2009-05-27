public class ArithmeticCall
{
    public static void main(String[] args)
    {
        if (Arithmetic.addition3() == 3 && 
            Arithmetic.addition15() == 15 &&
            Arithmetic.addition21() == 21)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}