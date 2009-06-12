package net.wolfesoftware.java.jax.ast;

public class ClassDeclaration extends ParseElement
{
    public ClassBody classBody;
    public ClassDeclaration(ClassBody classBody)
    {
        this.classBody = classBody;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        classBody.decompile(indentation, out);
    }

    public static final int TYPE = 0x3465065d;
    public int getElementType()
    {
        return TYPE;
    }

}
