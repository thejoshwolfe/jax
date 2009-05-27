package net.wolfesoftware.java.jax.parser;

import java.util.*;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.ast.*;
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
        LinkedList<TopLevelItem> topLevelItems = new LinkedList<TopLevelItem>();
        while (true)
        {
            SubParsing<TopLevelItem> topLevelItem = parseTopLevelItem(offset);
            if (topLevelItem != null) {
                topLevelItems.add(topLevelItem.element);
                offset = topLevelItem.end;
            } else
                topLevelItems.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text != Lang.SYMBOL_SEMICOLON)
                break;
            offset++;
        }
        return new SubParsing<Program>(new Program(topLevelItems), offset);
    }

    private SubParsing<TopLevelItem> parseTopLevelItem(int offset)
    {
        SubParsing<?> content;
        
        content = parseFunctionDefinition(offset);
        if (content == null)
            return null;
        offset = content.end;
        
        return new SubParsing<TopLevelItem>(new TopLevelItem(content.element), offset);
    }

    private SubParsing<FunctionDefinition> parseFunctionDefinition(int offset)
    {
        TypeId typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset++;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        if (getToken(offset).text != Lang.SYMBOL_OPEN_PARENS)
            return null;
        offset++;

        SubParsing<ArgumentDeclarations> argumentDeclarations = parseArgumentDeclarations(offset);
        if (argumentDeclarations == null)
            return null;
        offset = argumentDeclarations.end;

        if (getToken(offset).text != Lang.SYMBOL_CLOSE_PARENS)
            return null;
        offset++;

        SubParsing<Expression> expression = parseExpression(offset);
        if (expression == null)
            return null;
        offset = expression.end;

        return new SubParsing<FunctionDefinition>(new FunctionDefinition(typeId, id, argumentDeclarations.element, expression.element), offset);
    }

    private SubParsing<ArgumentDeclarations> parseArgumentDeclarations(int offset)
    {
        LinkedList<VariableDeclaration> variableDeclarations = new LinkedList<VariableDeclaration>();
        while (true)
        {
            SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
            if (variableDeclaration == null)
            {
                if (variableDeclarations.size() == 0)
                    break;
                else
                    return null;
            }
            variableDeclarations.add(variableDeclaration.element);
            offset = variableDeclaration.end;

            if (getToken(offset).text != Lang.SYMBOL_COMMA)
                break;
            offset++;
        }
        return new SubParsing<ArgumentDeclarations>(new ArgumentDeclarations(variableDeclarations), offset);
    }

    private SubParsing<VariableDeclaration> parseVariableDeclaration(int offset)
    {
        TypeId typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset++;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        return new SubParsing<VariableDeclaration>(new VariableDeclaration(typeId, id), offset);
    }

    private TypeId parseTypeId(int offset)
    {
        Token token = getToken(offset);
        if (token.text == Lang.KEYWORD_INT)
            return TypeId.KEYWORD_INT;
        if (token.text == Lang.KEYWORD_VOID)
            return TypeId.KEYWORD_VOID;
        return null;
    }

    private Id parseId(int offset)
    {
        Token token = getToken(offset);
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
        LinkedList<Expression> declarations = new LinkedList<Expression>();
        while (true)
        {
            SubParsing<Expression> expression = parseExpression(offset);
            if (expression != null) {
                declarations.add(expression.element);
                offset = expression.end;
            } else
                declarations.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text != Lang.SYMBOL_SEMICOLON)
                break;
            offset++;
        }
        return new SubParsing<BlockContents>(new BlockContents(declarations), offset);
    }
    private final class ExpressionParser
    {
        private final Stack<StackElement> stack = new Stack<StackElement>();
        public SubParsing<Expression> parseExpression(int offset)
        {
            while (offset < tokens.length)
            {
                HashMap<String, List<ExpressionOperator>> operators = null;
                if (hasOpenTop())
                {
                    LiteralElement literal = parseLiteral(offset);
                    if (literal != null)
                    {
                        pushUnit(literal);
                        offset++;
                        continue;
                    }
                    // function invocation goes here
                    Id id = parseId(offset);
                    if (id != null)
                    {
                        pushUnit(id);
                        offset++;
                        continue;
                    }
                    operators = ExpressionOperator.CLOSED_LEFT;
                }
                else
                    operators = ExpressionOperator.OPEN_LEFT;

                if (operators != null)
                {
                    List<ExpressionOperator> ops = operators.get(getToken(offset).text);
                    if (ops != null)
                    {
                        int newOffset = consumeOperators(offset + 1, ops);
                        if (newOffset == -1)
                            return null;
                        offset = newOffset;
                        continue;
                    }
                    break;
                }
            }
            if (hasOpenTop())
                return null;
            groupStack(0);
            return new SubParsing<Expression>(((ExpressionStackElement)stack.pop()).expression, offset);
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
                ParseElement expressionContent = operatorElement.op.makeExpressionContent(leftExpression, operatorElement.innerExpressions, rightExpression);
                stack.push(new ExpressionStackElement(new Expression(expressionContent), getTopPrecedence()));
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
