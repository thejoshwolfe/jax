public class ConstructorDeclarationCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= new ConstructorDeclaration().thing == 1;
        pass &= new ConstructorDeclaration(2).thing == 2;
        pass &= new ConstructorDeclaration("fake", 3).thing == 3;
        System.out.println(pass ? "+++ PASS" : "*** FAIL");
    }
}
