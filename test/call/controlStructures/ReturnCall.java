public class ReturnCall {
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= !helper(new boolean[] {false, false})[1];
        pass &= helper(new boolean[] {false, true})[1];
        pass &= helper(new boolean[] {true, false})[1];
        pass &= helper(new boolean[] {true, true})[1];
        pass &= Return.returnExpression(null) == null;
        pass &= Return.returnExpression("").equals("");
        pass &= Return.returnExpression("a").equals("");
        pass &= Return.returnExpression("ab").equals("b");
        if (pass)
            System.out.println("+++ PASS");
    }
    private static boolean[] helper(boolean[] array)
    {
        Return.returnVoid(array);
        return array;
    }
}