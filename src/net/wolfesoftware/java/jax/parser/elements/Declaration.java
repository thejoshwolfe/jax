package net.wolfesoftware.java.jax.parser.elements;

public class Declaration extends ParseElement
{
    public TypeId type;
    public Id id;
    public Expression expression;

    public Declaration(TypeId type, Id id, Expression expression)
    {
        this.type = type;
        this.id = id;
        this.expression = expression;
    }

    public static final int TYPE = 0x19530467;
    public int getElementType()
    {
        return TYPE;
    }

    @Override
    protected void decompile(String indentation, StringBuffer out)
    {
        type.decompile(indentation, out);
        out.append(" ");
        id.decompile(indentation, out);
        out.append(" = ");
        expression.decompile(indentation, out);
    }
}
