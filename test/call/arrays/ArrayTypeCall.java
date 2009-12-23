public class ArrayTypeCall
{
    public static void main(String[] args)
    {
        int[] thing1 = new int[] {1,2};
        String[][][] thing2 = new String[0][][];
        boolean pass = 
            ArrayType.gimmeThat(thing1) == thing1 &&
            ArrayType.gimmeThatToo(thing2) == thing2;
        if (pass)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}