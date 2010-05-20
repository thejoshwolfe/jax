public class DoBreakContinueCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= DoBreakContinue.findThis(new String[] { "a", null, "b", "c" }, "b") == 3;
        boolean[] makeThisSayTrue = { false };
        DoBreakContinue.showOff(makeThisSayTrue);
        pass &= makeThisSayTrue[0];
        if (pass)
            System.out.println("+++ PASS");
    }
}
