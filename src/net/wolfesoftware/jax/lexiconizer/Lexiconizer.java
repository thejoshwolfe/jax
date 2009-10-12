package net.wolfesoftware.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.parser.Parsing;
import net.wolfesoftware.jax.util.Util;

public class Lexiconizer
{
    public static Lexiconization lexiconize(Parsing parsing, String filePath)
    {
        return new Lexiconizer(parsing, filePath).lexiconizeRoot();
    }

    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        RuntimeType.initPrimitives(importedTypes);
        RuntimeType.initJavaLang(importedTypes);
    }
    private final Root root;
    private final String filePath;
    private final ArrayList<LexicalException> errors = new ArrayList<LexicalException>();

    private Lexiconizer(Parsing parsing, String filePath)
    {
        root = parsing.root;
        this.filePath = filePath;
    }

    private Lexiconization lexiconizeRoot()
    {
        try {
            lexiconizeCompilationUnit(root.content);
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        return new Lexiconization(root, errors);
    }

    private void lexiconizeCompilationUnit(CompilationUnit compilationUnit)
    {
        lexiconizeImports(compilationUnit.imports);
        lexiconizeClassDeclaration(compilationUnit.classDeclaration);
    }

    private void lexiconizeImports(Imports imports)
    {
        for (ImportStatement importStatement : imports.elements)
            lexiconizeImportStatement(importStatement);
    }

    private void lexiconizeImportStatement(ImportStatement importStatement)
    {
        ParseElement content = importStatement.content;
        switch (importStatement.content.getElementType()) {
            case ImportStar.TYPE:
                lexiconizeImportStar((ImportStar)content);
                break;
            case ImportClass.TYPE:
                lexiconizeImportClass((ImportClass)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private void lexiconizeImportStar(ImportStar importStar)
    {
        deleteNulls(importStar.qualifiedName);
        BuiltinPackageLister.importPackageStar(importStar.qualifiedName, importedTypes, errors);
    }

    private void lexiconizeImportClass(ImportClass importClass)
    {
        resolveQualifiedName(importClass.qualifiedName);
    }

    private void lexiconizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        // verify package here
        // verify className and fileName match
        String classNameFromFile = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
        if (!classDeclaration.id.name.equals(classNameFromFile))
            errors.add(new LexicalException(classDeclaration.id, "Class name does not match file name"));

        LocalType context = new LocalType(classNameFromFile, classDeclaration.id.name);
        importedTypes.put(classNameFromFile, context);
        lexiconizeClassBody(context, classDeclaration.classBody);
    }

    private void lexiconizeClassBody(LocalType context, ClassBody classBody)
    {
        deleteNulls(classBody);

        for (ClassMember classMember : classBody.elements)
            preLexiconizeClassMemeber(context, classMember);

        for (ClassMember classMember : classBody.elements)
            lexiconizeClassMemeber(context, classMember);
    }

    private void preLexiconizeClassMemeber(LocalType context, ClassMember topLevelItem)
    {
        if (topLevelItem == null)
            return;
        ParseElement content = topLevelItem.content;
        switch (content.getElementType()) {
            case FunctionDefinition.TYPE:
                preLexiconizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException("TODO: implement " + Integer.toString(content.getElementType(), 16));
        }
    }

    private void preLexiconizeFunctionDefinition(LocalType context, FunctionDefinition functionDefinition)
    {
        resolveType(functionDefinition.typeId);
        functionDefinition.context = new RootLocalContext(context);
        Type[] arguemntSignature = lexiconizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.method = new LocalMethod(context, functionDefinition.typeId.type, functionDefinition.id.name, arguemntSignature, true);
        context.addMethod(functionDefinition.method);
    }

    private Type[] lexiconizeArgumentDeclarations(LocalContext context, ArgumentDeclarations argumentDeclarations)
    {
        Type[] argumentSignature = new Type[argumentDeclarations.elements.size()];
        int i = 0;
        for (VariableDeclaration variableDeclaration : argumentDeclarations.elements) {
            lexiconizeVariableDeclaration(context, variableDeclaration);
            argumentSignature[i++] = variableDeclaration.typeId.type;
        }
        return argumentSignature;
    }

    private void lexiconizeClassMemeber(LocalType context, ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType()) {
            case FunctionDefinition.TYPE:
                lexiconizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException("TODO: implement " + Integer.toString(content.getElementType(), 16));
        }
    }

    private void lexiconizeFunctionDefinition(LocalType context, FunctionDefinition functionDefinition)
    {
        functionDefinition.returnBehavior = lexiconizeExpression(functionDefinition.context, functionDefinition.expression);
        if (functionDefinition.method.returnType != functionDefinition.returnBehavior.type)
            errors.add(LexicalException.cantCast(functionDefinition.expression, functionDefinition.returnBehavior.type, functionDefinition.method.returnType));
        if (functionDefinition.returnBehavior.type != RuntimeType.VOID)
            functionDefinition.context.modifyStack(-1); // return
        Util._assert(functionDefinition.context.stackSize == 0);
    }

    private ReturnBehavior lexiconizeExpression(LocalContext context, Expression expression)
    {
        if (expression == null)
            return ReturnBehavior.VOID;
        ParseElement content = expression.content;
        ReturnBehavior returnBehavior;
        switch (content.getElementType()) {
            case Addition.TYPE:
                returnBehavior = lexiconizeAddition(context, (Addition)content);
                break;
            case Subtraction.TYPE:
                returnBehavior = lexiconizeSubtraction(context, (Subtraction)content);
                break;
            case Multiplication.TYPE:
                returnBehavior = lexiconizeMultiplication(context, (Multiplication)content);
                break;
            case Division.TYPE:
                returnBehavior = lexiconizeDivision(context, (Division)content);
                break;
            case LessThan.TYPE:
                returnBehavior = lexiconizeLessThan(context, (LessThan)content);
                break;
            case GreaterThan.TYPE:
                returnBehavior = lexiconizeGreaterThan(context, (GreaterThan)content);
                break;
            case LessThanOrEqual.TYPE:
                returnBehavior = lexiconizeLessThanOrEqual(context, (LessThanOrEqual)content);
                break;
            case GreaterThanOrEqual.TYPE:
                returnBehavior = lexiconizeGreaterThanOrEqual(context, (GreaterThanOrEqual)content);
                break;
            case Equality.TYPE:
                returnBehavior = lexiconizeEquality(context, (Equality)content);
                break;
            case Inequality.TYPE:
                returnBehavior = lexiconizeInequality(context, (Inequality)content);
                break;
            case Id.TYPE:
                returnBehavior = lexiconizeId(context, (Id)content);
                break;
            case Block.TYPE:
                returnBehavior = lexiconizeBlock(context, (Block)content);
                break;
            case IntLiteral.TYPE:
                returnBehavior = lexiconizeIntLiteral(context, (IntLiteral)content);
                break;
            case BooleanLiteral.TYPE:
                returnBehavior = lexiconizeBooleanLiteral(context, (BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                returnBehavior = lexiconizeStringLiteral(context, (StringLiteral)content);
                break;
            case Quantity.TYPE:
                returnBehavior = lexiconizeQuantity(context, (Quantity)content);
                break;
            case VariableCreation.TYPE:
                returnBehavior = lexiconizeVariableCreation(context, (VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                returnBehavior = lexiconizeVariableDeclaration(context, (VariableDeclaration)content);
                break;
            case Assignment.TYPE:
                returnBehavior = lexiconizeAssignment(context, (Assignment)content);
                break;
            case IfThenElse.TYPE:
                returnBehavior = lexiconizeIfThenElse(context, (IfThenElse)content);
                break;
            case IfThen.TYPE:
                returnBehavior = lexiconizeIfThen(context, (IfThen)content);
                break;
            case ForLoop.TYPE:
                returnBehavior = lexiconizeForLoop(context, (ForLoop)content);
                break;
            case FunctionInvocation.TYPE:
                returnBehavior = lexiconizeFunctionInvocation(context, (FunctionInvocation)content);
                break;
            case DereferenceMethod.TYPE:
                switch (disambuateDereferenceMethod(context, expression)) {
                    case DereferenceMethod.TYPE:
                        returnBehavior = lexiconizeDereferenceMethod(context, (DereferenceMethod)content);
                        break;
                    case StaticFunctionInvocation.TYPE:
                        returnBehavior = lexiconizeStaticFunctionInvocation(context, (StaticFunctionInvocation)expression.content);
                        break;
                    default:
                        throw new RuntimeException();
                }
                break;
            case DereferenceField.TYPE:
                switch (disambuateDereferenceField(context, expression)) {
                    case DereferenceField.TYPE:
                        returnBehavior = lexiconizeDereferenceField(context, (DereferenceField)content);
                        break;
                    case StaticDereferenceField.TYPE:
                        returnBehavior = lexiconizeStaticDereferenceField(context, (StaticDereferenceField)expression.content);
                        break;
                    default:
                        throw new RuntimeException();
                }
                break;
            case ArrayDereference.TYPE:
                returnBehavior = lexiconizeArrayDereference(context, (ArrayDereference)content);
                break;
            case TryCatch.TYPE:
                returnBehavior = lexiconizeTryCatch(context, (TryCatch)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeForLoop(LocalContext context, ForLoop forLoop)
    {
        LocalContext innerContext = new LocalContext(context);
        ReturnBehavior returnBehavior1 = lexiconizeExpression(innerContext, forLoop.expression1);
        if (returnBehavior1.type != RuntimeType.VOID)
            errors.add(LexicalException.mustBeVoid(forLoop.expression1));

        forLoop.continueToLabel = innerContext.nextLabel();
        ReturnBehavior returnBehavior3 = lexiconizeExpression(innerContext, forLoop.expression3);
        if (returnBehavior3.type != RuntimeType.VOID)
            innerContext.modifyStack(-1);

        forLoop.initialGotoLabel = innerContext.nextLabel();
        ReturnBehavior returnBehavior2 = lexiconizeExpression(innerContext, forLoop.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(forLoop.expression2));
        innerContext.modifyStack(-1);

        ReturnBehavior returnBehavior4 = lexiconizeExpression(innerContext, forLoop.expression4);
        if (returnBehavior4.type != RuntimeType.VOID)
            errors.add(LexicalException.mustBeVoid(forLoop.expression4));

        forLoop.breakToLabel = innerContext.nextLabel();

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeArrayDereference(LocalContext context, ArrayDereference arrayDereference)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, arrayDereference.expression1);
        if (returnBehavior1.type.getType() != ArrayType.TYPE) {
            errors.add(new LexicalException(arrayDereference, "Can't dereference this thing like an array"));
            // TODO: handle this more gracefully
        }
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, arrayDereference.expression2);
        if (returnBehavior2.type != RuntimeType.INT)
            errors.add(new LexicalException(arrayDereference.expression2, "Must be int"));
        context.modifyStack(-2 + 1);

        Type scalarType = ((ArrayType)returnBehavior1.type).scalarType;
        return new ReturnBehavior(scalarType);
    }

    private ReturnBehavior lexiconizeStaticDereferenceField(LocalContext context, StaticDereferenceField staticDereferenceField)
    {
        // lexiconization already done for typeId
        staticDereferenceField.field = resolveField(staticDereferenceField.typeId.type, staticDereferenceField.id);
        context.modifyStack(1);
        return new ReturnBehavior(staticDereferenceField.field.returnType);
    }

    private ReturnBehavior lexiconizeStaticFunctionInvocation(LocalContext context, StaticFunctionInvocation staticFunctionInvocation)
    {
        // lexiconization already done for typeId
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, staticFunctionInvocation.functionInvocation.arguments);
        staticFunctionInvocation.functionInvocation.method = resolveMethod(staticFunctionInvocation.typeId.type, staticFunctionInvocation.functionInvocation.id, argumentSignature);
        context.modifyStack(-argumentSignature.length);
        Type returnType = staticFunctionInvocation.functionInvocation.method.returnType;
        if (returnType != RuntimeType.VOID)
            context.modifyStack(1);
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior lexiconizeTryCatch(LocalContext context, TryCatch tryCatch)
    {
        ReturnBehavior tryPartReturnBehavior = lexiconizeTryPart(context, tryCatch.tryPart);
        if (tryPartReturnBehavior.type != RuntimeType.VOID)
            context.modifyStack(-1); // take this off for now

        ReturnBehavior catchPartReturnBehavior = lexiconizeCatchPart(context, tryCatch.catchPart);
        tryCatch.endLabel = context.nextLabel();
        if (catchPartReturnBehavior.type != RuntimeType.VOID)
            context.modifyStack(-1); // take this off for now

        if (tryPartReturnBehavior.type != catchPartReturnBehavior.type)
            errors.add(new LexicalException(tryCatch, "return types must match"));
        if (tryPartReturnBehavior.type != RuntimeType.VOID)
            context.modifyStack(1); // and now put it back on
        return new ReturnBehavior(tryPartReturnBehavior.type);
    }

    private ReturnBehavior lexiconizeTryPart(LocalContext context, TryPart tryPart)
    {
        tryPart.startLabel = context.nextLabel();
        lexiconizeExpression(context, tryPart.expression);
        tryPart.endLabel = context.nextLabel();
        return tryPart.expression.returnBehavior;
    }

    private ReturnBehavior lexiconizeCatchPart(LocalContext context, CatchPart catchPart)
    {
        return lexiconizeCatchList(context, catchPart.catchList);
    }

    private ReturnBehavior lexiconizeCatchList(LocalContext context, CatchList catchList)
    {
        Type returnType = null;
        for (CatchBody catchBody : catchList.elements) {
            ReturnBehavior returnBehavior = lexiconizeCatchBody(context, catchBody);
            if (returnType == null)
                returnType = returnBehavior.type;
            else if (returnType != returnBehavior.type)
                errors.add(new LexicalException(catchList, "return types must match"));
        }
        if (returnType == null)
            errors.add(new LexicalException(catchList, "must catch something"));
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior lexiconizeCatchBody(LocalContext context, CatchBody catchBody)
    {
        LocalContext nestedContext = new LocalContext(context);
        catchBody.startLabel = nestedContext.nextLabel();
        lexiconizeVariableDeclaration(nestedContext, catchBody.variableDeclaration);
        catchBody.endLabel = nestedContext.nextLabel();
        if (!catchBody.variableDeclaration.typeId.type.isInstanceOf(RuntimeType.getType(Throwable.class)))
            errors.add(new LexicalException(catchBody.variableDeclaration, "Type must descend from Throwable. Can't catch a " + catchBody.variableDeclaration.typeId));
        nestedContext.modifyStack(1); // exception object
        nestedContext.modifyStack(-1);
        ReturnBehavior returnBehavior = lexiconizeExpression(nestedContext, catchBody.expression);
        // TODO one for the exception object
        return new ReturnBehavior(returnBehavior.type);
    }

    private int disambuateDereferenceField(LocalContext context, Expression expression)
    {
        // TODO reduce code duplication in /disambiguate.*/
        DereferenceField dereferenceField = (DereferenceField)expression.content;
        if (dereferenceField.expression.content.getElementType() != Id.TYPE)
            return DereferenceField.TYPE;
        LocalVariable localVariable = resolveId(context, (Id)dereferenceField.expression.content);
        if (localVariable != null)
            return DereferenceField.TYPE;
        TypeId typeId = new TypeId(new ScalarType(dereferenceField.expression.content), ArrayDimensions.EMPTY);
        resolveType(typeId);
        if (typeId.type != null) {
            // convert to StaticDereferenceField
            expression.content = new StaticDereferenceField(typeId, dereferenceField.id);
            return StaticDereferenceField.TYPE;
        }
        throw new RuntimeException(); // TODO what do we do here?
    }

    private ReturnBehavior lexiconizeDereferenceField(LocalContext context, DereferenceField dereferenceField)
    {
        throw new RuntimeException("TODO: implement lexiconizeDereferenceField()");
    }

    private int disambuateDereferenceMethod(LocalContext context, Expression expression)
    {
        // TODO reduce code duplication in /disambiguate.*/
        DereferenceMethod dereferenceMethod = (DereferenceMethod)expression.content;
        if (dereferenceMethod.expression.content.getElementType() != Id.TYPE)
            return DereferenceMethod.TYPE;
        LocalVariable localVariable = resolveId(context, (Id)dereferenceMethod.expression.content);
        if (localVariable != null)
            return DereferenceMethod.TYPE;
        TypeId typeId = new TypeId(new ScalarType(dereferenceMethod.expression.content), ArrayDimensions.EMPTY);
        resolveType(typeId);
        if (typeId.type != null) {
            // convert to StaticFunctionInvocation
            expression.content = new StaticFunctionInvocation(typeId, dereferenceMethod.functionInvocation);
            return StaticFunctionInvocation.TYPE;
        }
        return -1; // TODO return something
    }

    private ReturnBehavior lexiconizeDereferenceMethod(LocalContext context, DereferenceMethod dereferenceMethod)
    {
        ReturnBehavior expressionReturnBehavior = lexiconizeExpression(context, dereferenceMethod.expression);
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, dereferenceMethod.functionInvocation.arguments);
        dereferenceMethod.functionInvocation.method = resolveMethod(expressionReturnBehavior.type, dereferenceMethod.functionInvocation.id, argumentSignature);
        context.modifyStack(-(1 + argumentSignature.length));
        if (dereferenceMethod.functionInvocation.method.returnType != RuntimeType.VOID)
            context.modifyStack(1);
        return new ReturnBehavior(dereferenceMethod.functionInvocation.method.returnType);
    }

    private ReturnBehavior lexiconizeFunctionInvocation(LocalContext context, FunctionInvocation functionInvocation)
    {
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, functionInvocation.arguments);
        functionInvocation.method = resolveFunction(context.getClassContext(), functionInvocation.id, argumentSignature);
        context.modifyStack(-argumentSignature.length);
        if (functionInvocation.method.returnType != RuntimeType.VOID)
            context.modifyStack(1);
        return new ReturnBehavior(functionInvocation.method.returnType);
    }

    private ReturnBehavior[] lexiconizeArguments(LocalContext context, Arguments arguments)
    {
        deleteNulls(arguments);
        ReturnBehavior[] rtnArr = new ReturnBehavior[arguments.elements.size()];
        int i = 0;
        for (Expression element : arguments.elements)
            rtnArr[i++] = lexiconizeExpression(context, element);
        return rtnArr;
    }

    private ReturnBehavior lexiconizeIfThenElse(LocalContext context, IfThenElse ifThenElse)
    {
        lexiconizeExpression(context, ifThenElse.expression1);
        if (ifThenElse.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(ifThenElse.expression1));
        context.modifyStack(-1);

        ifThenElse.label1 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression2);
        if (ifThenElse.expression2.returnBehavior.type != RuntimeType.VOID)
            context.modifyStack(-1); // for now, take this off

        ifThenElse.label2 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression3);
        if (ifThenElse.expression3.returnBehavior.type != RuntimeType.VOID)
            context.modifyStack(-1); // for now, take this off

        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new LexicalException(ifThenElse, "return types must match"));
        if (ifThenElse.expression2.returnBehavior.type != RuntimeType.VOID)
            context.modifyStack(1); // and now put it back
        return new ReturnBehavior(ifThenElse.expression2.returnBehavior.type);
    }
    private ReturnBehavior lexiconizeIfThen(LocalContext context, IfThen ifThen)
    {
        lexiconizeExpression(context, ifThen.expression1);
        if (ifThen.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(ifThen.expression1));
        context.modifyStack(-1);
        ifThen.label = context.nextLabel();

        lexiconizeExpression(context, ifThen.expression2);
        if (ifThen.expression2.returnBehavior.type != RuntimeType.VOID)
            errors.add(LexicalException.mustBeVoid(ifThen.expression2));

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeAssignment(LocalContext context, Assignment assignment)
    {
        assignment.id.variable = resolveId(context, assignment.id);
        if (assignment.id.variable == null)
            errors.add(LexicalException.cantResolveLocalVariable(assignment.id));
        ReturnBehavior returnBehavior = lexiconizeExpression(context, assignment.expression);
        if (assignment.id.variable != null && assignment.id.variable.type != returnBehavior.type)
            errors.add(LexicalException.cantCast(assignment.expression, returnBehavior.type, assignment.id.variable.type));
        context.modifyStack(1); // "dup" for multiassignment. This is likely to change in the future.
        context.modifyStack(-1);
        return new ReturnBehavior(returnBehavior.type);
    }

    private ReturnBehavior lexiconizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        resolveType(variableDeclaration.typeId);
        if (variableDeclaration.typeId.type == null)
            errors.add(new LexicalException(variableDeclaration.typeId, "Dunno what this type is."));
        else if (variableDeclaration.typeId.type == RuntimeType.VOID)
            errors.add(new LexicalException(variableDeclaration, "You can't have a void variable."));
        context.addLocalVariable(variableDeclaration.id, variableDeclaration.typeId.type, errors);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeId(LocalContext context, Id id)
    {
        id.variable = resolveId(context, id);
        if (id.variable == null) {
            errors.add(LexicalException.cantResolveLocalVariable(id));
            // TODO: return something
        }
        context.modifyStack(1);
        return new ReturnBehavior(id.variable.type);
    }

    private ReturnBehavior lexiconizeVariableCreation(LocalContext context, VariableCreation variableCreation)
    {
        lexiconizeVariableDeclaration(context, variableCreation.variableDeclaration);
        ReturnBehavior returnBehavior = lexiconizeExpression(context, variableCreation.expression);
        if (variableCreation.variableDeclaration.typeId.type != returnBehavior.type)
            errors.add(LexicalException.cantCast(variableCreation.expression, returnBehavior.type, variableCreation.variableDeclaration.typeId.type));
        context.modifyStack(-1);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeBlock(LocalContext context, Block block)
    {
        block.context = new LocalContext(context);
        return lexiconizeBlockContents(block.context, block.blockContents);
    }

    private ReturnBehavior lexiconizeBlockContents(LocalContext context, BlockContents blockContents)
    {
        blockContents.forceVoid = blockContents.elements.size() == 0 || blockContents.elements.get(blockContents.elements.size() - 1) == null;

        deleteNulls(blockContents);

        Type returnType = RuntimeType.VOID;
        for (Expression element : blockContents.elements) {
            ReturnBehavior returnBehavior = lexiconizeExpression(context, element);
            returnType = returnBehavior.type;
            if (returnType != RuntimeType.VOID)
                context.modifyStack(-1); // assume the block does not return this value
        }
        if (blockContents.forceVoid)
            returnType = RuntimeType.VOID;
        if (returnType != RuntimeType.VOID)
            context.modifyStack(1); // turns out the block does return a value
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior lexiconizeQuantity(LocalContext context, Quantity quantity)
    {
        return lexiconizeExpression(context, quantity.expression);
    }

    private ReturnBehavior lexiconizeIntLiteral(LocalContext context, IntLiteral intLiteral)
    {
        context.modifyStack(1);
        return ReturnBehavior.INT;
    }
    private ReturnBehavior lexiconizeBooleanLiteral(LocalContext context, BooleanLiteral booleanLiteral)
    {
        context.modifyStack(1);
        return ReturnBehavior.BOOLEAN;
    }
    private ReturnBehavior lexiconizeStringLiteral(LocalContext context, StringLiteral stringLiteral)
    {
        context.modifyStack(1);
        return ReturnBehavior.STRING;
    }

    private ReturnBehavior lexiconizeAddition(LocalContext context, Addition addition)
    {
        return lexiconizeIntOperator(context, addition);
    }
    private ReturnBehavior lexiconizeSubtraction(LocalContext context, Subtraction subtraction)
    {
        return lexiconizeIntOperator(context, subtraction);
    }
    private ReturnBehavior lexiconizeMultiplication(LocalContext context, Multiplication multiplication)
    {
        return lexiconizeIntOperator(context, multiplication);
    }
    private ReturnBehavior lexiconizeDivision(LocalContext context, Division division)
    {
        return lexiconizeIntOperator(context, division);
    }
    private ReturnBehavior lexiconizeIntOperator(LocalContext context, BinaryOperatorElement operator)
    {
        ReturnBehavior returnBehavior = lexiconizeOperator(context, operator, null);
        if (returnBehavior.type != RuntimeType.INT)
            errors.add(new LexicalException(operator, "Expression must be of type int"));
        return returnBehavior;
    }
    private ReturnBehavior lexiconizeLessThan(LocalContext context, LessThan lessThan)
    {
        return lexiconizeComparisonOperator(context, lessThan);
    }
    private ReturnBehavior lexiconizeGreaterThan(LocalContext context, GreaterThan greaterThan)
    {
        return lexiconizeComparisonOperator(context, greaterThan);
    }
    private ReturnBehavior lexiconizeLessThanOrEqual(LocalContext context, LessThanOrEqual lessThanOrEqual)
    {
        return lexiconizeComparisonOperator(context, lessThanOrEqual);
    }
    private ReturnBehavior lexiconizeGreaterThanOrEqual(LocalContext context, GreaterThanOrEqual greaterThanOrEqual)
    {
        return lexiconizeComparisonOperator(context, greaterThanOrEqual);
    }
    private ReturnBehavior lexiconizeEquality(LocalContext context, Equality equality)
    {
        return lexiconizeComparisonOperator(context, equality);
    }
    private ReturnBehavior lexiconizeInequality(LocalContext context, Inequality inequality)
    {
        return lexiconizeComparisonOperator(context, inequality);
    }
    private ReturnBehavior lexiconizeComparisonOperator(LocalContext context, ComparisonOperator operator)
    {
        operator.label1 = context.nextLabel();
        operator.label2 = context.nextLabel();
        return lexiconizeOperator(context, operator, RuntimeType.BOOLEAN);
    }
    private ReturnBehavior lexiconizeOperator(LocalContext context, BinaryOperatorElement operator, Type returnType)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, operator.expression1);
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, operator.expression2);
        if (returnBehavior1.type != returnBehavior2.type)
            errors.add(new LexicalException(operator, "comparison types must match."));
        context.modifyStack(-2 + 1);
        return new ReturnBehavior(returnType != null ? returnType : returnBehavior1.type);
    }

    private void resolveQualifiedName(QualifiedName qualifiedName)
    {
        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = 0; i < qualifiedName.elements.size() - 1; i++)
            stringBuilder.append(qualifiedName.elements.get(i).name).append('.');
        String typeName = qualifiedName.elements.get(i).name;
        stringBuilder.append(typeName);
        String fullTypeName = stringBuilder.toString();
        try {
            Class<?> runtimeType = Class.forName(fullTypeName);
            importedTypes.put(typeName, RuntimeType.getType(runtimeType));
        } catch (ClassNotFoundException e) {
            errors.add(LexicalException.cantResolveImport(qualifiedName));
        }
    }

    private void resolveType(TypeId typeId)
    {
        int arrayOrder = typeId.arrayDimensions.elements.size();
        Type type = importedTypes.get(typeId.scalarType.toString());
        if (type == null)
            return;
        while (arrayOrder-- > 0)
            type = ArrayType.getType(type);
        typeId.type = type;
    }

    private Method resolveFunction(LocalType context, Id id, ReturnBehavior[] argumentSignature)
    {
        return context.resolveMethod(id.name, getArgumentTypes(argumentSignature));
    }

    private Method resolveMethod(Type type, Id id, ReturnBehavior[] argumentSignature)
    {
        return type.resolveMethod(id.name, getArgumentTypes(argumentSignature));
    }

    private LocalVariable resolveId(LocalContext context, Id id)
    {
        return context.getLocalVariable(id.name);
    }
    private Field resolveField(Type type, Id id)
    {
        return type.resolveField(id.name);
    }

    private static Type[] getArgumentTypes(ReturnBehavior[] argumentSignature)
    {
        Type[] argumentTypes = new Type[argumentSignature.length];
        for (int i = 0; i < argumentSignature.length; i++)
            argumentTypes[i] = argumentSignature[i].type;
        return argumentTypes;
    }
    private static void deleteNulls(ListElement<?> listElement)
    {
        Iterator<?> iterator = listElement.elements.iterator();
        while (iterator.hasNext())
            if (iterator.next() == null)
                iterator.remove();
    }
}