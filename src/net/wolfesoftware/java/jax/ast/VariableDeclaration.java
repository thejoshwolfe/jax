package net.wolfesoftware.java.jax.ast;

import net.wolfesoftware.java.jax.lexiconizer.Type;

public class VariableDeclaration extends ParseElement
{
    public TypeId typeId;
    public Type type;

    public Id id;

    public VariableDeclaration(TypeId type, Id id)
    {
        this.typeId = type;
        this.id = id;
    }
    
    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        typeId.decompile(indentation, out);
        out.append(" ");
        id.decompile(indentation, out);
    }

    public static final int TYPE = 0x49f4078d;
    public int getElementType()
    {
        return TYPE;
    }
}
