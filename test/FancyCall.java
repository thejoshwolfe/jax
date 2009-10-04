public class FancyCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= Fancy.readFirstDigits("abc123efg456") == 123;
        pass &= Fancy.readFirstDigits("no digits here") == -1;
        if (pass)
            Fancy.printThis("+++ PASS");
    }
}
