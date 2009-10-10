package net.wolfesoftware.jax.ast;

public class Assignment extends ParseElement
{
    public Id id;
    public Expression expression;
    public Assignment(Id id, Expression expression)
    {
        this.id = id;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        id.decompile(indentation, out);
        out.append(" = ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x15d9041a;
    public int getElementType()
    {
        return TYPE;
    }
}
