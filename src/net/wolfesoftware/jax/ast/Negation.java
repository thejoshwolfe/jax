package net.wolfesoftware.jax.ast;

public class Negation extends ParseElement
{
    public byte instruction;

    public Expression expression;
    public Negation(Expression expression)
    {
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append('-');
        expression.decompile(indentation, out);
    }
    public static final int TYPE = 0x0de10336;
    public int getElementType()
    {
        return TYPE;
    }
}
