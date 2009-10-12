public class ArrayDereferenceCall
{
    public static void main(String[] args)
    {
        String[] arr = new String[] { "first", "second" };
        if (ArrayDereference.secondOne(arr) == "second")
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}