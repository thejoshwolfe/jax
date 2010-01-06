package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysizer.Type;

public class TypeId extends ParseElement
{
    public Type type;
    public ScalarType scalarType;
    public ArrayDimensions arrayDimensions;

    public TypeId(ScalarType scalarType, ArrayDimensions arrayDimensions)
    {
        this.scalarType = scalarType;
        this.arrayDimensions = arrayDimensions;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        scalarType.decompile(indentation, out);
        arrayDimensions.decompile(indentation, out);
    }

    public static final int TYPE = 0x040401a3;
    public int getElementType()
    {
        return TYPE;
    }

    public static TypeId fromId(Id id) {
        return new TypeId(new ScalarType(id), ArrayDimensions.EMPTY);
    }
}
