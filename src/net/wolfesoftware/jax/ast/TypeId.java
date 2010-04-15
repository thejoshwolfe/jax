package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.*;

public class TypeId extends ParseElement
{
    public Type type;
    public String scalarTypeName;
    public ArrayDimensions arrayDimensions;

    public TypeId(String scalarTypeName, ArrayDimensions arrayDimensions)
    {
        this.scalarTypeName = scalarTypeName;
        this.arrayDimensions = arrayDimensions;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(scalarTypeName);
        arrayDimensions.decompile(indentation, out);
    }

    public static final int TYPE = 0x040401a3;
    public int getElementType()
    {
        return TYPE;
    }

    public static TypeId fromName(String name)
    {
        return new TypeId(name, ArrayDimensions.EMPTY);
    }
}
