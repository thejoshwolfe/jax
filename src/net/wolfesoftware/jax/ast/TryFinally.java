package net.wolfesoftware.jax.ast;

public class TryFinally extends ParseElement
{
    public TryPart tryPart;
    public FinallyPart finallyPart;

    public TryFinally(TryPart tryPart, FinallyPart finallyPart)
    {
        this.tryPart = tryPart;
        this.finallyPart = finallyPart;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        tryPart.decompile(indentation, out);
        finallyPart.decompile(indentation, out);
    }

    public static final int TYPE = 0x15bb040f;
    public int getElementType()
    {
        return TYPE;
    }
}
