package net.wolfesoftware.jax.ast;

public class ImportStarStatement extends ParseElement
{
    public QualifiedName qualifiedName;

    public ImportStarStatement(QualifiedName qualifiedName)
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

    public static final int TYPE = 0x4cea07cb;
    public int getElementType()
    {
        return TYPE;
    }

}
