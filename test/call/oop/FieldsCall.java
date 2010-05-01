public class FieldsCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= Fields.getFive() == 5;
        pass &= Fields.ten == 10;
        Fields fields = new Fields();
        pass &= fields.getObject() != null;
        pass &= fields.getThing() == null;
        fields.setThing("asdf");
        pass &= fields.getThing() == "asdf";
        fields.setThisThing("fdsa");
        pass &= fields.getThisThing() == "fdsa";
        if (!pass)
            System.out.println("*** FAIL");
        System.out.println("+++ PASS");
    }
}
