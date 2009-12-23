public class ArithmeticCall
{
    public static void main(String[] args)
    {
        if (Arithmetic.addition3() == 3 && 
            Arithmetic.addition15() == 15 &&
            Arithmetic.addition21() == 21 &&
            Arithmetic.subtraction9() == 9 &&
            Arithmetic.multiplication24() == 24 &&
            Arithmetic.division5() == 5 &&
            Arithmetic.mixed42() == 42 &&
            Arithmetic.negative3() == -3 &&
            Arithmetic.inc3() == 3 &&
            Arithmetic.inc4() == 4 &&
            Arithmetic.decr1() == 1 &&
            Arithmetic.decr8() == 8)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}