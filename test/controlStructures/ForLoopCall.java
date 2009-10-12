import java.util.ArrayList;

public class ForLoopCall
{
    public static void main(String[] args)
    {
        ArrayList list = new ArrayList();
        int number = 5;
        ForLoop.loadArrayList(list, number);
        boolean pass = true;
        for (int i = 0; i < number; i++)
            pass &= list.get(i).equals(String.valueOf(i));
        if (pass)
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}