public class FancyCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= Fancy.readFirstDigits("abc123efg456") == 123;
        pass &= Fancy.readFirstDigits("no digits here") == -1;
        pass &= Fancy.dareYouToParseThisInt("1234").equals("1234");
        pass &= Fancy.dareYouToParseThisInt("not a number").equals("Nice Try!\n");
        pass &= Fancy.meaningOfLife() == 42;
        if (pass)
            Fancy.printThis("+++ PASS");
    }
}
