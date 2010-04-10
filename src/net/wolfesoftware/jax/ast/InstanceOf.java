package net.wolfesoftware.jax.ast;

public class InstanceOf extends ParseElement
{
    public Expression expression;
    public TypeId typeId;
    public InstanceOf(Expression expression, TypeId typeId)
    {
        this.expression = expression;
        this.typeId = typeId;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expression.decompile(indentation, out);
        out.append(" instanceof ");
        typeId.decompile(indentation, out);
    }

    public static final int TYPE = 0x15b103eb;
    public int getElementType()
    {
        return TYPE;
    }
}
