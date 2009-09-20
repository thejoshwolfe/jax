package net.wolfesoftware.java.jax.parser;

import java.util.*;
import net.wolfesoftware.java.common.Util;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.tokenizer.*;

public final class Parser
{
    public static Parsing parse(Tokenization tokenization)
    {
        return new Parser(tokenization).parseRoot();
    }

    private final Token[] tokens;
    private final ArrayList<ParsingException> errors = new ArrayList<ParsingException>();

    private Parser(Tokenization tokenization)
    {
        tokens = tokenization.tokens.toArray(new Token[tokenization.tokens.size()]);
    }

    private Parsing parseRoot()
    {
        SubParsing<CompilationUnit> compilationUnit = parseCompilationUnit(0);
        if (compilationUnit == null)
        {
            errors.add(ParsingException.newInstance(0, "Parse Error"));
            return new Parsing(null, errors);
        }
        if (compilationUnit.end != tokens.length)
            errors.add(ParsingException.newInstance(compilationUnit.end, "Expected EOF"));
        return new Parsing(new Root(compilationUnit.element), errors);
    }

    private SubParsing<CompilationUnit> parseCompilationUnit(int offset)
    {
        SubParsing<Imports> imports = parseImports(offset);
        if (imports == null)
            return null;
        offset = imports.end;

        SubParsing<ClassDeclaration> classDeclaration = parseClassDeclaration(offset);
        if (classDeclaration == null)
            return null;
        offset = classDeclaration.end;

        return new SubParsing<CompilationUnit>(new CompilationUnit(imports.element, classDeclaration.element), offset);
    }

    private SubParsing<Imports> parseImports(int offset)
    {
        ArrayList<ImportStatement> elements = new ArrayList<ImportStatement>();
        while (true)
        {
            SubParsing<ImportStatement> importStatement = parseImportStatement(offset);
            if (importStatement != null) {
                elements.add(importStatement.element);
                offset = importStatement.end;
            } else
                break;
            offset++;
        }
        return new SubParsing<Imports>(new Imports(elements), offset);
    }

    private SubParsing<ImportStatement> parseImportStatement(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_IMPORT)
            return null;
        offset++;

        SubParsing<FullClassName> fullClassName = parseFullClassName(offset);
        if (fullClassName == null)
            return null;
        offset = fullClassName.end;
        
        if (getToken(offset).text != Lang.SYMBOL_SEMICOLON)
            return null;
        offset++;
        
        return new SubParsing<ImportStatement>(new ImportStatement(fullClassName.element), offset);
    }

    private SubParsing<FullClassName> parseFullClassName(int offset)
    {
        ArrayList<Id> elements = new ArrayList<Id>();
        while (true)
        {
            Id classMember = parseId(offset);
            if (classMember != null) {
                elements.add(classMember);
                offset++;
            } else
                elements.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text != Lang.SYMBOL_PERIOD)
                break;
            offset++;
        }
        return new SubParsing<FullClassName>(new FullClassName(elements), offset);
    }

    private SubParsing<ClassDeclaration> parseClassDeclaration(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_CLASS)
            return null;
        offset++;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        if (getToken(offset).text != Lang.SYMBOL_OPEN_BRACE)
            return null;
        offset++;

        SubParsing<ClassBody> classBody = parseClassBody(offset);
        if (classBody == null)
            return null;
        offset = classBody.end;

        if (getToken(offset).text != Lang.SYMBOL_CLOSE_BRACE)
            return null;
        offset++;

        return new SubParsing<ClassDeclaration>(new ClassDeclaration(id, classBody.element), offset);
    }

    private SubParsing<ClassBody> parseClassBody(int offset)
    {
        ArrayList<ClassMember> elements = new ArrayList<ClassMember>();
        while (true)
        {
            SubParsing<ClassMember> classMember = parseClassMember(offset);
            if (classMember != null) {
                elements.add(classMember.element);
                offset = classMember.end;
            } else
                elements.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text != Lang.SYMBOL_SEMICOLON)
                break;
            offset++;
        }
        return new SubParsing<ClassBody>(new ClassBody(elements), offset);
    }

    private SubParsing<ClassMember> parseClassMember(int offset)
    {
        SubParsing<?> content;
        
        content = parseFunctionDefinition(offset);
        if (content == null)
            return null;
        offset = content.end;
        
        return new SubParsing<ClassMember>(new ClassMember(content.element), offset);
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
        ArrayList<VariableDeclaration> variableDeclarations = new ArrayList<VariableDeclaration>();
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
        if (token.getType() == IdentifierToken.TYPE)
            return new TypeId(new Id(token.text));
        if (token.text == Lang.KEYWORD_INT)
            return TypeId.KEYWORD_INT;
        if (token.text == Lang.KEYWORD_VOID)
            return TypeId.KEYWORD_VOID;
        if (token.text == Lang.KEYWORD_BOOLEAN)
            return TypeId.KEYWORD_BOOLEAN;
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
        ArrayList<Expression> declarations = new ArrayList<Expression>();
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
    private SubParsing<VariableCreation> parseVariableCreation(int offset)
    {
        SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
        if (variableDeclaration == null)
            return null;
        offset = variableDeclaration.end;

        if (getToken(offset).text != Lang.SYMBOL_EQUALS)
            return null;
        offset++;

        SubParsing<Expression> expression = parseExpression(offset);
        if (expression == null)
            return null;
        offset = expression.end;

        return new SubParsing<VariableCreation>(new VariableCreation(variableDeclaration.element, expression.element), offset);
    }
    private SubParsing<Assignment> parseAssignment(int offset)
    {
        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;
        
        if (getToken(offset).text != Lang.SYMBOL_EQUALS)
            return null;
        offset++;
        
        SubParsing<Expression> expression = parseExpression(offset);
        if (expression == null)
            return null;
        offset = expression.end;

        return new SubParsing<Assignment>(new Assignment(id, expression.element), offset);
    }
    private SubParsing<FunctionInvocation> parseFunctionInvocation(int offset)
    {
        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        if (getToken(offset).text != Lang.SYMBOL_OPEN_PARENS)
            return null;
        offset++;

        SubParsing<Arguments> arguments = parseArguments(offset);
        if (arguments == null)
            return null;
        offset = arguments.end;

        if (getToken(offset).text != Lang.SYMBOL_CLOSE_PARENS)
            return null;
        offset++;

        return new SubParsing<FunctionInvocation>(new FunctionInvocation(id, arguments.element), offset);
    }
    private SubParsing<TryPart> parseTryPart(int offset)
    {
        SubParsing<Expression> expression = parseExpression(offset);
        if (expression == null)
            return null;
        offset = expression.end;

        return new SubParsing<TryPart>(new TryPart(expression.element), offset);
    }
    private SubParsing<CatchPart> parseCatchPart(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_CATCH)
            return null;
        offset++;

        SubParsing<CatchList> catchList = parseCatchList(offset);
        if (catchList == null)
            return null;
        offset = catchList.end;

        return new SubParsing<CatchPart>(new CatchPart(catchList.element), offset);
    }
    private SubParsing<Arguments> parseArguments(int offset)
    {
        ArrayList<Expression> elements = new ArrayList<Expression>();
        while (true)
        {
            SubParsing<Expression> expression = parseExpression(offset);
            if (expression != null) {
                elements.add(expression.element);
                offset = expression.end;
            } else
                elements.add(null);
            if (getToken(offset).text != Lang.SYMBOL_COMMA)
                break;
            offset++;
        }
        return new SubParsing<Arguments>(new Arguments(elements), offset);
    }
    private final class ExpressionParser
    {
        private final Stack<StackElement> stack = new Stack<StackElement>();
        public SubParsing<Expression> parseExpression(int offset)
        {
            while (offset < tokens.length)
            {
                HashMap<String, List<ExpressionOperator>> operators;
                if (hasOpenTop())
                {
                    SubParsing<VariableCreation> variableCreation = parseVariableCreation(offset);
                    if (variableCreation != null)
                    {
                        pushUnit(variableCreation.element);
                        offset = variableCreation.end;
                        continue;
                    }
                    SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
                    if (variableDeclaration != null)
                    {
                        pushUnit(variableDeclaration.element);
                        offset = variableDeclaration.end;
                        continue;
                    }
                    LiteralElement literal = parseLiteral(offset);
                    if (literal != null)
                    {
                        pushUnit(literal);
                        offset++;
                        continue;
                    }
                    SubParsing<FunctionInvocation> functionInvocation = parseFunctionInvocation(offset);
                    if (functionInvocation != null)
                    {
                        pushUnit(functionInvocation.element);
                        offset = functionInvocation.end;
                        continue;
                    }
                    SubParsing<Assignment> assigment = parseAssignment(offset);
                    if (assigment != null)
                    {
                        pushUnit(assigment.element);
                        offset = assigment.end;
                        continue;
                    }
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

                List<ExpressionOperator> ops = operators.get(getToken(offset).text);
                if (ops == null)
                    break;
                int newOffset = consumeOperators(offset + 1, ops);
                if (newOffset == -1)
                    return null;
                offset = newOffset;
                continue;
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
                        case FunctionInvocation.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != FunctionInvocation.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseFunctionInvocation(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case Id.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != Id.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                // TODO streamline this a little
                                innerParsing = new SubParsing<Id>(parseId(offset), offset + 1);
                                if (innerParsing.element == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case TryPart.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != TryPart.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseTryPart(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case CatchPart.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != CatchPart.TYPE)
                            {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseCatchPart(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case -1:
                            break;
                        default:
                            throw new RuntimeException(Integer.toHexString((Integer)expectcedElement));
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
        private void pushOpenLeftOperator(ExpressionOperator op, ArrayList<ParseElement> innerElements)
        {
            groupStack(op.leftPrecedence);
            if (op.rightPrecedence == -1)
            {
                // immediately group postfix operators
                Expression leftExpression = ((ExpressionStackElement)stack.pop()).expression;
                stack.push(new ExpressionStackElement(new Expression(op.makeExpressionContent(leftExpression, innerElements, null)), getTopPrecedence()));
            }
            else
                stack.push(new InfixOperatorStackElement(op, innerElements));
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
