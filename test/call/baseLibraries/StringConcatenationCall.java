public class StringConcatenationCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= StringConcatenation.addThese("asdf", "qwer").equals("asdfqwer");
        String s = StringConcatenation.addThese(true, null, (byte)1, (short)2, 3, 4, 5.0f, 6.0, 'a', Integer.valueOf(7));
        pass &= s.equals("truenull12345.06.0a7");
        if (pass)
            System.out.println(StringConcatenation.pass());
        else
            System.out.println("*** FAIL");
    }
}