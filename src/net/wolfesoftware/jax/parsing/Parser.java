package net.wolfesoftware.jax.parsing;

import java.util.*;
import net.wolfesoftware.jax.JaxcOptions;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.tokenization.*;
import net.wolfesoftware.jax.util.Util;

public final class Parser
{
    public static Parsing parse(Tokenization tokenization, JaxcOptions options)
    {
        return new Parser(tokenization, options).parseRoot();
    }

    private final Token[] tokens;
    private final LineColumnLookup lineColumnLookup;
    private int maxIndex = 0;
    private final ArrayList<ParsingError> errors = new ArrayList<ParsingError>();
    private final JaxcOptions options;

    private Parser(Tokenization tokenization, JaxcOptions options)
    {
        tokens = tokenization.tokens.toArray(new Token[tokenization.tokens.size()]);
        lineColumnLookup = tokenization.lineColumnLookup;
        this.options = options;
    }

    private Parsing parseRoot()
    {
        SubParsing<CompilationUnit> compilationUnit = parseCompilationUnit(0);
        if (compilationUnit == null) {
            errors.add(new ParsingError(tokens[maxIndex], lineColumnLookup));
            return new Parsing(null, errors);
        }
        if (compilationUnit.end != tokens.length)
            errors.add(new ParsingError(tokens[compilationUnit.end], lineColumnLookup));
        return new Parsing(new Root(compilationUnit.element), errors);
    }

    private SubParsing<CompilationUnit> parseCompilationUnit(int offset)
    {
        SubParsing<PackageStatements> packageStatements = parsePackageStatements(offset);
        if (packageStatements == null)
            return null;
        offset = packageStatements.end;

        SubParsing<Imports> imports = parseImports(offset);
        if (imports == null)
            return null;
        offset = imports.end;

        SubParsing<ClassDeclaration> classDeclaration = parseClassDeclaration(offset);
        if (classDeclaration == null)
            return null;
        offset = classDeclaration.end;

        return new SubParsing<CompilationUnit>(new CompilationUnit(packageStatements.element, imports.element, classDeclaration.element), offset);
    }

    private SubParsing<PackageStatements> parsePackageStatements(int offset)
    {
        ArrayList<PackageStatement> elements = new ArrayList<PackageStatement>();
        while (true) {
            SubParsing<PackageStatement> packageStatement = parsePackageStatement(offset);
            if (packageStatement != null) {
                elements.add(packageStatement.element);
                offset = packageStatement.end;
            } else
                break;
        }
        return new SubParsing<PackageStatements>(new PackageStatements(elements), offset);
    }

    private SubParsing<PackageStatement> parsePackageStatement(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_PACKAGE)
            return null;
        offset++;

        SubParsing<QualifiedName> qualifiedName = parseQualifiedName(offset);
        if (qualifiedName == null)
            return null;
        offset = qualifiedName.end;

        if (getToken(offset).text != Lang.SYMBOL_SEMICOLON)
            return null;
        offset++;

        return new SubParsing<PackageStatement>(new PackageStatement(qualifiedName.element), offset);
    }

    private SubParsing<Imports> parseImports(int offset)
    {
        ArrayList<ImportStatement> elements = new ArrayList<ImportStatement>();
        while (true) {
            SubParsing<ImportStatement> importStatement = parseImportStatement(offset);
            if (importStatement != null) {
                elements.add(importStatement.element);
                offset = importStatement.end;
            } else
                break;
        }
        return new SubParsing<Imports>(new Imports(elements), offset);
    }

    private SubParsing<ImportStatement> parseImportStatement(int offset)
    {
        SubParsing<? extends ParseElement> content = null;

        if (content == null)
            content = parseImportStar(offset);
        if (content == null)
            content = parseImportClass(offset);
        if (content == null)
            return null;

        return new SubParsing<ImportStatement>(new ImportStatement(content.element), content.end);
    }

    private SubParsing<ImportStar> parseImportStar(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_IMPORT)
            return null;
        offset++;

        SubParsing<QualifiedName> qualifiedName = parseQualifiedName(offset);
        if (qualifiedName == null)
            return null;
        offset = qualifiedName.end;

        List<Id> qualifiedNameList = qualifiedName.element.elements;
        if (qualifiedNameList.isEmpty() || qualifiedNameList.get(qualifiedNameList.size() - 1) != null)
            return null;
        if (getToken(offset).text != Lang.SYMBOL_ASTERISK)
            return null;
        offset++;
        if (getToken(offset).text != Lang.SYMBOL_SEMICOLON)
            return null;
        offset++;

        return new SubParsing<ImportStar>(new ImportStar(qualifiedName.element), offset);
    }

    private SubParsing<ImportClass> parseImportClass(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_IMPORT)
            return null;
        offset++;

        SubParsing<QualifiedName> qualifiedName = parseQualifiedName(offset);
        if (qualifiedName == null)
            return null;
        offset = qualifiedName.end;

        if (getToken(offset).text != Lang.SYMBOL_SEMICOLON)
            return null;
        offset++;

        return new SubParsing<ImportClass>(new ImportClass(qualifiedName.element), offset);
    }

    private SubParsing<QualifiedName> parseQualifiedName(int offset)
    {
        ArrayList<Id> elements = new ArrayList<Id>();
        while (true) {
            Id id = parseId(offset);
            if (id != null) {
                elements.add(id);
                offset++;
            } else
                elements.add(null);
            Token period = getToken(offset);
            if (period.text != Lang.SYMBOL_PERIOD)
                break;
            offset++;
        }
        return new SubParsing<QualifiedName>(new QualifiedName(elements), offset);
    }

    private SubParsing<ClassDeclaration> parseClassDeclaration(int offset)
    {
        SubParsing<ClassModifiers> classModifiers = parseClassModifiers(offset);
        if (classModifiers == null)
            return null;
        offset = classModifiers.end;

        if (getToken(offset).text != Lang.KEYWORD_CLASS)
            return null;
        offset++;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        SubParsing<MaybeImplements> maybeImplements = parseMaybeImplements(offset);
        if (maybeImplements == null)
            return null;
        offset = maybeImplements.end;

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

        return new SubParsing<ClassDeclaration>(new ClassDeclaration(classModifiers.element, id, maybeImplements.element, classBody.element), offset);
    }

    private SubParsing<ClassModifiers> parseClassModifiers(int offset)
    {
        ArrayList<ClassModifier> elements = new ArrayList<ClassModifier>();
        while (true) {
            ClassModifier classModifier = parseClassModifier(offset);
            if (classModifier != null) {
                elements.add(classModifier);
                offset++;
            } else
                break;
        }
        return new SubParsing<ClassModifiers>(new ClassModifiers(elements), offset);
    }

    private ClassModifier parseClassModifier(int offset)
    {
        Token token = getToken(offset);
        if (token.text == Lang.KEYWORD_FINAL)
            return ClassModifier.FINAL;
        if (token.text == Lang.KEYWORD_PUBLIC)
            return ClassModifier.PUBLIC;
        return null;
    }

    private SubParsing<MaybeImplements> parseMaybeImplements(int offset)
    {
        SubParsing<? extends ParseElement> content = null;

        if (content == null)
            content = parseImplementsPart(offset);
        if (content == null)
            content = new SubParsing<MaybeImplements>(new MaybeImplements(EmptyElement.INSTANCE), offset);

        return new SubParsing<MaybeImplements>(new MaybeImplements(content.element), content.end);
    }

    private SubParsing<ImplementsPart> parseImplementsPart(int offset)
    {
        if (getToken(offset).text != Lang.KEYWORD_IMPLEMENTS)
            return null;
        offset++;

        SubParsing<InterfaceList> interfaceList = parseInterfaceList(offset);
        if (interfaceList == null)
            return null;
        offset = interfaceList.end;

        return new SubParsing<ImplementsPart>(new ImplementsPart(interfaceList.element), offset);
    }

    private SubParsing<InterfaceList> parseInterfaceList(int offset)
    {
        ArrayList<TypeId> elements = new ArrayList<TypeId>();
        while (true) {
            SubParsing<TypeId> typeId = parseTypeId(offset);
            if (typeId != null) {
                elements.add(typeId.element);
                offset = typeId.end;
            } else
                break;
            Token semicolon = getToken(offset);
            if (semicolon.text != Lang.SYMBOL_COMMA)
                break;
            offset++;
        }
        return new SubParsing<InterfaceList>(new InterfaceList(elements), offset);
    }

    private SubParsing<ClassBody> parseClassBody(int offset)
    {
        ArrayList<ClassMember> elements = new ArrayList<ClassMember>();
        while (true) {
            SubParsing<ClassMember> classMember = parseClassMember(offset);
            if (classMember != null) {
                elements.add(classMember.element);
                offset = classMember.end;
            } else
                elements.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text == Lang.SYMBOL_SEMICOLON) {
                offset++;
                continue;
            }
            if (options.javaCompatabilityMode) {
                if (classMember != null && classMember.element.decompile().endsWith("}"))
                    continue;
            }
            break;
        }
        return new SubParsing<ClassBody>(new ClassBody(elements), offset);
    }

    private SubParsing<ClassMember> parseClassMember(int offset)
    {
        SubParsing<?> content = null;

        if (content == null)
            content = parseConstructorDeclaration(offset);
        if (content == null)
            content = parseMethodDeclaration(offset);
        // abstract method parsing goes here
        if (content == null)
            content = parseFieldCreation(offset);
        if (content == null)
            content = parseFieldDeclaration(offset);
        if (content == null)
            return null;
        offset = content.end;

        return new SubParsing<ClassMember>(new ClassMember(content.element), offset);
    }

    private SubParsing<FieldDeclaration> parseFieldDeclaration(int offset)
    {
        SubParsing<FieldModifiers> fieldModifiers = parseFieldModifiers(offset);
        if (fieldModifiers == null)
            return null;
        offset = fieldModifiers.end;

        SubParsing<TypeId> typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset = typeId.end;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        return new SubParsing<FieldDeclaration>(new FieldDeclaration(fieldModifiers.element, typeId.element, id), offset);
    }

    private SubParsing<FieldCreation> parseFieldCreation(int offset)
    {
        SubParsing<FieldModifiers> fieldModifiers = parseFieldModifiers(offset);
        if (fieldModifiers == null)
            return null;
        offset = fieldModifiers.end;

        SubParsing<TypeId> typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset = typeId.end;

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

        return new SubParsing<FieldCreation>(new FieldCreation(fieldModifiers.element, typeId.element, id, expression.element), offset);
    }

    private SubParsing<FieldModifiers> parseFieldModifiers(int offset)
    {
        ArrayList<FieldModifier> elements = new ArrayList<FieldModifier>();
        while (true) {
            FieldModifier classModifier = parseFieldModifier(offset);
            if (classModifier != null) {
                elements.add(classModifier);
                offset++;
            } else
                break;
        }
        return new SubParsing<FieldModifiers>(new FieldModifiers(elements), offset);
    }

    private FieldModifier parseFieldModifier(int offset)
    {
        Token token = getToken(offset);
        if (token.text == Lang.KEYWORD_PUBLIC)
            return FieldModifier.PUBLIC;
        if (token.text == Lang.KEYWORD_PRIVATE)
            return FieldModifier.PRIVATE;
        if (token.text == Lang.KEYWORD_PROTECTED)
            return FieldModifier.PROTECTED;
        if (token.text == Lang.KEYWORD_STATIC)
            return FieldModifier.STATIC;
        if (token.text == Lang.KEYWORD_FINAL)
            return FieldModifier.FINAL;
        if (token.text == Lang.KEYWORD_VOLATILE)
            return FieldModifier.VOLATILE;
        if (token.text == Lang.KEYWORD_TRANSIENT)
            return FieldModifier.TRANSIENT;
        return null;
    }

    private SubParsing<ConstructorDeclaration> parseConstructorDeclaration(int offset)
    {
        SubParsing<MethodModifiers> methodModifiers = parseMethodModifiers(offset);
        if (methodModifiers == null)
            return null;
        offset = methodModifiers.end;

        SubParsing<TypeId> typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset = typeId.end;

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

        return new SubParsing<ConstructorDeclaration>(new ConstructorDeclaration(methodModifiers.element, typeId.element, argumentDeclarations.element, expression.element), offset);
    }

    private SubParsing<MethodDeclaration> parseMethodDeclaration(int offset)
    {
        SubParsing<MethodModifiers> methodModifiers = parseMethodModifiers(offset);
        if (methodModifiers == null)
            return null;
        offset = methodModifiers.end;

        SubParsing<TypeId> typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset = typeId.end;

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

        return new SubParsing<MethodDeclaration>(new MethodDeclaration(methodModifiers.element, typeId.element, id, argumentDeclarations.element, expression.element), offset);
    }

    private SubParsing<MethodModifiers> parseMethodModifiers(int offset)
    {
        ArrayList<MethodModifier> elements = new ArrayList<MethodModifier>();
        while (true) {
            MethodModifier classModifier = parseMethodModifier(offset);
            if (classModifier != null) {
                elements.add(classModifier);
                offset++;
            } else
                break;
        }
        return new SubParsing<MethodModifiers>(new MethodModifiers(elements), offset);
    }

    private MethodModifier parseMethodModifier(int offset)
    {
        Token token = getToken(offset);
        if (token.text == Lang.KEYWORD_PUBLIC)
            return MethodModifier.PUBLIC;
        if (token.text == Lang.KEYWORD_PRIVATE)
            return MethodModifier.PRIVATE;
        if (token.text == Lang.KEYWORD_PROTECTED)
            return MethodModifier.PROTECTED;
        if (token.text == Lang.KEYWORD_STATIC)
            return MethodModifier.STATIC;
        if (token.text == Lang.KEYWORD_FINAL)
            return MethodModifier.FINAL;
        if (token.text == Lang.KEYWORD_SYNCHRONIZED)
            return MethodModifier.SYNCHRONIZED;
        if (token.text == Lang.KEYWORD_ABSTRACT)
            return MethodModifier.ABSTRACT;
        if (token.text == Lang.KEYWORD_STRICTFP)
            return MethodModifier.STRICTFP;
        return null;
    }

    private SubParsing<ArgumentDeclarations> parseArgumentDeclarations(int offset)
    {
        ArrayList<VariableDeclaration> variableDeclarations = new ArrayList<VariableDeclaration>();
        while (true) {
            SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
            if (variableDeclaration == null) {
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
        SubParsing<TypeId> typeId = parseTypeId(offset);
        if (typeId == null)
            return null;
        offset = typeId.end;

        Id id = parseId(offset);
        if (id == null)
            return null;
        offset++;

        return new SubParsing<VariableDeclaration>(new VariableDeclaration(typeId.element, id), offset);
    }

    private SubParsing<TypeId> parseTypeId(int offset)
    {
        ScalarType scalarType = parseScalarType(offset);
        if (scalarType == null)
            return null;
        offset++;

        SubParsing<ArrayDimensions> arrayDimensions = parseArrayDimensions(offset);
        if (arrayDimensions == null)
            return null;
        offset = arrayDimensions.end;

        return new SubParsing<TypeId>(new TypeId(scalarType, arrayDimensions.element), offset);
    }

    private SubParsing<ArrayDimensions> parseArrayDimensions(int offset)
    {
        ArrayList<ArrayDimension> elements = new ArrayList<ArrayDimension>();
        while (true) {
            ArrayDimension arrayDimension = parseArrayDimension(offset);
            if (arrayDimension != null) {
                elements.add(arrayDimension);
                offset += 2;
            } else
                break;
        }
        return new SubParsing<ArrayDimensions>(new ArrayDimensions(elements), offset);
    }

    private ArrayDimension parseArrayDimension(int offset)
    {
        if (getToken(offset).text != Lang.SYMBOL_OPEN_BRACKET)
            return null;
        offset++;

        if (getToken(offset).text != Lang.SYMBOL_CLOSE_BRACKET)
            return null;
        offset++;

        return ArrayDimension.INSTANCE;
    }

    private ScalarType parseScalarType(int offset)
    {
        Token token = getToken(offset);
        if (token.getType() == IdentifierToken.TYPE)
            return new ScalarType(new Id(token.text));
        if (token.text == Lang.KEYWORD_INT)
            return ScalarType.INT;
        if (token.text == Lang.KEYWORD_BYTE)
            return ScalarType.BYTE;
        if (token.text == Lang.KEYWORD_FLOAT)
            return ScalarType.FLOAT;
        if (token.text == Lang.KEYWORD_DOUBLE)
            return ScalarType.DOUBLE;
        if (token.text == Lang.KEYWORD_VOID)
            return ScalarType.VOID;
        if (token.text == Lang.KEYWORD_BOOLEAN)
            return ScalarType.BOOLEAN;
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
        ArrayList<Expression> elements = new ArrayList<Expression>();
        while (true) {
            SubParsing<Expression> expression = parseExpression(offset);
            if (expression != null) {
                elements.add(expression.element);
                offset = expression.end;
            } else
                elements.add(null);
            Token semicolon = getToken(offset);
            if (semicolon.text == Lang.SYMBOL_SEMICOLON) {
                offset++;
                continue;
            }
            if (options.javaCompatabilityMode) {
                if (expression != null && expression.element.decompile().endsWith("}"))
                    continue;
            }
            break;
        }
        return new SubParsing<BlockContents>(new BlockContents(elements), offset);
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
    private SubParsing<MethodInvocation> parseMethodInvocation(int offset)
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

        return new SubParsing<MethodInvocation>(new MethodInvocation(id, arguments.element), offset);
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
    private SubParsing<CatchList> parseCatchList(int offset)
    {
        ArrayList<CatchBody> catchBodies = new ArrayList<CatchBody>();
        while (true) {
            SubParsing<CatchBody> catchBody = parseCatchBody(offset);
            if (catchBody == null) {
                if (catchBodies.size() == 0)
                    break;
                else
                    return null;
            }
            catchBodies.add(catchBody.element);
            offset = catchBody.end;

            if (getToken(offset).text != Lang.SYMBOL_COMMA)
                break;
            offset++;
        }
        return new SubParsing<CatchList>(new CatchList(catchBodies), offset);
    }

    private SubParsing<CatchBody> parseCatchBody(int offset)
    {
        if (getToken(offset).text != Lang.SYMBOL_OPEN_PARENS)
            return null;
        offset++;

        SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
        if (variableDeclaration == null)
            return null;
        offset = variableDeclaration.end;

        if (getToken(offset).text != Lang.SYMBOL_CLOSE_PARENS)
            return null;
        offset++;

        SubParsing<Expression> expression = parseExpression(offset);
        if (expression == null)
            return null;
        offset = expression.end;

        return new SubParsing<CatchBody>(new CatchBody(variableDeclaration.element, expression.element), offset);
    }

    private SubParsing<Arguments> parseArguments(int offset)
    {
        ArrayList<Expression> elements = new ArrayList<Expression>();
        while (true) {
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
            while (offset < tokens.length) {
                HashMap<String, List<ExpressionOperator>> operators;
                if (hasOpenTop()) {
                    SubParsing<VariableCreation> variableCreation = parseVariableCreation(offset);
                    if (variableCreation != null) {
                        pushUnit(variableCreation.element);
                        offset = variableCreation.end;
                        continue;
                    }
                    SubParsing<VariableDeclaration> variableDeclaration = parseVariableDeclaration(offset);
                    if (variableDeclaration != null) {
                        pushUnit(variableDeclaration.element);
                        offset = variableDeclaration.end;
                        continue;
                    }
                    LiteralElement literal = parseLiteral(offset);
                    if (literal != null) {
                        pushUnit(literal);
                        offset++;
                        continue;
                    }
                    SubParsing<MethodInvocation> methodInvocation = parseMethodInvocation(offset);
                    if (methodInvocation != null) {
                        pushUnit(methodInvocation.element);
                        offset = methodInvocation.end;
                        continue;
                    }
                    SubParsing<Assignment> assigment = parseAssignment(offset);
                    if (assigment != null) {
                        pushUnit(assigment.element);
                        offset = assigment.end;
                        continue;
                    }
                    Id id = parseId(offset);
                    if (id != null) {
                        pushUnit(id);
                        offset++;
                        continue;
                    }
                    EnclosedSubParsing typeIdCast = parseTypeIdCast(offset);
                    if (typeIdCast != null) {
                        pushClosedLeftOperator(ExpressionOperator.typeIdCast, typeIdCast.expressions);
                        offset = typeIdCast.end;
                        continue;
                    }
                    operators = ExpressionOperator.CLOSED_LEFT;
                } else
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
            for (ExpressionOperator op : ops) {
                ArrayList<ParseElement> innerExpressions = null;
                if (op instanceof ExpressionEnclosingOperator) {
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
            for (int i = 0; i < elements.length; i++) {
                Object expectcedElement = elements[i];
                if (expectcedElement.getClass() == String.class) {
                    if (!(i < partialSubParsings.size() && partialSubParsings.get(i) == null)) {
                        // history was missing/wrong
                        Util.removeAfter(partialSubParsings, i);
                        Token token = getToken(offset);
                        if (token.text != expectcedElement)
                            return null;
                        partialSubParsings.add(null);
                    }
                    offset++;
                } else {
                    SubParsing<?> innerParsing = null;
                    if (i < partialSubParsings.size())
                        innerParsing = partialSubParsings.get(i);
                    switch ((Integer)expectcedElement) {
                        case Expression.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != Expression.TYPE) {
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
                            if (innerParsing == null || innerParsing.element.getElementType() != BlockContents.TYPE) {
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
                        case MethodInvocation.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != MethodInvocation.TYPE) {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseMethodInvocation(offset);
                                if (innerParsing == null)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case Id.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != Id.TYPE) {
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
                            if (innerParsing == null || innerParsing.element.getElementType() != TryPart.TYPE) {
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
                            if (innerParsing == null || innerParsing.element.getElementType() != CatchPart.TYPE) {
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
                        case PrimitiveType.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != PrimitiveType.TYPE) {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseTypeId(offset);
                                if (innerParsing == null)
                                    return null;
                                if (((TypeId)innerParsing.element).scalarType.content.getElementType() != PrimitiveType.TYPE)
                                    return null;
                                partialSubParsings.add(innerParsing);
                            }
                            offset = innerParsing.end;
                            innerElements.add(innerParsing.element);
                            break;
                        case Arguments.TYPE:
                            if (innerParsing == null || innerParsing.element.getElementType() != Arguments.TYPE) {
                                // history was missing/wrong
                                Util.removeAfter(partialSubParsings, i);
                                innerParsing = parseArguments(offset);
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
            if (op.rightPrecedence == -1) {
                // immediately group postfix operators
                Expression leftExpression = ((ExpressionStackElement)stack.pop()).expression;
                stack.push(new ExpressionStackElement(new Expression(op.makeExpressionContent(leftExpression, innerElements, null)), getTopPrecedence()));
            } else
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
            while (precedence < getTopPrecedence()) {
                Expression rightExpression = ((ExpressionStackElement)stack.pop()).expression;
                OperatorStackElement operatorElement = (OperatorStackElement)stack.pop();
                Expression leftExpression = operatorElement.op.leftPrecedence == -1 ? null : ((ExpressionStackElement)stack.pop()).expression;
                ParseElement expressionContent = operatorElement.op.makeExpressionContent(leftExpression, operatorElement.innerExpressions, rightExpression);
                stack.push(new ExpressionStackElement(new Expression(expressionContent), getTopPrecedence()));
            }
        }
        private EnclosedSubParsing parseTypeIdCast(int offset)
        {
            if (getToken(offset).text != Lang.SYMBOL_OPEN_PARENS)
                return null;
            offset++;

            SubParsing<TypeId> typeId = parseTypeId(offset);
            if (typeId == null)
                return null;
            offset = typeId.end;

            if (getToken(offset).text != Lang.SYMBOL_CLOSE_PARENS)
                return null;
            offset++;

            String betterNotBeAPlusOrMinus = getToken(offset).text;
            if (betterNotBeAPlusOrMinus == Lang.SYMBOL_PLUS || betterNotBeAPlusOrMinus == Lang.SYMBOL_MINUS)
                return null;

            ArrayList<ParseElement> elements = new ArrayList<ParseElement>();
            elements.add(typeId.element);
            return new EnclosedSubParsing(elements, offset);
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
                return "([" + Util.join(expressions, ", ") + "], " + end + ")";
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
        maxIndex = Math.max(maxIndex, offset);
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
