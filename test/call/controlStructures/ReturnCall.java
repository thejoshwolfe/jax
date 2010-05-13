public class ReturnCall {
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= !Return.returnVoid(new boolean[] {false, false})[1];
        pass &= Return.returnVoid(new boolean[] {false, true})[1];
        pass &= Return.returnVoid(new boolean[] {true, false})[1];
        pass &= Return.returnVoid(new boolean[] {true, true})[1];
        pass &= Return.returnExpression(null) == null;
        pass &= Return.returnExpression("").equals("");
        pass &= Return.returnExpression("a").equals("");
        pass &= Return.returnExpression("ab").equals("b");
    }
}