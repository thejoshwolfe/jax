public class ThrowCall
{
    public static void main(String[] args)
    {
        boolean pass = true;

        Throwable t = new Throwable();
        try {
            Throw.throwThis(t);
            pass = false;
        } catch (Throwable e) {
            pass &= t == e;
        }
        if (pass)
            System.out.println("+++ PASS");
    }
}
