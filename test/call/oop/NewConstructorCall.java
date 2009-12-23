public class NewConstructorCall
{
    public static void main(String[] args)
    {
        if (NewConstructor.gimmeAThing().getClass() == Object.class &&
            NewConstructor.gimmeAMe().getClass() == NewConstructor.class &&
            NewConstructor.withArguments().getMessage().equals("teh message"))
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}