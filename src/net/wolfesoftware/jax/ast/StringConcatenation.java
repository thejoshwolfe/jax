package net.wolfesoftware.jax.ast;

import java.util.ArrayList;
import net.wolfesoftware.jax.semalysis.*;

public class StringConcatenation extends ParseElement
{
    private ArrayList<Expression> expressions = new ArrayList<Expression>();
    public StringConcatenation(Expression firstExpression)
    {
        expressions.add(firstExpression);
    }

    public void append(Expression expression)
    {
        expressions.add(expression);
    }

    @Override
    protected void decompile(String indentation, StringBuilder out)
    {
        expressions.get(0).decompile(indentation, out);
        for (int i = 1; i < expressions.size(); i++) {
            out.append(" + ");
            expressions.get(i).decompile(indentation, out);
        }
    }

    public static final int TYPE = 0x4c6907be;
    public int getElementType()
    {
        return TYPE;
    }

    /**
     * <code>new StringBuilder(a).append(b).append(c).toString()</code>
     */
    public Expression getEffectiveExpression()
    {
        Expression expression = newStringBuilder(stringValueOf(expressions.get(0)));
        for (int i = 1; i < expressions.size(); i++)
            expression = stringBuilderAppend(expression, expressions.get(i));
        expression = stringBuilderToString(expression);
        return expression;
    }

    private static Expression newStringBuilder(Expression stringArg)
    {
        // TODO: cache this constructor
        Constructor constructor = RuntimeType.STRING_BUILDER.resolveConstructor(new Type[] { RuntimeType.STRING });
        ConstructorInvocation constructorInvocation = ConstructorInvocation.fromConstructor(constructor, stringArg);
        Expression newExpression = new Expression(constructorInvocation);
        newExpression.returnBehavior = ReturnBehavior.STRING;
        return newExpression;
    }

    private static Expression stringValueOf(Expression expression)
    {
        Type type = expression.returnBehavior.type;
        if (type == RuntimeType.STRING)
            return expression;
        Method method = RuntimeType.STRING.resolveMethod("valueOf", new Type[] { type });
        Expression newExpression = new Expression(StaticMethodInvocation.fromMethod(method, expression));
        newExpression.returnBehavior = ReturnBehavior.STRING;
        return newExpression;
    }

    private static Expression stringBuilderAppend(Expression stringBuilderExpression, Expression someTypeArg)
    {
        Method appendMethod = RuntimeType.STRING_BUILDER.resolveMethod("append", new Type[] { someTypeArg.returnBehavior.type });
        InstanceMethodInvocation instanceMethodInvocation = InstanceMethodInvocation.fromMethod(stringBuilderExpression, appendMethod, someTypeArg);
        Expression newExpression = new Expression(instanceMethodInvocation);
        newExpression.returnBehavior = ReturnBehavior.STRING_BUILDER;
        return newExpression;
    }

    private static Expression stringBuilderToString(Expression stringBuilderExpression)
    {
        // TODO: cache this method
        Method toStringMethod = RuntimeType.STRING_BUILDER.resolveMethod("toString", new Type[] {});
        InstanceMethodInvocation instanceMethodInvocation = InstanceMethodInvocation.fromMethod(stringBuilderExpression, toStringMethod);
        Expression newExpression = new Expression(instanceMethodInvocation);
        newExpression.returnBehavior = ReturnBehavior.STRING;
        return newExpression;
    }
}
