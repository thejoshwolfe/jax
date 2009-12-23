public class ShortCircuitCall
{
    public static void main(String[] args)
    {
        if (!ShortCircuit.equalsFive(null) && 
                !ShortCircuit.equalsFive("Three") &&
                ShortCircuit.equalsFive("Five") &&
                ShortCircuit.notEqualsFive(null) &&
                ShortCircuit.notEqualsFive("Three") &&
                !ShortCircuit.notEqualsFive("Five"))
            System.out.println("+++ PASS");
        else
            System.out.println("*** FAIL");
    }
}