package net.wolfesoftware.jax.ast;

import java.util.ArrayList;
import net.wolfesoftware.jax.semalysizer.*;

public class TryCatch extends ParseElement
{
    public ArrayList<SecretLocalVariable> preserveTheseOperands = new ArrayList<SecretLocalVariable>(0);
    public Type type;
    public SecretLocalVariable valueStashVariable = null;

    public TryPart tryPart;
    public CatchPart catchPart;

    public TryCatch(TryPart tryPart, CatchPart catchPart)
    {
        this.tryPart = tryPart;
        this.catchPart = catchPart;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        tryPart.decompile(indentation, out);
        catchPart.decompile(indentation, out);
    }

    public static final int TYPE = 0x0df90323;
    public int getElementType()
    {
        return TYPE;
    }
}
