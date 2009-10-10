package net.wolfesoftware.jax.ast;

public class ImportClass extends ParseElement
{
    public QualifiedName qualifiedName;

    public ImportClass(QualifiedName qualifiedName)
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

    public static final int TYPE = 0x1a290472;
    public int getElementType()
    {
        return TYPE;
    }

}
