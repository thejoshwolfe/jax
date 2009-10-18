package net.wolfesoftware.jax.ast;

public class TypeCast extends ParseElement
{
    public TypeId typeId;
    public Expression expression;
    public TypeCast(TypeId typeId, Expression expression)
    {
        this.typeId = typeId;
        this.expression = expression;
    }

    public static final int TYPE = 0x0e19032e;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("(");
        typeId.decompile(indentation, out);
        out.append(")");
        expression.decompile(indentation, out);
    }
}
