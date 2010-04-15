package net.wolfesoftware.jax.ast;

import net.wolfesoftware.jax.semalysis.LocalVariable;

public class LocalVariableAssignment extends AbstractAssignment
{
    public LocalVariable variable;
    public LocalVariableAssignment(LocalVariable variable, String operator, Expression rightExpression)
    {
        super(operator, rightExpression);
        this.variable = variable;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append(variable.name);
        out.append(' ').append(operator).append(' ');
        rightExpression.decompile(indentation, out);
    }

    public static final int TYPE = 0x6b6e092b;
    public int getElementType()
    {
        return TYPE;
    }
}
