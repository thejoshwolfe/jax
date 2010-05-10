public class InitializersCall {
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= Initializers.five == 5;
        Initializers a = new Initializers(); 
        pass &= a.four == 4 && a.three == 3;
        a = new Initializers("");
        pass &= a.four == 4 && a.three == 3;
        System.out.println(pass ? "+++ PASS" : "*** FAIL");
    }
}