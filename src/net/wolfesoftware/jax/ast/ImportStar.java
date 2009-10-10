package net.wolfesoftware.jax.ast;

public class ImportStar extends ParseElement
{
    public QualifiedName qualifiedName;

    public ImportStar(QualifiedName qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("import ");
        qualifiedName.decompile(indentation, out);
        out.append(".*;");
    }

    public static final int TYPE = 0x160e0416;
    public int getElementType()
    {
        return TYPE;
    }

}
