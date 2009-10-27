package net.wolfesoftware.jax.ast;

public class CatchBody extends ParseElement
{
    public int startOffset;
    public int endGotoOffset;

    public VariableDeclaration variableDeclaration;
    public Expression expression;
    public CatchBody(VariableDeclaration variableDeclaration, Expression expression)
    {
        this.variableDeclaration = variableDeclaration;
        this.expression = expression;
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        out.append("(");
        variableDeclaration.decompile(indentation, out);
        out.append(") ");
        expression.decompile(indentation, out);
    }

    public static final int TYPE = 0x10880372;
    public int getElementType()
    {
        return TYPE;
    }
}
