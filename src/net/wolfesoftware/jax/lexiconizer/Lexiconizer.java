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
        boolean broken = true;
        try {
            lexiconizeCompilationUnit(root.content);
            broken = false;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        if (broken && errors.isEmpty())
            errors.add(new LexicalException(new Id(""), "Things are broken"));
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

        classDeclaration.localType = new LocalType(classNameFromFile, classDeclaration.id.name);
        importedTypes.put(classNameFromFile, classDeclaration.localType);
        lexiconizeClassBody(classDeclaration.localType, classDeclaration.classBody);
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
        if (context.constructors.size() == 0)
            context.constructors.add(new Constructor(context, new Type[0]));
    }

    private void preLexiconizeFunctionDefinition(LocalType context, FunctionDefinition functionDefinition)
    {
        resolveType(functionDefinition.typeId, true);
        functionDefinition.context = new RootLocalContext(context);
        Type[] arguemntSignature = lexiconizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.method = new Method(context, functionDefinition.typeId.type, functionDefinition.id.name, arguemntSignature, true);
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

    private void lexiconizeClassMemeber(Type context, ClassMember classMember)
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

    private void lexiconizeFunctionDefinition(Type context, FunctionDefinition functionDefinition)
    {
        functionDefinition.returnBehavior = lexiconizeExpression(functionDefinition.context, functionDefinition.expression);
        if (functionDefinition.method.returnType != functionDefinition.returnBehavior.type)
            errors.add(LexicalException.cantCast(functionDefinition.expression, functionDefinition.returnBehavior.type, functionDefinition.method.returnType));
        functionDefinition.context.modifyStack(-functionDefinition.returnBehavior.type.getSize());
        Util._assert(functionDefinition.context.stackSize == 0);
    }

    private ReturnBehavior lexiconizeExpression(LocalContext context, Expression expression)
    {
        if (expression == null)
            throw null;
        ParseElement content = expression.content;
        ReturnBehavior returnBehavior;
        switch (content.getElementType()) {
            case Addition.TYPE:
                returnBehavior = lexiconizeAddition(context, expression);
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
            case PreIncrement.TYPE:
                returnBehavior = lexiconizePreIncrement(context, (PreIncrement)content);
                break;
            case PreDecrement.TYPE:
                returnBehavior = lexiconizePreDecrement(context, (PreDecrement)content);
                break;
            case PostIncrement.TYPE:
                returnBehavior = lexiconizePostIncrement(context, (PostIncrement)content);
                break;
            case PostDecrement.TYPE:
                returnBehavior = lexiconizePostDecrement(context, (PostDecrement)content);
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
            case ShortCircuitAnd.TYPE:
                returnBehavior = lexiconizeShortCircuitAnd(context, (ShortCircuitAnd)content);
                break;
            case ShortCircuitOr.TYPE:
                returnBehavior = lexiconizeShortCircuitOr(context, (ShortCircuitOr)content);
                break;
            case Negation.TYPE:
                returnBehavior = lexiconizeNegation(context, (Negation)content);
                break;
            case BooleanNot.TYPE:
                returnBehavior = lexiconizeBooleanNot(context, (BooleanNot)content);
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
            case FloatLiteral.TYPE:
                returnBehavior = lexiconizeFloatLiteral(context, (FloatLiteral)content);
                break;
            case DoubleLiteral.TYPE:
                returnBehavior = lexiconizeDoubleLiteral(context, (DoubleLiteral)content);
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
            case WhileLoop.TYPE:
                returnBehavior = lexiconizeWhileLoop(context, (WhileLoop)content);
                break;
            case FunctionInvocation.TYPE:
                returnBehavior = lexiconizeFunctionInvocation(context, (FunctionInvocation)content);
                break;
            case ConstructorInvocation.TYPE:
                returnBehavior = lexiconizeConstructorInvocation(context, (ConstructorInvocation)content);
                break;
            case DereferenceMethod.TYPE:
                switch (disambuateDereferenceMethod(context, expression)) {
                    case DereferenceMethod.TYPE:
                        returnBehavior = lexiconizeDereferenceMethod(context, (DereferenceMethod)content);
                        break;
                    case StaticFunctionInvocation.TYPE:
                        returnBehavior = lexiconizeStaticFunctionInvocation(context, (StaticFunctionInvocation)expression.content);
                        break;
                    case -1:
                        returnBehavior = ReturnBehavior.UNKNOWN;
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
                    case -1:
                        returnBehavior = ReturnBehavior.UNKNOWN;
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
            case TypeCast.TYPE:
                returnBehavior = lexiconizeTypeCast(context, expression);
                break;
            case NullExpression.TYPE:
                returnBehavior = lexiconizeNullExpression(context, (NullExpression)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeNullExpression(LocalContext context, NullExpression nullExpression)
    {
        context.modifyStack(1);
        return ReturnBehavior.NULL;
    }

    private ReturnBehavior lexiconizeShortCircuitAnd(LocalContext context, ShortCircuitAnd shortCircuitAnd)
    {
        return lexiconizeShortCircuitOperator(context, shortCircuitAnd);
    }

    private ReturnBehavior lexiconizeShortCircuitOr(LocalContext context, ShortCircuitOr shortCircuitOr)
    {
        return lexiconizeShortCircuitOperator(context, shortCircuitOr);
    }

    private ReturnBehavior lexiconizeShortCircuitOperator(LocalContext context, ShortCircuitOperator shortCircuitOperator)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, shortCircuitOperator.expression1);
        if (returnBehavior1.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(shortCircuitOperator.expression1));
        context.modifyStack(-returnBehavior1.type.getSize());
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, shortCircuitOperator.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(shortCircuitOperator.expression2));
        context.modifyStack(-returnBehavior2.type.getSize());
        shortCircuitOperator.label1 = context.nextLabel();
        shortCircuitOperator.label2 = context.nextLabel();
        context.modifyStack(1);
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior lexiconizeBooleanNot(LocalContext context, BooleanNot booleanNot)
    {
        ReturnBehavior returnBehavior = lexiconizeExpression(context, booleanNot.expression);
        if (returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(booleanNot.expression));
        context.modifyStack(-returnBehavior.type.getSize() + 1);
        booleanNot.label1 = context.nextLabel();
        booleanNot.label2 = context.nextLabel();
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior lexiconizeNegation(LocalContext context, Negation negation)
    {
        ReturnBehavior returnBehavior = lexiconizeExpression(context, negation.expression);
        if (returnBehavior.type != RuntimeType.INT) {
            errors.add(LexicalException.mustBeInt(negation.expression));
            context.modifyStack(RuntimeType.INT.getSize() - returnBehavior.type.getSize());
        }
        return ReturnBehavior.INT;
    }

    private ReturnBehavior lexiconizeTypeCast(LocalContext context, Expression expression)
    {
        TypeCast typeCast = (TypeCast)expression.content;
        // toType
        resolveType(typeCast.typeId, true);
        Type toType = typeCast.typeId.type;
        if (toType == RuntimeType.VOID) {
            errors.add(new LexicalException(typeCast.typeId, "can't cast to void."));
            toType = null;
        }
        // fromType
        ReturnBehavior returnBehavior = lexiconizeExpression(context, typeCast.expression);
        Type fromType = returnBehavior.type;
        if (fromType == RuntimeType.VOID) {
            errors.add(new LexicalException(typeCast, "can't cast from void."));
            fromType = null;
        }
        // error recovery
        if (fromType == null) {
            if (toType == null)
                return new ReturnBehavior(RuntimeType.getType(Object.class));
            return new ReturnBehavior(toType);
        } else if (toType == null)
            return new ReturnBehavior(fromType);

        // identity
        if (fromType == toType)
            return new ReturnBehavior(toType);
        context.modifyStack(toType.getSize() - returnBehavior.type.getSize());

        // primitive vs reference
        if (fromType.isPrimitive() != toType.isPrimitive()) {
            errors.add(new LexicalException(typeCast.typeId, "can't cast between primitives and non-primitives"));
            return new ReturnBehavior(toType);
        }
        if (toType.isPrimitive()) {
            // primitive
            if (fromType == RuntimeType.BOOLEAN || toType == RuntimeType.BOOLEAN) {
                errors.add(LexicalException.cantCast(typeCast.typeId, fromType, toType));
                return new ReturnBehavior(toType);
            }
            switch (RuntimeType.getPrimitiveConversionType(fromType, toType)) {
                case -1:
                    // narrowing
                    expression.content = PrimitiveConversion.getConversion(fromType, toType, typeCast.expression);
                    if (expression.content == null) {
                        errors.add(LexicalException.cantCast(typeCast, fromType, toType));
                        expression.content = typeCast;
                    }
                    break;
                case 0:
                    throw null; // identity casts are dealt with earlier
                case 1:
                    // inline widening cast
                    expression.content = typeCast.expression.content;
                    break;
                default:
                    throw null;
            }
            return new ReturnBehavior(toType);
        } else {
            // reference
            if (fromType.isInstanceOf(toType)) {
                // inline widening cast
                expression.content = typeCast.expression.content;
                return new ReturnBehavior(toType);
            }
            expression.content = new ReferenceConversion(typeCast.expression, toType);
            return new ReturnBehavior(toType);
        }
    }

    private ReturnBehavior lexiconizeWhileLoop(LocalContext context, WhileLoop whileLoop)
    {
        whileLoop.continueToLabel = context.nextLabel();
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, whileLoop.expression1);
        if (returnBehavior1.type != RuntimeType.BOOLEAN)
            errors.add(LexicalException.mustBeBoolean(whileLoop.expression1));
        context.modifyStack(-returnBehavior1.type.getSize());

        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, whileLoop.expression2);
        if (returnBehavior2.type != RuntimeType.VOID) {
            errors.add(LexicalException.mustBeVoid(whileLoop.expression2));
            context.modifyStack(-returnBehavior2.type.getSize());
        }

        whileLoop.breakToLabel = context.nextLabel();
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeConstructorInvocation(LocalContext context, ConstructorInvocation constructorInvocation)
    {
        TypeId typeId = TypeId.fromId(constructorInvocation.functionInvocation.id);
        resolveType(typeId, true);
        context.modifyStack(2); // new; dup;
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, constructorInvocation.functionInvocation.arguments);
        constructorInvocation.constructor = resolveConstructor(typeId.type, argumentSignature);
        if (constructorInvocation.constructor == null)
            errors.add(new LexicalException(constructorInvocation, "can't resolve this constructor"));
        context.modifyStack(-(1 + getArgumentSignatureSize(argumentSignature)));
        return new ReturnBehavior(typeId.type);
    }

    private ReturnBehavior lexiconizePreIncrement(LocalContext context, PreIncrement preIncrement)
    {
        return lexiconizeIncrementDecrement(context, preIncrement);
    }
    private ReturnBehavior lexiconizePreDecrement(LocalContext context, PreDecrement preDecrement)
    {
        return lexiconizeIncrementDecrement(context, preDecrement);
    }
    private ReturnBehavior lexiconizePostIncrement(LocalContext context, PostIncrement postIncrement)
    {
        return lexiconizeIncrementDecrement(context, postIncrement);
    }
    private ReturnBehavior lexiconizePostDecrement(LocalContext context, PostDecrement postDecrement)
    {
        return lexiconizeIncrementDecrement(context, postDecrement);
    }
    private ReturnBehavior lexiconizeIncrementDecrement(LocalContext context, IncrementDecrement incrementDecrement)
    {
        if (incrementDecrement.expression.content.getElementType() != Id.TYPE)
            errors.add(LexicalException.mustBeVariable(incrementDecrement.expression.content));
        else {
            incrementDecrement.id = (Id)incrementDecrement.expression.content;
            lexiconizeId(context, incrementDecrement.id);
            if (incrementDecrement.id.variable.type != RuntimeType.INT)
                errors.add(LexicalException.variableMustBeInt(incrementDecrement.id));
        }
        return ReturnBehavior.INT;
    }

    private ReturnBehavior lexiconizeForLoop(LocalContext context, ForLoop forLoop)
    {
        LocalContext innerContext = new LocalContext(context);
        ReturnBehavior returnBehavior1 = lexiconizeExpression(innerContext, forLoop.expression1);
        if (returnBehavior1.type != RuntimeType.VOID)
            errors.add(LexicalException.mustBeVoid(forLoop.expression1));

        forLoop.continueToLabel = innerContext.nextLabel();
        ReturnBehavior returnBehavior3 = lexiconizeExpression(innerContext, forLoop.expression3);
        innerContext.modifyStack(-returnBehavior3.type.getSize());

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
        if (returnBehavior1.type.getType() != ArrayType.TYPE)
            errors.add(new LexicalException(arrayDereference, "Can't dereference this thing like an array"));
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, arrayDereference.expression2);
        if (returnBehavior2.type != RuntimeType.INT)
            errors.add(LexicalException.mustBeInt(arrayDereference.expression2));

        Type scalarType = ((ArrayType)returnBehavior1.type).scalarType;
        context.modifyStack(-2 + scalarType.getSize());

        return new ReturnBehavior(scalarType);
    }

    private ReturnBehavior lexiconizeStaticDereferenceField(LocalContext context, StaticDereferenceField staticDereferenceField)
    {
        // lexiconization already done for typeId
        staticDereferenceField.field = resolveField(staticDereferenceField.typeId.type, staticDereferenceField.id);
        if (staticDereferenceField.field == null)
            errors.add(LexicalException.cantResolveField(staticDereferenceField.typeId.type, staticDereferenceField.id));
        Type type = staticDereferenceField.field.returnType;
        context.modifyStack(type.getSize());
        return new ReturnBehavior(type);
    }

    private ReturnBehavior lexiconizeStaticFunctionInvocation(LocalContext context, StaticFunctionInvocation staticFunctionInvocation)
    {
        // lexiconization already done for typeId
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, staticFunctionInvocation.functionInvocation.arguments);
        staticFunctionInvocation.functionInvocation.method = resolveMethod(staticFunctionInvocation.typeId.type, staticFunctionInvocation.functionInvocation, argumentSignature);
        context.modifyStack(-getArgumentSignatureSize(argumentSignature));
        Type returnType = staticFunctionInvocation.functionInvocation.method.returnType;
        context.modifyStack(returnType.getSize());
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior lexiconizeTryCatch(LocalContext context, TryCatch tryCatch)
    {
        ReturnBehavior tryPartReturnBehavior = lexiconizeTryPart(context, tryCatch.tryPart);
        context.modifyStack(-tryPartReturnBehavior.type.getSize()); // take this off for now

        ReturnBehavior catchPartReturnBehavior = lexiconizeCatchPart(context, tryCatch.catchPart);
        tryCatch.endLabel = context.nextLabel();
        context.modifyStack(-catchPartReturnBehavior.type.getSize()); // take this off for now

        if (tryPartReturnBehavior.type != catchPartReturnBehavior.type)
            errors.add(new LexicalException(tryCatch, "return types must match"));
        context.modifyStack(tryPartReturnBehavior.type.getSize()); // and now put one of them back on
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
        nestedContext.modifyStack(1); // exception object pushed onto the stack
        nestedContext.modifyStack(-1); // store exception object in local variable
        ReturnBehavior returnBehavior = lexiconizeExpression(nestedContext, catchBody.expression);
        return new ReturnBehavior(returnBehavior.type);
    }

    private int disambuateDereferenceField(LocalContext context, Expression expression)
    {
        DereferenceField dereferenceField = (DereferenceField)expression.content;
        if (dereferenceField.expression.content.getElementType() != Id.TYPE)
            return DereferenceField.TYPE;
        Id id = (Id)dereferenceField.expression.content;
        LocalVariable localVariable = resolveId(context, id);
        if (localVariable != null)
            return DereferenceField.TYPE;
        TypeId typeId = TypeId.fromId(id);
        if (resolveType(typeId, false)) {
            // convert to StaticDereferenceField
            expression.content = new StaticDereferenceField(typeId, dereferenceField.id);
            return StaticDereferenceField.TYPE;
        }
        errors.add(LexicalException.cantResolveLocalVariable(id));
        return -1;
    }

    private ReturnBehavior lexiconizeDereferenceField(LocalContext context, DereferenceField dereferenceField)
    {
        Type type = lexiconizeExpression(context, dereferenceField.expression).type;
        Field field = resolveField(type, dereferenceField.id);
        if (field == null)
            errors.add(LexicalException.cantResolveField(type, dereferenceField.id));
        context.modifyStack(-type.getSize() + field.returnType.getSize());
        return new ReturnBehavior(field.returnType);
    }

    private int disambuateDereferenceMethod(LocalContext context, Expression expression)
    {
        DereferenceMethod dereferenceMethod = (DereferenceMethod)expression.content;
        if (dereferenceMethod.expression.content.getElementType() != Id.TYPE)
            return DereferenceMethod.TYPE;
        Id id = (Id)dereferenceMethod.expression.content;
        LocalVariable localVariable = resolveId(context, id);
        if (localVariable != null)
            return DereferenceMethod.TYPE;
        TypeId typeId = TypeId.fromId(id);
        if (resolveType(typeId, false)) {
            // convert to StaticFunctionInvocation
            expression.content = new StaticFunctionInvocation(typeId, dereferenceMethod.functionInvocation);
            return StaticFunctionInvocation.TYPE;
        }
        errors.add(LexicalException.cantResolveLocalVariable(id));
        return -1;
    }

    private ReturnBehavior lexiconizeDereferenceMethod(LocalContext context, DereferenceMethod dereferenceMethod)
    {
        ReturnBehavior expressionReturnBehavior = lexiconizeExpression(context, dereferenceMethod.expression);
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, dereferenceMethod.functionInvocation.arguments);
        dereferenceMethod.functionInvocation.method = resolveMethod(expressionReturnBehavior.type, dereferenceMethod.functionInvocation, argumentSignature);
        context.modifyStack(-(1 + getArgumentSignatureSize(argumentSignature)));
        context.modifyStack(dereferenceMethod.functionInvocation.method.returnType.getSize());
        return new ReturnBehavior(dereferenceMethod.functionInvocation.method.returnType);
    }

    private ReturnBehavior lexiconizeFunctionInvocation(LocalContext context, FunctionInvocation functionInvocation)
    {
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, functionInvocation.arguments);
        functionInvocation.method = resolveMethod(context.getClassContext(), functionInvocation, argumentSignature);
        context.modifyStack(-getArgumentSignatureSize(argumentSignature));
        context.modifyStack(functionInvocation.method.returnType.getSize());
        return new ReturnBehavior(functionInvocation.method.returnType);
    }

    private ReturnBehavior[] lexiconizeArguments(LocalContext context, Arguments arguments)
    {
        deleteNulls(arguments);
        ReturnBehavior[] rtnArr = new ReturnBehavior[arguments.elements.size()];
        int i = 0;
        for (Expression element : arguments.elements) {
            if (element.returnBehavior == null)
                rtnArr[i++] = lexiconizeExpression(context, element);
            else {
                // the conversion from addition to string concatenation has already lexiconized some arguments
                rtnArr[i++] = element.returnBehavior;
                context.modifyStack(element.returnBehavior.type.getSize());
            }
        }
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
        context.modifyStack(-ifThenElse.expression2.returnBehavior.type.getSize()); // for now, take this off

        ifThenElse.label2 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression3);
        context.modifyStack(-ifThenElse.expression3.returnBehavior.type.getSize()); // for now, take this off

        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new LexicalException(ifThenElse, "return types must match"));
        context.modifyStack(ifThenElse.expression2.returnBehavior.type.getSize()); // and now put it back
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
        context.modifyStack(returnBehavior.type.getSize()); // "dup" for multiassignment. This is likely to change in the future.
        context.modifyStack(-returnBehavior.type.getSize());
        return new ReturnBehavior(returnBehavior.type);
    }

    private ReturnBehavior lexiconizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        resolveType(variableDeclaration.typeId, true);
        if (variableDeclaration.typeId.type == RuntimeType.VOID)
            errors.add(new LexicalException(variableDeclaration, "You can't have a void variable."));
        context.addLocalVariable(variableDeclaration.id, variableDeclaration.typeId.type, errors);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeId(LocalContext context, Id id)
    {
        id.variable = resolveId(context, id);
        if (id.variable == null) {
            errors.add(LexicalException.cantResolveLocalVariable(id));
            return ReturnBehavior.UNKNOWN;
        }
        context.modifyStack(id.variable.type.getSize());
        return new ReturnBehavior(id.variable.type);
    }

    private ReturnBehavior lexiconizeVariableCreation(LocalContext context, VariableCreation variableCreation)
    {
        lexiconizeVariableDeclaration(context, variableCreation.variableDeclaration);
        ReturnBehavior returnBehavior = lexiconizeExpression(context, variableCreation.expression);
        if (variableCreation.variableDeclaration.typeId.type != returnBehavior.type)
            errors.add(LexicalException.cantCast(variableCreation.expression, returnBehavior.type, variableCreation.variableDeclaration.typeId.type));
        context.modifyStack(-returnBehavior.type.getSize());
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
            context.modifyStack(-returnType.getSize()); // assume the block does not return this value
        }
        if (blockContents.forceVoid)
            returnType = RuntimeType.VOID;
        context.modifyStack(returnType.getSize()); // turns out the block does return a value
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
    private ReturnBehavior lexiconizeFloatLiteral(LocalContext context, FloatLiteral floatLiteral)
    {
        context.modifyStack(1);
        return ReturnBehavior.FLOAT;
    }
    private ReturnBehavior lexiconizeDoubleLiteral(LocalContext context, DoubleLiteral doubleLiteral)
    {
        context.modifyStack(2);
        return ReturnBehavior.DOUBLE;
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

    private ReturnBehavior lexiconizeAddition(LocalContext context, Expression expression)
    {
        Addition addition = (Addition)expression.content;
        Type returnType1 = lexiconizeExpression(context, addition.expression1).type;
        Type returnType2 = lexiconizeExpression(context, addition.expression2).type;
        context.modifyStack(-returnType1.getSize() - returnType2.getSize());
        if (returnType1 == RuntimeType.STRING || returnType2 == RuntimeType.STRING) {
            // convert to string concatenation
            // a + b
            // becomes
            // String.valueOf(a).concat(String.valueOf(b))
            Expression string1 = stringValueOf(addition.expression1);
            Expression string2 = stringValueOf(addition.expression2);
            expression.content = new DereferenceMethod(string1, new FunctionInvocation(new Id("concat"), new Arguments(Collections.nCopies(1, string2))));
            return lexiconizeExpression(context, expression);
        }
        // TODO: don't assume types are int
        context.modifyStack(1);
        return new ReturnBehavior(RuntimeType.INT);
    }
    private Expression stringValueOf(Expression expression)
    {
        return new Expression(new DereferenceMethod(new Expression(new Id("String")), new FunctionInvocation(new Id("valueOf"), new Arguments(Collections.nCopies(1, expression)))));
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
        ReturnBehavior returnBehavior = lexiconizeOperator(context, operator, null, false);
        if (returnBehavior.type != RuntimeType.INT)
            errors.add(new LexicalException(operator, "Expression must be of type int."));
        return returnBehavior;
    }
    private ReturnBehavior lexiconizeLessThan(LocalContext context, LessThan lessThan)
    {
        return lexiconizeComparisonOperator(context, lessThan, false);
    }
    private ReturnBehavior lexiconizeGreaterThan(LocalContext context, GreaterThan greaterThan)
    {
        return lexiconizeComparisonOperator(context, greaterThan, false);
    }
    private ReturnBehavior lexiconizeLessThanOrEqual(LocalContext context, LessThanOrEqual lessThanOrEqual)
    {
        return lexiconizeComparisonOperator(context, lessThanOrEqual, false);
    }
    private ReturnBehavior lexiconizeGreaterThanOrEqual(LocalContext context, GreaterThanOrEqual greaterThanOrEqual)
    {
        return lexiconizeComparisonOperator(context, greaterThanOrEqual, false);
    }
    private ReturnBehavior lexiconizeEquality(LocalContext context, Equality equality)
    {
        return lexiconizeComparisonOperator(context, equality, true);
    }
    private ReturnBehavior lexiconizeInequality(LocalContext context, Inequality inequality)
    {
        return lexiconizeComparisonOperator(context, inequality, true);
    }
    private ReturnBehavior lexiconizeComparisonOperator(LocalContext context, ComparisonOperator operator, boolean allowReferenceOperands)
    {
        operator.label1 = context.nextLabel();
        operator.label2 = context.nextLabel();
        return lexiconizeOperator(context, operator, RuntimeType.BOOLEAN, allowReferenceOperands);
    }
    private ReturnBehavior lexiconizeOperator(LocalContext context, BinaryOperatorElement operator, Type returnType, boolean allowReferenceOperands)
    {
        Type returnType1 = lexiconizeExpression(context, operator.expression1).type;
        Type returnType2 = lexiconizeExpression(context, operator.expression2).type;
        if (allowReferenceOperands) {
            if (!(returnType1.isInstanceOf(returnType2) || returnType2.isInstanceOf(returnType1)))
                errors.add(new LexicalException(operator, "operand types are incompatible."));
        } else {
            if (!returnType1.isPrimitive())
                errors.add(new LexicalException(operator.expression1, "operand can't be a reference type."));
            if (!returnType2.isPrimitive())
                errors.add(new LexicalException(operator.expression2, "operand can't be a reference type."));
        }
        returnType = returnType != null ? returnType : returnType1;
        context.modifyStack(-returnType1.getSize() - returnType2.getSize() + returnType.getSize());
        return new ReturnBehavior(returnType);
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

    private boolean resolveType(TypeId typeId, boolean errorOnFailure)
    {
        boolean failure = false;
        Type type = importedTypes.get(typeId.scalarType.toString());
        if (type == null) {
            failure = true;
            type = UnknownType.INSTANCE;
        }
        int arrayOrder = typeId.arrayDimensions.elements.size();
        while (arrayOrder-- > 0)
            type = ArrayType.getType(type);
        typeId.type = type;
        if (failure && errorOnFailure)
            errors.add(LexicalException.cantResolveType(typeId));
        return !failure;
    }

    private Method resolveMethod(Type type, FunctionInvocation functionInvocation, ReturnBehavior[] argumentSignature)
    {
        Method method = type.resolveMethod(functionInvocation.id.name, getArgumentTypes(argumentSignature));
        if (method == null) {
            errors.add(LexicalException.cantResolveMethod(type, functionInvocation.id, argumentSignature));
            return Method.UNKNOWN;
        }
        return method;
    }

    private Constructor resolveConstructor(Type type, ReturnBehavior[] argumentSignature)
    {
        return type.resolveConstructor(getArgumentTypes(argumentSignature));
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
    private int getArgumentSignatureSize(ReturnBehavior[] argumentSignature)
    {
        int sum = 0;
        for (ReturnBehavior returnBehavior : argumentSignature)
            sum += returnBehavior.type.getSize();
        return sum;
    }
    private static void deleteNulls(ListElement<?> listElement)
    {
        Iterator<?> iterator = listElement.elements.iterator();
        while (iterator.hasNext())
            if (iterator.next() == null)
                iterator.remove();
    }
}
