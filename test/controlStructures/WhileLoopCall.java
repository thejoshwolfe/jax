import java.util.ArrayList;

public class WhileLoopCall
{
    public static void main(String[] args)
    {
        ArrayList lines = WhileLoop.splitLines("a\nbcd\nefg");
        if (lines.get(0).equals("a") &&
            lines.get(1).equals("bcd") &&
            lines.get(2).equals("efg"))
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}