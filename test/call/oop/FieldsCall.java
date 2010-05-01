public class FieldsCall
{
    public static void main(String[] args)
    {
        boolean pass = true;
        pass &= Fields.getFive() == 5;
        pass &= Fields.ten == 10;
        pass &= Fields.getObject() != null;
        pass &= Fields.getThing() == null;
        pass &= Fields.setThing("asdf");
        pass &= Fields.getThing() == "asdf";
        pass &= Fields.setThisThing("fdsa");
        pass &= Fields.getThisThing() == "fdsa";
        if (!pass)
            System.out.println("*** FAIL");
        System.out.println(Fields.pass());
    }
}
