package net.wolfesoftware.java.jax.ast;

public class ImportStatement extends ParseElement
{
    public FullClassName fullClassName;

    public ImportStatement(FullClassName fullClassName)
    {
        this.fullClassName = fullClassName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("import ");
        fullClassName.decompile(indentation, out);
        out.append(";");
    }

    public static final int TYPE = 0x0842027c;
    public int getElementType()
    {
        return TYPE;
    }

}
