package net.wolfesoftware.jax.parsing;

import java.lang.reflect.*;
import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.tokenization.Lang;

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
    PRECEDENCE_LOGICAL_OR = 30,
    PRECEDENCE_TERNARY = 20,
    PRECEDENCE_ASSIGNMENT = 10,
    PRECEDENCE_LOWEST = 1;

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
            return new AmbiguousPreIncrementDecrement(Lang.SYMBOL_PLUS_PLUS, rightExpression);
        }
    };
    public static final ExpressionOperator preDecrement = new ExpressionOperator(-1, Lang.SYMBOL_MINUS_MINUS, PRECEDENCE_UNARY + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousPreIncrementDecrement(Lang.SYMBOL_MINUS_MINUS, rightExpression);
        }
    };
    public static final ExpressionOperator postIncrement = new ExpressionOperator(PRECEDENCE_POSTFIX, Lang.SYMBOL_PLUS_PLUS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousPostIncrementDecrement(rightExpression, Lang.SYMBOL_PLUS_PLUS);
        }
    };
    public static final ExpressionOperator postDecrement = new ExpressionOperator(PRECEDENCE_POSTFIX, Lang.SYMBOL_MINUS_MINUS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousPostIncrementDecrement(rightExpression, Lang.SYMBOL_MINUS_MINUS);
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
    public static final ExpressionOperator lessThanOrEqual = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_LESS_THAN_EQUALS, PRECEDENCE_RELATIONAL + 1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new LessThanOrEqual(leftExpression, rightExpression);
        }
    };
    public static final ExpressionOperator greaterThanOrEqual = new ExpressionOperator(PRECEDENCE_RELATIONAL, Lang.SYMBOL_GREATER_THAN_EQUALS, PRECEDENCE_RELATIONAL + 1) {
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

    public static final ExpressionOperator returnExpression = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_RETURN, -1,
            Expression.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ReturnExpression((Expression)innerElements.get(0));
        }
    };
    public static final ExpressionOperator returnVoid = new ExpressionOperator(-1, Lang.KEYWORD_RETURN, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return ReturnVoid.INSTANCE;
        }
    };
    public static final ExpressionOperator _throw = new ExpressionOperator(-1, Lang.KEYWORD_THROW, PRECEDENCE_LOWEST) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new Throw(rightExpression);
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
    public static final ExpressionOperator questionColon = new ExpressionEnclosingOperator(PRECEDENCE_TERNARY, Lang.SYMBOL_QUESTION, PRECEDENCE_TERNARY, 
            Expression.TYPE, Lang.SYMBOL_COLON) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new QuestionColon(leftExpression, (Expression)innerElements.get(0), rightExpression);
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
            AmbiguousId.TYPE, Lang.SYMBOL_OPEN_PARENS, Arguments.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ConstructorInvocation((AmbiguousId)innerElements.get(0), (Arguments)innerElements.get(1));
        }
    };
    public static final ExpressionOperator constructorRedirectThis = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_THIS, -1,
            Lang.SYMBOL_OPEN_PARENS, Arguments.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ConstructorRedirectThis((Arguments)innerElements.get(0));
        }
    };
    public static final ExpressionOperator constructorRedirectSuper = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_SUPER, -1,
            Lang.SYMBOL_OPEN_PARENS, Arguments.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ConstructorRedirectSuper((Arguments)innerElements.get(0));
        }
    };

    public static final ExpressionOperator dereferenceField = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_PERIOD, -1,
            AmbiguousId.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousFieldExpression(leftExpression, (AmbiguousId)innerElements.get(0));
        }
    };
    private static class ExpressionAssignmentOperator extends ExpressionOperator
    {
        public ExpressionAssignmentOperator(String text)
        {
            super(PRECEDENCE_ASSIGNMENT, text, PRECEDENCE_ASSIGNMENT);
        }
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousAssignment(leftExpression, text, rightExpression);
        }
    }
    // = += -= *= /= %= &= ^= |= <<= >>= >>>=
    public static final ExpressionOperator 
    assignmentEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_EQUALS),
    assignmentPlusEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_PLUS_EQUALS),
    assignmentMinusEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_MINUS_EQUALS),
    assignmentTimesEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_ASTERISK_EQUALS),
    assignmentDivideEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_SLASH_EQUALS),
    assignmentModEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_PERCENT_EQUALS),
    assignmentAndEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_AMPERSAND_EQUALS),
    assignmentXorEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_CARET_EQUALS),
    assignmentOrEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_PIPE_EQUALS),
    assignmentShiftLeftEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_LESS_THAN_LESS_THAN_EQUALS),
    assignmentShiftRightEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_GREATER_THAN_GREATER_THAN_EQUALS),
    assignmentShiftRightUnsignedEquals = new ExpressionAssignmentOperator(Lang.SYMBOL_GREATER_THAN_GREATER_THAN_GREATER_THAN_EQUALS);

    public static final ExpressionOperator instanceOf = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.KEYWORD_INSTANCEOF, -1,
            TypeId.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new InstanceOf(leftExpression, (TypeId)innerElements.get(0));
        }
    };

    public static final ExpressionOperator methodInvocation = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_PERIOD, -1,
            AmbiguousId.TYPE, Lang.SYMBOL_OPEN_PARENS, Arguments.TYPE, Lang.SYMBOL_CLOSE_PARENS) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new AmbiguousMethodInvocation(leftExpression, (AmbiguousId)innerElements.get(0), (Arguments)innerElements.get(1));
        }
    };
    public static final ExpressionOperator arrayDereference = new ExpressionEnclosingOperator(PRECEDENCE_DEREFERENCE, Lang.SYMBOL_OPEN_BRACKET, -1,
            Expression.TYPE, Lang.SYMBOL_CLOSE_BRACKET) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new ArrayDereference(leftExpression, (Expression)innerElements.get(0));
        }
    };

    public static final ExpressionOperator tryCatch = new ExpressionEnclosingOperator(-1, Lang.KEYWORD_TRY, -1,
            TryPart.TYPE, CatchPart.TYPE) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return new TryCatch((TryPart)innerElements.get(0), (CatchPart)innerElements.get(1));
        }
    };

    public static final ExpressionOperator _null = new ExpressionOperator(-1, Lang.KEYWORD_NULL, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return NullExpression.INSTANCE;
        }
    };
    public static final ExpressionOperator _this = new ExpressionOperator(-1, Lang.KEYWORD_THIS, -1) {
        public ParseElement makeExpressionContent(Expression leftExpression, ArrayList<ParseElement> innerElements, Expression rightExpression)
        {
            return ThisExpression.INSTANCE;
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
