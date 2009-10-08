package net.wolfesoftware.java.jax.parser;

import net.wolfesoftware.java.jax.util.Util;

public abstract class ExpressionEnclosingOperator extends ExpressionOperator
{
    public final Object[] elements;
    public ExpressionEnclosingOperator(int leftPrecedence, String text, int rightPrecedence, Object... elements)
    {
        super(leftPrecedence, text, rightPrecedence);
        this.elements = elements;
    }
    public String toString()
    {
        Object[] textAndElements = new Object[1 + elements.length];
        textAndElements[0] = text;
        System.arraycopy(elements, 0, textAndElements, 1, elements.length);
        return Util.join(textAndElements, " ");
    }
}
