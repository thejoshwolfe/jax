public class ArrayDereferenceCall
{
    public static void main(String[] args)
    {
        String[] arr = new String[] { "first", "second" };
        int[][][] arr2 = {null, null, null, null, {null, null, {1234}}};
        boolean pass =
            ArrayDereference.secondOne(arr) == "second" ||
            ArrayDereference.fifthThird(arr2)[0] == 1234;
        if (pass)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}