public class StringLiteralCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= StringLiteral.helloWorld1().equals("Hello World!");
        pass &= StringLiteral.helloWorld1().equals("Hello World!");
        if (!pass)
            System.out.println("*** FAIL");
        System.out.println(StringLiteral.pass());
    }
}