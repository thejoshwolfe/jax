package net.wolfesoftware.jax.parser;

import java.lang.reflect.*;
import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.tokenizer.Lang;

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

    private static final int 
    PRECEDENCE_DEREFERENCE = 160,
    PRECEDENCE_POSTFIX = 140,
    PRECEDENCE_UNARY = 130,
    PRECEDENCE_MULTIPLICATIVE = 120,
    PRECEDENCE_ADDITIVE = 110,
    PRECEDENCE_RELATIONAL = 90,
    PRECEDENCE_EQUALITY = 80,
    PRECEDENCE_LOGICAL_AND = 40,
    PRECEDENCE_LOGICAL_OR = 30;

    /* Operation */
    public static final ExpressionOperator addition = new ExpressionOperator(PRECEDENCE_ADDITIVE, Lang.SYMBOL_PLUS, PRECEDENCE_ADDITIVE + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Addition(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator subtraction = new ExpressionOperator(PRECEDENCE_ADDITIVE, Lang.SYMBOL_MINUS, PRECEDENCE_ADDITIVE + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Subtraction(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator multiplication = new ExpressionOperator(PRECEDENCE_MULTIPLICATIVE, Lang.SYMBOL_ASTERISK, PRECEDENCE_MULTIPLICATIVE + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Multiplication(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator division = new ExpressionOperator(PRECEDENCE_MULTIPLICATIVE, Lang.SYMBOL_SLASH, PRECEDENCE_MULTIPLICATIVE + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Division(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator preIncrement = new ExpressionOperator(-1, Lang.SYMBOL_PLUS_PLUS, PRECEDENCE_UNARY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new PreIncrement(rightExpression);
        }
    };
    public static final ExpressionOperator preDecrement = new ExpressionOperator(-1, Lang.SYMBOL_MINUS_MINUS, PRECEDENCE_UNARY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new PreDecrement(rightExpression);
        }
    };
    public static final ExpressionOperator postIncrement = new ExpressionOperator(PRECEDENCE_POSTFIX, Lang.SYMBOL_PLUS_PLUS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new PostIncrement(leftExpression);
        }
    };
    public static final ExpressionOperator postDecrement = new ExpressionOperator(PRECEDENCE_POSTFIX, Lang.SYMBOL_MINUS_MINUS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new PostDecrement(leftExpression);
        }
    };

    public static final ExpressionOperator lessThan = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_LESS_THAN, PRECEDENCE_RELATIONAL + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new LessThan(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator greaterThan = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_GREATER_THAN, PRECEDENCE_RELATIONAL + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new GreaterThan(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator lessThanOrEqual = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_LESS_THAN_OR_EQUAL, PRECEDENCE_RELATIONAL + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new LessThanOrEqual(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator greaterThanOrEqual = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_GREATER_THAN_OR_EQUAL, PRECEDENCE_RELATIONAL + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new GreaterThanOrEqual(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator equality = new ExpressionOperator(PRECEDENCE_EQUALITY, Lang.SYMBOL_EQUALS_EQUALS, PRECEDENCE_EQUALITY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Equality(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator inequality = new ExpressionOperator(PRECEDENCE_EQUALITY, Lang.SYMBOL_BANG_EQUALS, PRECEDENCE_EQUALITY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Inequality(leftExpression, rightExpression);
        }
    };

    public static final ExpressionOperator negation = new ExpressionOperator(-1, Lang.SYMBOL_MINUS, PRECEDENCE_UNARY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Negation(rightExpression);
        }
    };
    public static final ExpressionOperator booleanNot = new ExpressionOperator(-1, Lang.SYMBOL_BANG, PRECEDENCE_UNARY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new BooleanNot(rightExpression);
        }
    };

    public static final ExpressionOperator shortCircuitAnd = new ExpressionOperator(PRECEDENCE_LOGICAL_AND, Lang.SYMBOL_AMPERSAND_AMPERSAND, PRECEDENCE_LOGICAL_AND + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ShortCircuitAnd(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator shortCircuitOr = new ExpressionOperator(PRECEDENCE_LOGICAL_OR, Lang.SYMBOL_PIPE_PIPE, PRECEDENCE_LOGICAL_OR + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ShortCircuitOr(leftExpression, rightExpression);
        }
    };


    public static final ExpressionOperator quantity = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_PARENS, -1, 
            Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Quantity((Expression)innerElements.get(0));
        }
    };
    public static final ExpressionOperator primitiveCast = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_PARENS, PRECEDENCE_UNARY + 1, 
            PrimitiveType.TYPE, Lang.SYMBOL_CLOSE_PARENS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new TypeCast((TypeId)innerElements.get(0), rightExpression);
        }
    };
    public static final ExpressionOperator typeIdCast = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_PARENS, PRECEDENCE_UNARY + 1, 
            TypeId.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new TypeCast((TypeId)innerElements.get(0), rightExpression);
        }
    };

    /* ControlStructure */
    public static final ExpressionOperator ifThen = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_IF, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new IfThen((Expression)innerElements.get(0), (Expression)innerElements.get(1));
        }
    };
    public static final ExpressionOperator ifThenElse = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_IF, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, Expression.TYPE, Lang.KEYWORD_ELSE, Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new IfThenElse((Expression)innerElements.get(0), (Expression)innerElements.get(1), (Expression)innerElements.get(2));
        }
    };
    public static final ExpressionOperator forLoop = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_FOR, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_SEMICOLON, Expression.TYPE, Lang.SYMBOL_SEMICOLON, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, 
            Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ForLoop((Expression)innerElements.get(0), (Expression)innerElements.get(1), (Expression)innerElements.get(2), (Expression)innerElements.get(3));
        }
    };
    public static final ExpressionOperator whileLoop = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_WHILE, -1, 
            Lang.SYMBOL_OPEN_PARENS, Expression.TYPE, Lang.SYMBOL_CLOSE_PARENS, Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new WhileLoop((Expression)innerElements.get(0), (Expression)innerElements.get(1));
        }
    };

    public static final ExpressionOperator block = new ExpressionEnclosingOperator(-1, Lang.SYMBOL_OPEN_BRACE, -1,
            BlockContents.TYPE, Lang.SYMBOL_CLOSE_BRACE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Block((BlockContents)innerElements.get(0));
        }
    };
    public static final ExpressionOperator constructorInvocation = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_NEW, -1,
            FunctionInvocation.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ConstructorInvocation((FunctionInvocation)innerElements.get(0));
        }
    };

    public static final ExpressionOperator dereferenceField = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_PERIOD, -1,
            Id.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new DereferenceField(leftExpression, (Id)innerElements.get(0));
        }
    };
    public static final ExpressionOperator dereferenceMethod = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_PERIOD, -1,
            FunctionInvocation.TYPE, -1) { // insert this extra term so that the Parser looks for methods before fields
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new DereferenceMethod(leftExpression, (FunctionInvocation)innerElements.get(0));
        }
    };
    public static final ExpressionOperator arrayDereference = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_OPEN_BRACKET, -1,
            Expression.TYPE, Lang.SYMBOL_CLOSE_BRACKET) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ArrayDereference(leftExpression, (Expression)innerElements.get(0));
        }
    };

    /* TryGroup */
    public static final ExpressionOperator tryCatch = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_TRY, -1,
            TryPart.TYPE, CatchPart.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new TryCatch((TryPart)innerElements.get(0), (CatchPart)innerElements.get(1));
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
        for (Field field : fields) {
            if (field.getModifiers() == (Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL) && field.getType() == ExpressionOperator.class) {
                ExpressionOperator op;
                try {
                    op = (ExpressionOperator)field.get(null);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                if (op == typeIdCast)
                    continue; // don't count this one
                HashMap<String, List<ExpressionOperator>> operators = op.leftPrecedence == -1 ? CLOSED_LEFT : OPEN_LEFT;
                putOperator(operators, op);
            }
        }
    }
    private static void putOperator(HashMap<String, List<ExpressionOperator>> operators, ExpressionOperator op)
    {
        List<ExpressionOperator> list = operators.get(op.text);
        if (list == null) {
            list = new LinkedList<ExpressionOperator>();
            list.clear();
            operators.put(op.text, list);
        }
        list.add(op);
        if (1 < list.size())
            Collections.sort(list, ambiguityOrderingComparitor);
    }
}
