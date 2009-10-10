package net.wolfesoftware.jax.ast;

public class VariableCreation extends ParseElement
{
    public VariableDeclaration variableDeclaration;
    public Expression expression;
    public VariableCreation(VariableDeclaration variableDeclaration, Expression expression)
    {
        this.variableDeclaration = variableDeclaration;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        variableDeclaration.decompile(indentation, out);
        out.append(" = ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x3507065c;
    public int getElementType()
    {
        return TYPE;
    }

}
