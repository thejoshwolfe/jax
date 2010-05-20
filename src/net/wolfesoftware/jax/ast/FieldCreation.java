package net.wolfesoftware.jax.ast;

public class FieldCreation extends FieldDeclaration
{
    public Expression expression;
    public FieldCreation(Modifiers fieldModifiers, TypeId type, String fieldName, Expression expression)
    {
        super(fieldModifiers, type, fieldName);
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        super.decompile(indentation, out);
        out.append(" = ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x226a051a;
    public int getElementType()
    {
        return TYPE;
    }
}
