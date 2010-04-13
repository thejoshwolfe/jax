package net.wolfesoftware.jax.ast;

public class FieldCreation extends FieldDeclaration
{
    public Expression expression;
    public FieldCreation(FieldModifiers fieldModifiers, TypeId type, Id id, Expression expression)
    {
        super(fieldModifiers, type, id);
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
