package net.wolfesoftware.jax.ast;

public class TryCatchFinally extends ParseElement
{
    public TryPart tryPart;
    public CatchPart catchPart;
    public FinallyPart finallyPart;

    public TryCatchFinally(TryPart tryPart, CatchPart catchPart, FinallyPart finallyPart)
    {
        this.tryPart = tryPart;
        this.catchPart = catchPart;
        this.finallyPart = finallyPart;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        tryPart.decompile(indentation, out);
        catchPart.decompile(indentation, out);
        finallyPart.decompile(indentation, out);
    }

    public static final int TYPE = 0x2e8d05f2;
    public int getElementType()
    {
        return TYPE;
    }
}
