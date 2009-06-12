package net.wolfesoftware.java.jax.ast;

public class Root extends ParseElement
{
    public CompilationUnit content;
    public Root(CompilationUnit content)
    {
        this.content = content;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        content.decompile(indentation, out);
    }

    public static final int TYPE = 0x03eb01a5;
    public int getElementType()
    {
        return TYPE;
    }
}
