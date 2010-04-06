package net.wolfesoftware.jax.ast;

public class PackageStatement extends ParseElement
{
    public QualifiedName qualifiedName;

    public PackageStatement(QualifiedName qualifiedName)
    {
        this.qualifiedName = qualifiedName;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("package ");
        qualifiedName.decompile(indentation, out);
        out.append(";");
    }

    public static final int TYPE = 0x34990662;
    public int getElementType()
    {
        return TYPE;
    }

}
