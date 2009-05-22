package net.wolfesoftware.java.jax.parser;

import java.util.*;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.Lang;
import net.wolfesoftware.java.jax.parser.elements.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public final class Parser
{
    public static Parsing parse(Tokenization tokenization)
    {
        return new Parser(tokenization).parse();
    }

    private final Token[] tokens;
    private final ArrayList<ParsingException> errors = new ArrayList<ParsingException>();

    private Parser(Tokenization tokenization)
    {
        tokens = tokenization.tokens.toArray(new Token[tokenization.tokens.size()]);
    }

    private Parsing parse()
    {
        SubParsing<Program> program = parseProgram(0);
        if (program == null)
        {
            errors.add(ParsingException.newInstance(0, "Can't find Program"));
            return new Parsing(null, errors);
        }
        if (program.end != tokens.length)
            errors.add(ParsingException.newInstance(program.end, "Expected EOF"));
        return new Parsing(program.element, errors);
    }

    private SubParsing<Program> parseProgram(int offset)
    {
        int i = offset;
        LinkedList<Declaration> declarations = new LinkedList<Declaration>();
        while (true)
        {
            SubParsing<Declaration> declaration = parseDeclaration(i);
            if (declaration != null) {
                declarations.add(declaration.element);
                i = declaration.end;
            } else
                declarations.add(null);
            Token semicolon = getToken(i);
            if (semicolon.text != Lang.SYMBOL_SEMICOLON)
                break;
            i++;
        }
        return new SubParsing<Program>(new Program(declarations), i);
    }

    private SubParsing<Declaration> parseDeclaration(int offset)
    {
        SubParsing<TypeId> type = parseType(offset);
        if (type == null)
            return null;

        Id id = parseId(getToken(type.end));
        if (id == null)
            return null;

        Token equals = getToken(type.end + 1);
        if (equals.text != Lang.SYMBOL_EQUALS)
            return null;

        SubParsing<Expression> expression = new ExpressionParser().parseExpression(type.end + 2);
        if (expression == null)
            return null;

        return new SubParsing<Declaration>(new Declaration(type.element, id, expression.element), expression.end);
    }

    private SubParsing<TypeId> parseType(int offset)
    {
        Token token = getToken(offset);
        if (token.text == Lang.KEYWORD_INT)
            return new SubParsing<TypeId>(TypeId.KEYWORD_INT, offset + 1);
        if (token.text == Lang.KEYWORD_VOID)
            return new SubParsing<TypeId>(TypeId.KEYWORD_VOID, offset + 1);
        return null;
    }

    private Id parseId(Token token)
    {
        if (token.getType() != IdentifierToken.TYPE)
            return null;
        return new Id(token.text);
    }
    
    private SubParsing<Expression> parseExpression(int offset)
    {
        return new ExpressionParser().parseExpression(offset);
    }

    private SubParsing<BlockContents> parseBlockContents(int offset)
    {
        int i = offset;
        LinkedList<Expression> declarations = new LinkedList<Expression>();
        while (true)
        {
            SubParsing<Expression> expression = parseExpression(i);
            if (expression != null) {
                declarations.add(expression.element);
                i = expression.end;
            } else
                declarations.add(null);
            Token semicolon = getToken(i);
            if (semicolon.text != Lang.SYMBOL_SEMICOLON)
                break;
            i++;
        }
        return new SubParsing<BlockContents>(new BlockContents(declarations), i);
    }
    private final class ExpressionParser
    {
        private final Stack<StackElement> stack = new Stack<StackElement>();
        public SubParsing<Expression> parseExpression(int offset)
        {
            int i = offset;
            while (i < tokens.length)
            {
                Token token = getToken(i);
                HashMap<String, List<ExpressionOperator>> operators = null;
                if (hasOpenTop())
                {
                    LiteralElement literal = parseLiteral(i);
                    if (literal != null)
                    {
                        pushUnit(literal);
                        i++;
                        continue;
                    }
                    // function invocation goes here
                    Id id = parseId(token);
                    if (id != null)
                    {
                        pushUnit(id);
                        i++;
                        continue;
                    }
                    operators = ExpressionOperator.CLOSED_LEFT;
                }
                else
                    operators = ExpressionOperator.OPEN_LEFT;

                if (operators != null)
                {
                    List<ExpressionOperator> ops = operators.get(token.text);
                    if (ops != null)
                    {
                        int newI = consumeOperators(i + 1, ops);
                        if (newI == -1)
                            return null;
                        i = newI;
                        continue;
                    }
                    break;
                }
            }
            if (hasOpenTop())
                return null;
            groupStack(0);
            return new SubParsing<Expression>(((ExpressionStackElement)stack.pop()).expression, i);
        }
        private int consumeOperators(int offset, List<ExpressionOperator> ops)
        {
            for (ExpressionOperator op : ops)
            {
                ArrayList<ParseElement> innerExpressions = null;
                if (op instanceof ExpressionEnclosingOperator)
                {
                    EnclosedSubParsing innerExpressionParsing = parseEnclosed(offset, ((ExpressionEnclosingOperator)op).elements);
                    if (innerExpressionParsing == null)
                        continue;
                    innerExpressions = innerExpressionParsing.expressions;
                    offset = innerExpressionParsing.end;
                }
                if (op.leftPrecedence == -1)
                    pushClosedLeftOperator(op, innerExpressions);
                else
                    pushOpenLeftOperator(op, innerExpressions);
                return offset;
            }
            return -1;
        }
        private final ArrayList<SubParsing<?>> partialSubParsings = new ArrayList<SubParsing<?>>();
        private EnclosedSubParsing parseEnclosed(int offset, Object[] elements)
        {
            ArrayList<ParseElement> innerElements = new ArrayList<ParseElement>();
            for (int i = 0; i < elements.length; i++)
            {
                Object expectcedElement = elements[i];
                if (expectcedElement.getClass() == String.class)
                {
                    if (!(i < partialSubParsings.size() && partialSubParsings.get(i) == null))
                    {
                        // history was missing/wrong
                        Util.removeAfter(partialSubParsings, i);
                        Token token = getToken(offset);
                        if (token.text != expectcedElement)
                            return null;
                        partialSubParsings.add(null);
                    }
                    offset++;
                }
                else
                {
                    SubParsing<?> innerParsing = null;
                    if (i < partialSubParsings.size())
                        innerParsing = partialSubParsings.get(i);
                    switch ((Integer)expectcedElement)
                    {
                        case Expression.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != Expression.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = new ExpressionParser().parseExpression(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case BlockContents.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != BlockContents.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseBlockContents(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        default:
                            throw new RuntimeException();
                    }
                }
            }
            partialSubParsings.clear();
            return new EnclosedSubParsing(innerElements, offset);
        }

        private boolean hasOpenTop()
        {
            return stack.isEmpty() || stack.peek().hasOpenRight();
        }
        private int getTopPrecedence()
        {
            return stack.isEmpty() ? 0 : stack.peek().getRightPrecedence();
        }
        private void pushUnit(ParseElement element)
        {
            stack.push(new ExpressionStackElement(new Expression(element), getTopPrecedence()));
        }
        private void pushOpenLeftOperator(ExpressionOperator op, ArrayList<ParseElement> innerExpressions)
        {
            groupStack(op.leftPrecedence);
            if (op.rightPrecedence == -1)
                throw new RuntimeException(); // immediate group postfix operator
            else
                stack.push(new InfixOperatorStackElement(op, innerExpressions));
        }
        private void pushClosedLeftOperator(ExpressionOperator op, ArrayList<ParseElement> innerElements)
        {
            if (op.rightPrecedence == -1)
                pushUnit(op.makeExpressionContent(null, innerElements, null));
            else
                stack.push(new PrefixOperatorStackElement(op, innerElements, getTopPrecedence()));
        }
        private void groupStack(int precedence)
        {
            while (precedence < getTopPrecedence())
            {
                Expression rightExpression = ((ExpressionStackElement)stack.pop()).expression;
                OperatorStackElement operatorElement = (OperatorStackElement)stack.pop();
                Expression leftExpression = operatorElement.op.leftPrecedence == -1 ? null : ((ExpressionStackElement)stack.pop()).expression;
                Expression expression = operatorElement.op.makeExpressionContent(leftExpression, operatorElement.innerExpressions, rightExpression);
                stack.push(new ExpressionStackElement(expression, getTopPrecedence()));
            }
        }

        private abstract class StackElement
        {
            public abstract int getRightPrecedence();
            public abstract boolean hasOpenRight();
        }
        private class ExpressionStackElement extends StackElement
        {
            public final Expression expression;
            public final int rightPrecedence;
            public ExpressionStackElement(Expression expression, int rightPrecedence)
            {
                this.expression = expression;
                this.rightPrecedence = rightPrecedence;
            }
            public int getRightPrecedence()
            {
                return rightPrecedence;
            }
            public boolean hasOpenRight()
            {
                return false;
            }
            public String toString()
            {
                return expression.toString() + ":" + getRightPrecedence();
            }
        }

        private abstract class OperatorStackElement extends StackElement
        {
            public final ExpressionOperator op;
            public final ArrayList<ParseElement> innerExpressions;
            protected OperatorStackElement(ExpressionOperator op, ArrayList<ParseElement> innerExpressions)
            {
                this.op = op;
                this.innerExpressions = innerExpressions;
            }
            public boolean hasOpenRight()
            {
                return true; // postfix operators are grouped immediately
            }
            public String toString()
            {
                return op.toString() + ":" + getRightPrecedence();
            }
        }

        private class InfixOperatorStackElement extends OperatorStackElement
        {
            public InfixOperatorStackElement(ExpressionOperator op, ArrayList<ParseElement> innerExpressions)
            {
                super(op, innerExpressions);
            }
            public int getRightPrecedence()
            {
                return op.rightPrecedence;
            }
        }
        private class PrefixOperatorStackElement extends OperatorStackElement
        {
            public final int rightPrecedence;
            public PrefixOperatorStackElement(ExpressionOperator op, ArrayList<ParseElement> innerExpressions, int minRightPrecedence)
            {
                super(op, innerExpressions);
                this.rightPrecedence = Math.max(op.rightPrecedence, minRightPrecedence);
            }
            public int getRightPrecedence()
            {
                return rightPrecedence;
            }
        }

        private class EnclosedSubParsing
        {
            public final ArrayList<ParseElement> expressions;
            public final int end;
            public EnclosedSubParsing(ArrayList<ParseElement> expressions, int end)
            {
                this.expressions = expressions;
                this.end = end;
            }
            public String toString()
            {
                return "([" + Util.join(expressions.toArray(), ", ") + "], " + end + ")";
            }
        }
    }

    private LiteralElement parseLiteral(int offset)
    {
        Token token = getToken(offset);
        if (!(token instanceof LiteralToken))
            return null;
        return ((LiteralToken)token).makeElement();
    }

    private static final Token NULL_TOKEN = new Token(-1, null) {
        public int getType()
        {
            return 0;
        };
        public String toString()
        {
            return "NULL";
        }
    };

    private Token getToken(int offset)
    {
        if (!(0 <= offset && offset < tokens.length))
            return NULL_TOKEN;
        return tokens[offset];
    }

    private static class SubParsing<T extends ParseElement>
    {
        public final T element;
        public final int end;

        public SubParsing(T element, int end)
        {
            this.element = element;
            this.end = end;
        }
        public String toString()
        {
            return "(\"" + element + "\", " + end + ")";
        }
    }
}
