package net.wolfesoftware.java.jax.parser;

import java.lang.reflect.*;
import java.util.*;
import net.wolfesoftware.java.jax.ast.*;

public abstract class ExpressionOperator
{
    public final int leftPrecedence;
    public final String text;
    public final int rightPrecedence;
    public ExpressionOperator(int leftPrecedence, String text, int rightPrecedence)
    {
        this.leftPrecedence = leftPrecedence;
        this.text = text;
        this.rightPrecedence = rightPrecedence;
    }
    public abstract ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression);
    public String toString()
    {
        return text;
    }

    /* Operation */
    public static final ExpressionOperator ADDITION = new ExpressionOperator(120, Lang.SYMBOL_PLUS, 121) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Addition(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator SUBTRACTION = new ExpressionOperator(120, Lang.SYMBOL_MINUS, 121) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Subtraction(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator MULTIPLICATION = new ExpressionOperator(130, Lang.SYMBOL_ASTERISK, 131) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Multiplication(leftExpression, rightExpression);
        }
    };

    public static final ExpressionOperator QUANTITY = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_PARENS, -1, 
            Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Quantity((Expression)innerElements.get(0));
        }
    };

    /* ControlStructure */
    public static final ExpressionOperator IfThen = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_IF, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new IfThen((Expression)innerElements.get(0), (Expression)innerElements.get(1));
        }
    };
    public static final ExpressionOperator IfThenElse = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_IF, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, Expression.TYPE, Lang.KEYWORD_ELSE, Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new IfThenElse((Expression)innerElements.get(0), (Expression)innerElements.get(1), (Expression)innerElements.get(2));
        }
    };

    public static final ExpressionOperator Block = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_BRACE, -1,
            BlockContents.TYPE, Lang.SYMBOL_CLOSE_BRACE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Block((BlockContents)innerElements.get(0));
        }
    };
    
    public static final HashMap<String, List<ExpressionOperator>> OPEN_LEFT = new HashMap<String, List<ExpressionOperator>>();
    public static final HashMap<String, List<ExpressionOperator>> CLOSED_LEFT = new HashMap<String, List<ExpressionOperator>>();

    private static final Comparator<ExpressionOperator> ambiguityOrderingComparitor = new Comparator<ExpressionOperator>() {
        public int compare(ExpressionOperator o1, ExpressionOperator o2)
        {
            int len1 = (o1 instanceof ExpressionEnclosingOperator) ? ((ExpressionEnclosingOperator)o1).elements.length : 0;
            int len2 = (o2 instanceof ExpressionEnclosingOperator) ? ((ExpressionEnclosingOperator)o2).elements.length : 0;
            return len2 - len1;
        }
    };

    static {
        Field[] fields = ExpressionOperator.class.getFields();
        for (Field field : fields)
        {
            if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL) && field.getType() == ExpressionOperator.class)
            {
                ExpressionOperator op;
                try {
                    op = (ExpressionOperator)field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                HashMap<String, List<ExpressionOperator>> operators = op.leftPrecedence == -1 ? CLOSED_LEFT : OPEN_LEFT;
                putOperator(operators, op);
            }
        }
    }
    private static void putOperator(HashMap<String, List<ExpressionOperator>> operators, ExpressionOperator op)
    {
        List<ExpressionOperator> list = operators.get(op.text);
        if (list == null)
        {
            list = new LinkedList<ExpressionOperator>();
            list.clear();
            operators.put(op.text, list);
        }
        list.add(op);
        if (1 < list.size())
            Collections.sort(list, ambiguityOrderingComparitor);
    }
}
