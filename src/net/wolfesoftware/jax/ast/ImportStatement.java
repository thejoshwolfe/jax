package net.wolfesoftware.jax.ast;

public class ImportStatement extends ParseElement
{
    public QualifiedName qualifiedName;

    public ImportStatement(QualifiedName qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("import ");
        qualifiedName.decompile(indentation, out);
        out.append(";");
    }

    public static final int TYPE = 0x0842027c;
    public int getElementType()
    {
        return TYPE;
    }

}
