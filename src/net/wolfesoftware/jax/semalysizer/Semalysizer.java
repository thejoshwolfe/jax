package net.wolfesoftware.jax.semalysizer;

import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.codegen.Instructions;
import net.wolfesoftware.jax.parser.Parsing;
import net.wolfesoftware.jax.tokenizer.Lang;
import net.wolfesoftware.jax.util.Util;

public class Semalysizer
{
    public static Semalysization semalysize(Parsing parsing, String filePath)
    {
        return new Semalysizer(parsing, filePath).semalysizeRoot();
    }

    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        RuntimeType.initPrimitives(importedTypes);
        RuntimeType.initJavaLang(importedTypes);
    }
    private final Root root;
    private final String filePath;
    private final ArrayList<SemalyticalException> errors = new ArrayList<SemalyticalException>();

    private Semalysizer(Parsing parsing, String filePath)
    {
        root = parsing.root;
        this.filePath = filePath;
    }

    private Semalysization semalysizeRoot()
    {
        boolean broken = true;
        try {
            semalysizeCompilationUnit(root.content);
            broken = false;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (AssertionError e) {
            e.printStackTrace();
        }
        if (broken && errors.isEmpty())
            errors.add(new SemalyticalException(new Id(""), "Things are broken"));
        return new Semalysization(root, errors);
    }

    private void semalysizeCompilationUnit(CompilationUnit compilationUnit)
    {
        semalysizeImports(compilationUnit.imports);
        semalysizeClassDeclaration(compilationUnit.classDeclaration);
    }

    private void semalysizeImports(Imports imports)
    {
        for (ImportStatement importStatement : imports.elements)
            semalysizeImportStatement(importStatement);
    }

    private void semalysizeImportStatement(ImportStatement importStatement)
    {
        ParseElement content = importStatement.content;
        switch (importStatement.content.getElementType()) {
            case ImportStar.TYPE:
                semalysizeImportStar((ImportStar)content);
                break;
            case ImportClass.TYPE:
                semalysizeImportClass((ImportClass)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private void semalysizeImportStar(ImportStar importStar)
    {
        deleteNulls(importStar.qualifiedName);
        BuiltinPackageLister.importPackageStar(importStar.qualifiedName, importedTypes, errors);
    }

    private void semalysizeImportClass(ImportClass importClass)
    {
        resolveQualifiedName(importClass.qualifiedName);
    }

    private void semalysizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        String classNameFromFile = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
        if (!classDeclaration.id.name.equals(classNameFromFile))
            errors.add(new SemalyticalException(classDeclaration.id, "Class name does not match file name \"" + classNameFromFile + "\"."));

        semalysizeClassModifiers(classDeclaration.classModifiers);
        classDeclaration.localType = new LocalType(classNameFromFile, classDeclaration.id.name);
        importedTypes.put(classNameFromFile, classDeclaration.localType);
        semalysizeClassBody(classDeclaration.localType, classDeclaration.classBody);
    }

    private void semalysizeClassModifiers(ClassModifiers classModifiers)
    {
        HashSet<ClassModifier> presentModifiers = new HashSet<ClassModifier>();
        for (ClassModifier classModifier : classModifiers.elements) {
            if (presentModifiers.contains(classModifier))
                errors.add(new SemalyticalException(classModifier, "Please say that it's \"" + classModifier + "\" at most once."));
            presentModifiers.add(classModifier);
        }
        for (ClassModifier classModifier : presentModifiers)
            classModifiers.bitmask |= classModifier.bitmask;
    }

    private void semalysizeClassBody(LocalType context, ClassBody classBody)
    {
        deleteNulls(classBody);

        for (ClassMember classMember : classBody.elements)
            preSemalysizeClassMemeber(context, classMember);

        if (context.constructors.isEmpty()) {
            ClassMember classMember = context.makeDefaultConstructor(classBody);
            classBody.elements.add(classMember);
            preSemalysizeClassMemeber(context, classMember);
        }

        for (ClassMember classMember : classBody.elements)
            semalysizeClassMemeber(context, classMember);
    }

    private void preSemalysizeClassMemeber(LocalType context, ClassMember classMember)
    {
        if (classMember == null)
            return;
        ParseElement content = classMember.content;
        switch (content.getElementType()) {
            case FunctionDefinition.TYPE:
                preSemalysizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            case ConstructorDefinition.TYPE:
                preSemalysizeConstructorDefinition(context, (ConstructorDefinition)content);
                break;
            default:
                throw new RuntimeException("TODO: implement " + content.getClass().getName());
        }
    }

    private void preSemalysizeConstructorDefinition(LocalType context, ConstructorDefinition constructorDefinition)
    {
        resolveType(constructorDefinition.typeId, true);
        if (constructorDefinition.typeId.type != context)
            errors.add(new SemalyticalException(constructorDefinition.typeId, "you can't have a constructor for type \"" + constructorDefinition.typeId.type + "\" in this class."));
        constructorDefinition.context = new RootLocalContext(context, false);
        Type[] arguemntSignature = semalysizeArgumentDeclarations(constructorDefinition.context, constructorDefinition.argumentDeclarations);
        constructorDefinition.constructor = new Constructor(context, arguemntSignature);
        context.addConstructor(constructorDefinition.constructor);
    }

    private void preSemalysizeFunctionDefinition(LocalType context, FunctionDefinition functionDefinition)
    {
        resolveType(functionDefinition.typeId, true);
        functionDefinition.context = new RootLocalContext(context, true);
        Type[] arguemntSignature = semalysizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.method = new Method(context, functionDefinition.typeId.type, functionDefinition.id.name, arguemntSignature, true);
        context.addMethod(functionDefinition.method);
    }

    private Type[] semalysizeArgumentDeclarations(LocalContext context, ArgumentDeclarations argumentDeclarations)
    {
        Type[] argumentSignature = new Type[argumentDeclarations.elements.size()];
        int i = 0;
        for (VariableDeclaration variableDeclaration : argumentDeclarations.elements) {
            semalysizeVariableDeclaration(context, variableDeclaration);
            argumentSignature[i++] = variableDeclaration.typeId.type;
        }
        return argumentSignature;
    }

    private void semalysizeClassMemeber(Type context, ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType()) {
            case FunctionDefinition.TYPE:
                semalysizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            case ConstructorDefinition.TYPE:
                semalysizeConstructorDefinition(context, (ConstructorDefinition)content);
                break;
            default:
                throw new RuntimeException("TODO: implement " + content.getClass());
        }
    }

    private void semalysizeConstructorDefinition(Type context, ConstructorDefinition constructorDefinition)
    {
        ConstructorRedirect constructorRedirect = new ConstructorRedirect(Lang.KEYWORD_SUPER, new Arguments(new LinkedList<Expression>()));
        constructorDefinition.expression = new Expression(new Block(new BlockContents(Arrays.asList(new Expression(constructorRedirect), constructorDefinition.expression))));
        constructorDefinition.returnBehavior = semalysizeExpression(constructorDefinition.context, constructorDefinition.expression);
        if (constructorDefinition.returnBehavior.type != RuntimeType.VOID)
            errors.add(SemalyticalException.mustBeVoid(constructorDefinition.expression));
        Util._assert(constructorDefinition.context.stackSize == 0);
    }

    private void semalysizeFunctionDefinition(Type context, FunctionDefinition functionDefinition)
    {
        semalysizeExpression(functionDefinition.context, functionDefinition.expression);
        implicitCast(functionDefinition.context, functionDefinition.expression, functionDefinition.method.returnType);
        functionDefinition.returnBehavior = functionDefinition.expression.returnBehavior;
        if (functionDefinition.method.returnType != functionDefinition.returnBehavior.type)
            errors.add(SemalyticalException.cantCast(functionDefinition.expression, functionDefinition.returnBehavior.type, functionDefinition.method.returnType));
        Util._assert(functionDefinition.context.stackSize == 0);
    }

    private ReturnBehavior semalysizeExpression(LocalContext context, Expression expression)
    {
        if (expression == null)
            throw null;
        ParseElement content = expression.content;
        ReturnBehavior returnBehavior;
        switch (content.getElementType()) {
            case Addition.TYPE:
                returnBehavior = semalysizeAddition(context, expression);
                break;
            case Subtraction.TYPE:
                returnBehavior = semalysizeSubtraction(context, (Subtraction)content);
                break;
            case Multiplication.TYPE:
                returnBehavior = semalysizeMultiplication(context, (Multiplication)content);
                break;
            case Division.TYPE:
                returnBehavior = semalysizeDivision(context, (Division)content);
                break;
            case PreIncrement.TYPE:
                returnBehavior = semalysizePreIncrement(context, (PreIncrement)content);
                break;
            case PreDecrement.TYPE:
                returnBehavior = semalysizePreDecrement(context, (PreDecrement)content);
                break;
            case PostIncrement.TYPE:
                returnBehavior = semalysizePostIncrement(context, (PostIncrement)content);
                break;
            case PostDecrement.TYPE:
                returnBehavior = semalysizePostDecrement(context, (PostDecrement)content);
                break;
            case LessThan.TYPE:
                returnBehavior = semalysizeLessThan(context, (LessThan)content);
                break;
            case GreaterThan.TYPE:
                returnBehavior = semalysizeGreaterThan(context, (GreaterThan)content);
                break;
            case LessThanOrEqual.TYPE:
                returnBehavior = semalysizeLessThanOrEqual(context, (LessThanOrEqual)content);
                break;
            case GreaterThanOrEqual.TYPE:
                returnBehavior = semalysizeGreaterThanOrEqual(context, (GreaterThanOrEqual)content);
                break;
            case Equality.TYPE:
                returnBehavior = semalysizeEquality(context, (Equality)content);
                break;
            case Inequality.TYPE:
                returnBehavior = semalysizeInequality(context, (Inequality)content);
                break;
            case ShortCircuitAnd.TYPE:
                returnBehavior = semalysizeShortCircuitAnd(context, (ShortCircuitAnd)content);
                break;
            case ShortCircuitOr.TYPE:
                returnBehavior = semalysizeShortCircuitOr(context, (ShortCircuitOr)content);
                break;
            case Negation.TYPE:
                returnBehavior = semalysizeNegation(context, (Negation)content);
                break;
            case BooleanNot.TYPE:
                returnBehavior = semalysizeBooleanNot(context, (BooleanNot)content);
                break;
            case Id.TYPE:
                returnBehavior = semalysizeId(context, (Id)content);
                break;
            case Block.TYPE:
                returnBehavior = semalysizeBlock(context, (Block)content);
                break;
            case IntLiteral.TYPE:
                returnBehavior = semalysizeIntLiteral(context, (IntLiteral)content);
                break;
            case LongLiteral.TYPE:
                returnBehavior = semalysizeLongLiteral(context, (LongLiteral)content);
                break;
            case FloatLiteral.TYPE:
                returnBehavior = semalysizeFloatLiteral(context, (FloatLiteral)content);
                break;
            case DoubleLiteral.TYPE:
                returnBehavior = semalysizeDoubleLiteral(context, (DoubleLiteral)content);
                break;
            case BooleanLiteral.TYPE:
                returnBehavior = semalysizeBooleanLiteral(context, (BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                returnBehavior = semalysizeStringLiteral(context, (StringLiteral)content);
                break;
            case Quantity.TYPE:
                returnBehavior = semalysizeQuantity(context, (Quantity)content);
                break;
            case VariableCreation.TYPE:
                returnBehavior = semalysizeVariableCreation(context, (VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                returnBehavior = semalysizeVariableDeclaration(context, (VariableDeclaration)content);
                break;
            case Assignment.TYPE:
                returnBehavior = semalysizeAssignment(context, (Assignment)content);
                break;
            case IfThenElse.TYPE:
                returnBehavior = semalysizeIfThenElse(context, (IfThenElse)content);
                break;
            case IfThen.TYPE:
                returnBehavior = semalysizeIfThen(context, (IfThen)content);
                break;
            case ForLoop.TYPE:
                returnBehavior = semalysizeForLoop(context, (ForLoop)content);
                break;
            case WhileLoop.TYPE:
                returnBehavior = semalysizeWhileLoop(context, (WhileLoop)content);
                break;
            case FunctionInvocation.TYPE:
                returnBehavior = semalysizeFunctionInvocation(context, (FunctionInvocation)content);
                break;
            case ConstructorInvocation.TYPE:
                returnBehavior = semalysizeConstructorInvocation(context, (ConstructorInvocation)content);
                break;
            case ConstructorRedirect.TYPE:
                returnBehavior = semalysizeConstructorRedirect(context, (ConstructorRedirect)content);
                break;
            case DereferenceMethod.TYPE:
                switch (disambuateDereferenceMethod(context, expression)) {
                    case DereferenceMethod.TYPE:
                        returnBehavior = semalysizeDereferenceMethod(context, (DereferenceMethod)content);
                        break;
                    case StaticFunctionInvocation.TYPE:
                        returnBehavior = semalysizeStaticFunctionInvocation(context, (StaticFunctionInvocation)expression.content);
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
                        returnBehavior = semalysizeDereferenceField(context, (DereferenceField)content);
                        break;
                    case StaticDereferenceField.TYPE:
                        returnBehavior = semalysizeStaticDereferenceField(context, (StaticDereferenceField)expression.content);
                        break;
                    case -1:
                        returnBehavior = ReturnBehavior.UNKNOWN;
                        break;
                    default:
                        throw new RuntimeException();
                }
                break;
            case ArrayDereference.TYPE:
                returnBehavior = semalysizeArrayDereference(context, (ArrayDereference)content);
                break;
            case TryCatch.TYPE:
                returnBehavior = semalysizeTryCatch(context, (TryCatch)content);
                break;
            case TypeCast.TYPE:
                returnBehavior = semalysizeTypeCast(context, expression);
                break;
            case NullExpression.TYPE:
                returnBehavior = semalysizeNullExpression(context, (NullExpression)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior semalysizeConstructorRedirect(LocalContext context, ConstructorRedirect constructorRedirect)
    {
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, constructorRedirect.arguments);
        Type type;
        if (constructorRedirect.thisOrSuper == Lang.KEYWORD_THIS)
            type = context.getClassContext();
        else if (constructorRedirect.thisOrSuper == Lang.KEYWORD_SUPER)
            type = context.getClassContext().getParent();
        else
            throw null;
        constructorRedirect.constructor = resolveConstructor(type, argumentSignature);
        implicitCastArguments(context, constructorRedirect.arguments, constructorRedirect.constructor.argumentSignature);
        if (constructorRedirect.constructor == null)
            errors.add(new SemalyticalException(constructorRedirect, "can't resolve this constructor"));
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeNullExpression(LocalContext context, NullExpression nullExpression)
    {
        return ReturnBehavior.NULL;
    }

    private ReturnBehavior semalysizeShortCircuitAnd(LocalContext context, ShortCircuitAnd shortCircuitAnd)
    {
        return semalysizeShortCircuitOperator(context, shortCircuitAnd);
    }

    private ReturnBehavior semalysizeShortCircuitOr(LocalContext context, ShortCircuitOr shortCircuitOr)
    {
        return semalysizeShortCircuitOperator(context, shortCircuitOr);
    }

    private ReturnBehavior semalysizeShortCircuitOperator(LocalContext context, ShortCircuitOperator shortCircuitOperator)
    {
        ReturnBehavior returnBehavior1 = semalysizeExpression(context, shortCircuitOperator.expression1);
        if (returnBehavior1.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(shortCircuitOperator.expression1));
        ReturnBehavior returnBehavior2 = semalysizeExpression(context, shortCircuitOperator.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(shortCircuitOperator.expression2));
        shortCircuitOperator.label1 = context.nextLabel();
        shortCircuitOperator.label2 = context.nextLabel();
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior semalysizeBooleanNot(LocalContext context, BooleanNot booleanNot)
    {
        ReturnBehavior returnBehavior = semalysizeExpression(context, booleanNot.expression);
        if (returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(booleanNot.expression));
        booleanNot.label1 = context.nextLabel();
        booleanNot.label2 = context.nextLabel();
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior semalysizeNegation(LocalContext context, Negation negation)
    {
        Type operandType = semalysizeExpression(context, negation.expression).type;
        if (!SemalyticalException.mustBeNumeric(negation.expression, errors))
            return ReturnBehavior.INT;
        Type resultType = operandType;
        if (operandType == RuntimeType.CHAR || operandType == RuntimeType.BYTE || operandType == RuntimeType.SHORT)
            resultType = RuntimeType.INT;
        if (resultType == RuntimeType.INT)
            negation.instruction = Instructions.ineg;
        else if (resultType == RuntimeType.LONG)
            negation.instruction = Instructions.lneg;
        else if (resultType == RuntimeType.FLOAT)
            negation.instruction = Instructions.fneg;
        else if (resultType == RuntimeType.DOUBLE)
            negation.instruction = Instructions.dneg;

        return new ReturnBehavior(resultType);
    }

    private ReturnBehavior semalysizeTypeCast(LocalContext context, Expression expression)
    {
        TypeCast typeCast = (TypeCast)expression.content;
        Type fromType = semalysizeExpression(context, typeCast.expression).type;

        // inline the TypeCast object. Other classes are used when needed.
        expression.content = typeCast.expression.content;

        // toType
        resolveType(typeCast.typeId, true);
        Type toType = typeCast.typeId.type;
        if (toType == RuntimeType.VOID) {
            errors.add(new SemalyticalException(typeCast.typeId, "can't cast to void."));
            toType = null;
        }
        // fromType
        if (fromType == RuntimeType.VOID) {
            errors.add(new SemalyticalException(typeCast, "can't cast from void."));
            fromType = null;
        }
        // error recovery
        if (fromType == null) {
            if (toType == null)
                return new ReturnBehavior(RuntimeType.getType(Object.class));
            return new ReturnBehavior(toType);
        } else if (toType == null)
            return new ReturnBehavior(fromType);

        // primitive vs reference
        if (fromType.isPrimitive() != toType.isPrimitive()) {
            errors.add(new SemalyticalException(typeCast.typeId, "can't cast between primitives and non-primitives"));
            return new ReturnBehavior(toType);
        }
        if (toType.isPrimitive()) {
            // primitive
            if (fromType == RuntimeType.BOOLEAN || toType == RuntimeType.BOOLEAN) {
                errors.add(SemalyticalException.cantCast(typeCast.typeId, fromType, toType));
                return new ReturnBehavior(toType);
            }
            convertPrimitive(context, fromType, toType, expression);
            return expression.returnBehavior;
        } else {
            // reference
            if (!fromType.isInstanceOf(toType)) {
                Expression innerExpression = new Expression(expression.content);
                innerExpression.returnBehavior = expression.returnBehavior;
                expression.content = new ReferenceConversion(innerExpression, toType);
                expression.returnBehavior = new ReturnBehavior(toType);
            }
            return new ReturnBehavior(toType);
        }
    }

    private ReturnBehavior semalysizeWhileLoop(LocalContext context, WhileLoop whileLoop)
    {
        whileLoop.continueToLabel = context.nextLabel();
        ReturnBehavior returnBehavior1 = semalysizeExpression(context, whileLoop.expression1);
        if (returnBehavior1.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(whileLoop.expression1));

        ReturnBehavior returnBehavior2 = semalysizeExpression(context, whileLoop.expression2);
        if (returnBehavior2.type != RuntimeType.VOID)
            errors.add(SemalyticalException.mustBeVoid(whileLoop.expression2));

        whileLoop.breakToLabel = context.nextLabel();
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeConstructorInvocation(LocalContext context, ConstructorInvocation constructorInvocation)
    {
        TypeId typeId = TypeId.fromId(constructorInvocation.functionInvocation.id);
        resolveType(typeId, true);
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, constructorInvocation.functionInvocation.arguments);
        constructorInvocation.constructor = resolveConstructor(typeId.type, argumentSignature);
        implicitCastArguments(context, constructorInvocation.functionInvocation.arguments, constructorInvocation.constructor.argumentSignature);
        if (constructorInvocation.constructor == null)
            errors.add(new SemalyticalException(constructorInvocation, "can't resolve this constructor"));
        return new ReturnBehavior(typeId.type);
    }

    private ReturnBehavior semalysizePreIncrement(LocalContext context, PreIncrement preIncrement)
    {
        return semalysizeIncrementDecrement(context, preIncrement);
    }
    private ReturnBehavior semalysizePreDecrement(LocalContext context, PreDecrement preDecrement)
    {
        return semalysizeIncrementDecrement(context, preDecrement);
    }
    private ReturnBehavior semalysizePostIncrement(LocalContext context, PostIncrement postIncrement)
    {
        return semalysizeIncrementDecrement(context, postIncrement);
    }
    private ReturnBehavior semalysizePostDecrement(LocalContext context, PostDecrement postDecrement)
    {
        return semalysizeIncrementDecrement(context, postDecrement);
    }
    private ReturnBehavior semalysizeIncrementDecrement(LocalContext context, IncrementDecrement incrementDecrement)
    {
        if (incrementDecrement.expression.content.getElementType() != Id.TYPE)
            errors.add(SemalyticalException.mustBeVariable(incrementDecrement.expression.content));
        else {
            incrementDecrement.id = (Id)incrementDecrement.expression.content;
            semalysizeId(context, incrementDecrement.id);
            if (incrementDecrement.id.variable.type != RuntimeType.INT)
                errors.add(SemalyticalException.variableMustBeInt(incrementDecrement.id));
        }
        return ReturnBehavior.INT;
    }

    private ReturnBehavior semalysizeForLoop(LocalContext context, ForLoop forLoop)
    {
        LocalContext innerContext = new LocalContext(context);
        ReturnBehavior returnBehavior1 = semalysizeExpression(innerContext, forLoop.expression1);
        if (returnBehavior1.type != RuntimeType.VOID)
            errors.add(SemalyticalException.mustBeVoid(forLoop.expression1));

        forLoop.continueToLabel = innerContext.nextLabel();
        semalysizeExpression(innerContext, forLoop.expression3);

        forLoop.initialGotoLabel = innerContext.nextLabel();
        ReturnBehavior returnBehavior2 = semalysizeExpression(innerContext, forLoop.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(forLoop.expression2));

        ReturnBehavior returnBehavior4 = semalysizeExpression(innerContext, forLoop.expression4);
        if (returnBehavior4.type != RuntimeType.VOID)
            errors.add(SemalyticalException.mustBeVoid(forLoop.expression4));

        forLoop.breakToLabel = innerContext.nextLabel();

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeArrayDereference(LocalContext context, ArrayDereference arrayDereference)
    {
        ReturnBehavior returnBehavior1 = semalysizeExpression(context, arrayDereference.expression1);
        if (returnBehavior1.type.getType() != ArrayType.TYPE)
            errors.add(new SemalyticalException(arrayDereference, "Can't dereference this thing like an array"));
        ReturnBehavior returnBehavior2 = semalysizeExpression(context, arrayDereference.expression2);
        if (returnBehavior2.type != RuntimeType.INT)
            errors.add(SemalyticalException.mustBeInt(arrayDereference.expression2));

        Type scalarType = ((ArrayType)returnBehavior1.type).scalarType;
        return new ReturnBehavior(scalarType);
    }

    private ReturnBehavior semalysizeStaticDereferenceField(LocalContext context, StaticDereferenceField staticDereferenceField)
    {
        // semalysization already done for typeId
        staticDereferenceField.field = resolveField(staticDereferenceField.typeId.type, staticDereferenceField.id);
        if (staticDereferenceField.field == null)
            errors.add(SemalyticalException.cantResolveField(staticDereferenceField.typeId.type, staticDereferenceField.id));
        return new ReturnBehavior(staticDereferenceField.field.returnType);
    }

    private ReturnBehavior semalysizeStaticFunctionInvocation(LocalContext context, StaticFunctionInvocation staticFunctionInvocation)
    {
        // semalysization already done for typeId
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, staticFunctionInvocation.functionInvocation.arguments);
        staticFunctionInvocation.functionInvocation.method = resolveMethod(staticFunctionInvocation.typeId.type, staticFunctionInvocation.functionInvocation, argumentSignature);
        implicitCastArguments(context, staticFunctionInvocation.functionInvocation.arguments, staticFunctionInvocation.functionInvocation.method.argumentSignature);
        Type returnType = staticFunctionInvocation.functionInvocation.method.returnType;
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior semalysizeTryCatch(LocalContext context, TryCatch tryCatch)
    {
        while (context.isOperandStackEmpty()) {
            SecretLocalVariable secretLocalVariable = context.addSecretLocalVariable(context.popOperand());
            tryCatch.preserveTheseOperands.add(secretLocalVariable);
        }
        ReturnBehavior tryPartReturnBehavior = semalysizeTryPart(context, tryCatch.tryPart);

        ReturnBehavior catchPartReturnBehavior = semalysizeCatchPart(context, tryCatch.catchPart);

        if (tryPartReturnBehavior.type != catchPartReturnBehavior.type)
            errors.add(new SemalyticalException(tryCatch, "return types must match"));
        tryCatch.type = tryPartReturnBehavior.type;
        return new ReturnBehavior(tryPartReturnBehavior.type);
    }

    private ReturnBehavior semalysizeTryPart(LocalContext context, TryPart tryPart)
    {
        semalysizeExpression(context, tryPart.expression);
        return tryPart.expression.returnBehavior;
    }

    private ReturnBehavior semalysizeCatchPart(LocalContext context, CatchPart catchPart)
    {
        return semalysizeCatchList(context, catchPart.catchList);
    }

    private ReturnBehavior semalysizeCatchList(LocalContext context, CatchList catchList)
    {
        Type returnType = null;
        for (CatchBody catchBody : catchList.elements) {
            ReturnBehavior returnBehavior = semalysizeCatchBody(context, catchBody);
            if (returnType == null)
                returnType = returnBehavior.type;
            else if (returnType != returnBehavior.type)
                errors.add(new SemalyticalException(catchList, "return types must match"));
        }
        if (returnType == null)
            errors.add(new SemalyticalException(catchList, "must catch something"));
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior semalysizeCatchBody(LocalContext context, CatchBody catchBody)
    {
        LocalContext nestedContext = new LocalContext(context);
        semalysizeVariableDeclaration(nestedContext, catchBody.variableDeclaration);
        if (!catchBody.variableDeclaration.typeId.type.isInstanceOf(RuntimeType.getType(Throwable.class)))
            errors.add(new SemalyticalException(catchBody.variableDeclaration, "Type must descend from Throwable. Can't catch a " + catchBody.variableDeclaration.typeId));
        ReturnBehavior returnBehavior = semalysizeExpression(nestedContext, catchBody.expression);
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
        errors.add(SemalyticalException.cantResolveLocalVariable(id));
        return -1;
    }

    private ReturnBehavior semalysizeDereferenceField(LocalContext context, DereferenceField dereferenceField)
    {
        Type type = semalysizeExpression(context, dereferenceField.expression).type;
        dereferenceField.field = resolveField(type, dereferenceField.id);
        if (dereferenceField.field == null)
            errors.add(SemalyticalException.cantResolveField(type, dereferenceField.id));
        return new ReturnBehavior(dereferenceField.field.returnType);
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
        errors.add(SemalyticalException.cantResolveLocalVariable(id));
        return -1;
    }

    private ReturnBehavior semalysizeDereferenceMethod(LocalContext context, DereferenceMethod dereferenceMethod)
    {
        ReturnBehavior expressionReturnBehavior = semalysizeExpression(context, dereferenceMethod.expression);
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, dereferenceMethod.functionInvocation.arguments);
        dereferenceMethod.functionInvocation.method = resolveMethod(expressionReturnBehavior.type, dereferenceMethod.functionInvocation, argumentSignature);
        implicitCastArguments(context, dereferenceMethod.functionInvocation.arguments, dereferenceMethod.functionInvocation.method.argumentSignature);
        return new ReturnBehavior(dereferenceMethod.functionInvocation.method.returnType);
    }

    private ReturnBehavior semalysizeFunctionInvocation(LocalContext context, FunctionInvocation functionInvocation)
    {
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, functionInvocation.arguments);
        functionInvocation.method = resolveMethod(context.getClassContext(), functionInvocation, argumentSignature);
        implicitCastArguments(context, functionInvocation.arguments, functionInvocation.method.argumentSignature);
        return new ReturnBehavior(functionInvocation.method.returnType);
    }

    private ReturnBehavior[] semalysizeArguments(LocalContext context, Arguments arguments)
    {
        deleteNulls(arguments);
        ReturnBehavior[] rtnArr = new ReturnBehavior[arguments.elements.size()];
        int i = 0;
        for (Expression element : arguments.elements) {
            if (element.returnBehavior == null)
                rtnArr[i++] = semalysizeExpression(context, element);
            else {
                // the conversion from addition to string concatenation has already semalysized some arguments
                rtnArr[i++] = element.returnBehavior;
            }
        }
        return rtnArr;
    }

    private void implicitCastArguments(LocalContext context, Arguments arguments, Type[] argumentSignature)
    {
        int i = 0;
        for (Expression element : arguments.elements)
            implicitCast(context, element, argumentSignature[i++]);
    }

    private ReturnBehavior semalysizeIfThenElse(LocalContext context, IfThenElse ifThenElse)
    {
        semalysizeExpression(context, ifThenElse.expression1);
        if (ifThenElse.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(ifThenElse.expression1));

        ifThenElse.label1 = context.nextLabel();
        semalysizeExpression(context, ifThenElse.expression2);

        ifThenElse.label2 = context.nextLabel();
        semalysizeExpression(context, ifThenElse.expression3);

        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new SemalyticalException(ifThenElse, "return types must match"));
        return new ReturnBehavior(ifThenElse.expression2.returnBehavior.type);
    }
    private ReturnBehavior semalysizeIfThen(LocalContext context, IfThen ifThen)
    {
        semalysizeExpression(context, ifThen.expression1);
        if (ifThen.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalException.mustBeBoolean(ifThen.expression1));
        ifThen.label = context.nextLabel();

        semalysizeExpression(context, ifThen.expression2);
        if (ifThen.expression2.returnBehavior.type != RuntimeType.VOID)
            errors.add(SemalyticalException.mustBeVoid(ifThen.expression2));

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeAssignment(LocalContext context, Assignment assignment)
    {
        assignment.id.variable = resolveId(context, assignment.id);
        if (assignment.id.variable == null)
            errors.add(SemalyticalException.cantResolveLocalVariable(assignment.id));
        semalysizeExpression(context, assignment.expression);
        if (assignment.id.variable != null)
            implicitCast(context, assignment.expression, assignment.id.variable.type);
        Type returnType = assignment.expression.returnBehavior.type;
        return new ReturnBehavior(returnType);
    }

    private void implicitCast(LocalContext context, Expression expression, Type toType)
    {
        Type fromType = expression.returnBehavior.type;
        if (fromType == toType)
            return;
        boolean primitive = fromType.isPrimitive();
        if (primitive != toType.isPrimitive()) {
            errors.add(new SemalyticalException(expression, "can't cast between primitives and non-primitives"));
            return;
        }
        if (primitive) {
            if ((fromType == RuntimeType.BOOLEAN) != (toType == RuntimeType.BOOLEAN)) {
                errors.add(SemalyticalException.cantConvert(expression, fromType, toType));
                return;
            }
            switch (RuntimeType.getPrimitiveConversionType(fromType, toType)) {
                case -1:
                    errors.add(SemalyticalException.cantConvert(expression, fromType, toType));
                    break;
                case 0:
                    throw null; // handled earlier
                case 1:
                    convertPrimitive(context, fromType, toType, expression);
                    break;
                default:
                    throw null;
            }
        } else {
            if (!fromType.isInstanceOf(toType))
                errors.add(SemalyticalException.cantConvert(expression, fromType, toType));
        }
        expression.returnBehavior = new ReturnBehavior(toType); // TODO this is overwriting a valid object at least sometimes
    }

    private ReturnBehavior semalysizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        resolveType(variableDeclaration.typeId, true);
        if (variableDeclaration.typeId.type == RuntimeType.VOID)
            errors.add(new SemalyticalException(variableDeclaration, "You can't have a void variable."));
        context.addLocalVariable(variableDeclaration.id, variableDeclaration.typeId.type, errors);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeId(LocalContext context, Id id)
    {
        id.variable = resolveId(context, id);
        if (id.variable == null) {
            errors.add(SemalyticalException.cantResolveLocalVariable(id));
            return ReturnBehavior.UNKNOWN;
        }
        return new ReturnBehavior(id.variable.type);
    }

    private ReturnBehavior semalysizeVariableCreation(LocalContext context, VariableCreation variableCreation)
    {
        semalysizeVariableDeclaration(context, variableCreation.variableDeclaration);
        semalysizeExpression(context, variableCreation.expression);
        implicitCast(context, variableCreation.expression, variableCreation.variableDeclaration.typeId.type);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeBlock(LocalContext context, Block block)
    {
        block.context = new LocalContext(context);
        return semalysizeBlockContents(block.context, block.blockContents);
    }

    private ReturnBehavior semalysizeBlockContents(LocalContext context, BlockContents blockContents)
    {
        blockContents.forceVoid = blockContents.elements.size() == 0 || blockContents.elements.get(blockContents.elements.size() - 1) == null;

        deleteNulls(blockContents);

        Type returnType = RuntimeType.VOID;
        for (Expression element : blockContents.elements) {
            ReturnBehavior returnBehavior = semalysizeExpression(context, element);
            returnType = returnBehavior.type;
        }
        if (blockContents.forceVoid)
            returnType = RuntimeType.VOID;
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior semalysizeQuantity(LocalContext context, Quantity quantity)
    {
        return semalysizeExpression(context, quantity.expression);
    }

    private ReturnBehavior semalysizeIntLiteral(LocalContext context, IntLiteral intLiteral)
    {
        return ReturnBehavior.INT;
    }
    private ReturnBehavior semalysizeLongLiteral(LocalContext context, LongLiteral longLiteral)
    {
        return ReturnBehavior.LONG;
    }
    private ReturnBehavior semalysizeFloatLiteral(LocalContext context, FloatLiteral floatLiteral)
    {
        return ReturnBehavior.FLOAT;
    }
    private ReturnBehavior semalysizeDoubleLiteral(LocalContext context, DoubleLiteral doubleLiteral)
    {
        return ReturnBehavior.DOUBLE;
    }
    private ReturnBehavior semalysizeBooleanLiteral(LocalContext context, BooleanLiteral booleanLiteral)
    {
        return ReturnBehavior.BOOLEAN;
    }
    private ReturnBehavior semalysizeStringLiteral(LocalContext context, StringLiteral stringLiteral)
    {
        return ReturnBehavior.STRING;
    }

    private ReturnBehavior semalysizeAddition(LocalContext context, Expression expression)
    {
        Addition addition = (Addition)expression.content;
        Type returnType1 = semalysizeExpression(context, addition.expression1).type;
        Type returnType2 = semalysizeExpression(context, addition.expression2).type;
        if (returnType1 == RuntimeType.STRING || returnType2 == RuntimeType.STRING) {
            // convert to string concatenation
            // a + b
            // becomes
            // String.valueOf(a).concat(String.valueOf(b))
            Expression string1 = stringValueOf(addition.expression1);
            Expression string2 = stringValueOf(addition.expression2);
            expression.content = new DereferenceMethod(string1, new FunctionInvocation(new Id("concat"), new Arguments(Arrays.asList(string2))));
            return semalysizeExpression(context, expression);
        } else {
            return semalysizeNumericOperator(context, addition);
        }
    }
    private Expression stringValueOf(Expression expression)
    {
        return new Expression(new DereferenceMethod(new Expression(new Id("String")), new FunctionInvocation(new Id("valueOf"), new Arguments(Arrays.asList(expression)))));
    }
    private ReturnBehavior semalysizeSubtraction(LocalContext context, Subtraction subtraction)
    {
        return semalysizeNumericOperator(context, subtraction);
    }
    private ReturnBehavior semalysizeMultiplication(LocalContext context, Multiplication multiplication)
    {
        return semalysizeNumericOperator(context, multiplication);
    }
    private ReturnBehavior semalysizeDivision(LocalContext context, Division division)
    {
        return semalysizeNumericOperator(context, division);
    }
    private ReturnBehavior semalysizeNumericOperator(LocalContext context, BinaryOperatorElement operator)
    {
        Type returnType1 = lazySemalysizeExpression(context, operator.expression1).type;
        boolean good = SemalyticalException.mustBeNumeric(operator.expression1, errors);

        Type returnType2 = lazySemalysizeExpression(context, operator.expression2).type;
        good &= SemalyticalException.mustBeNumeric(operator.expression2, errors);

        if (!good)
            return ReturnBehavior.INT;

        Type resultType = RuntimeType.getPrimitiveConversionType(returnType1, returnType2) < 0 ? returnType1 : returnType2;
        if (resultType == RuntimeType.CHAR || resultType == RuntimeType.BYTE || resultType == RuntimeType.SHORT)
            resultType = RuntimeType.INT;
        convertPrimitive(context, returnType1, resultType, operator.expression1);
        convertPrimitive(context, returnType2, resultType, operator.expression2);

        operator.type = resultType;
        return new ReturnBehavior(resultType);
    }
    private ReturnBehavior lazySemalysizeExpression(LocalContext context, Expression expression)
    {
        if (expression.returnBehavior != null)
            return expression.returnBehavior;
        return semalysizeExpression(context, expression);
    }
    private ReturnBehavior semalysizeLessThan(LocalContext context, LessThan lessThan)
    {
        return semalysizeComparisonOperator(context, lessThan, false);
    }
    private ReturnBehavior semalysizeGreaterThan(LocalContext context, GreaterThan greaterThan)
    {
        return semalysizeComparisonOperator(context, greaterThan, false);
    }
    private ReturnBehavior semalysizeLessThanOrEqual(LocalContext context, LessThanOrEqual lessThanOrEqual)
    {
        return semalysizeComparisonOperator(context, lessThanOrEqual, false);
    }
    private ReturnBehavior semalysizeGreaterThanOrEqual(LocalContext context, GreaterThanOrEqual greaterThanOrEqual)
    {
        return semalysizeComparisonOperator(context, greaterThanOrEqual, false);
    }
    private ReturnBehavior semalysizeEquality(LocalContext context, Equality equality)
    {
        return semalysizeComparisonOperator(context, equality, true);
    }
    private ReturnBehavior semalysizeInequality(LocalContext context, Inequality inequality)
    {
        return semalysizeComparisonOperator(context, inequality, true);
    }
    private ReturnBehavior semalysizeComparisonOperator(LocalContext context, ComparisonOperator operator, boolean allowReferenceOperands)
    {
        operator.label1 = context.nextLabel();
        operator.label2 = context.nextLabel();
        return semalysizeOperator(context, operator, RuntimeType.BOOLEAN, allowReferenceOperands);
    }
    private ReturnBehavior semalysizeOperator(LocalContext context, BinaryOperatorElement operator, Type returnType, boolean allowReferenceOperands)
    {
        Type returnType1 = semalysizeExpression(context, operator.expression1).type;
        Type returnType2 = semalysizeExpression(context, operator.expression2).type;
        if (allowReferenceOperands) {
            if (!(returnType1.isInstanceOf(returnType2) || returnType2.isInstanceOf(returnType1)))
                errors.add(new SemalyticalException(operator, "operand types are incompatible."));
        } else {
            if (!returnType1.isPrimitive())
                errors.add(new SemalyticalException(operator.expression1, "operand can't be a reference type."));
            if (!returnType2.isPrimitive())
                errors.add(new SemalyticalException(operator.expression2, "operand can't be a reference type."));
        }
        returnType = returnType != null ? returnType : returnType1;
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
            errors.add(SemalyticalException.cantResolveImport(qualifiedName));
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
            errors.add(SemalyticalException.cantResolveType(typeId));
        return !failure;
    }

    private Method resolveMethod(Type type, FunctionInvocation functionInvocation, ReturnBehavior[] argumentSignature)
    {
        Method method = type.resolveMethod(functionInvocation.id.name, getArgumentTypes(argumentSignature));
        if (method == null) {
            errors.add(SemalyticalException.cantResolveMethod(type, functionInvocation.id, argumentSignature));
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
    private void convertPrimitive(LocalContext context, Type fromType, Type toType, Expression expression)
    {
        // same type needs no conversion
        if (fromType == toType)
            return;
        // for little baby types, treat them like ints
        if (fromType == RuntimeType.CHAR || fromType == RuntimeType.BYTE || fromType == RuntimeType.SHORT) {
            convertPrimitive(context, RuntimeType.INT, toType, expression);
            return;
        }
        // for converting great big types down to little baby types, we need to do it in two steps
        if (fromType == RuntimeType.LONG || fromType == RuntimeType.FLOAT || fromType == RuntimeType.DOUBLE) {
            if (toType == RuntimeType.CHAR || toType == RuntimeType.BYTE || toType == RuntimeType.SHORT) {
                // convert down to int
                convertPrimitive(context, fromType, RuntimeType.INT, expression);
                // convert down to the baby type
                convertPrimitive(context, RuntimeType.INT, toType, expression);
                return;
            }
        }
        // single operation conversion
        byte instruction = getPrimitiveConversionInstruction(fromType, toType);
        Expression innerExpression = new Expression(expression.content);
        innerExpression.returnBehavior = new ReturnBehavior(fromType);
        expression.content = new PrimitiveConversion(innerExpression, instruction, toType);
        expression.returnBehavior = new ReturnBehavior(toType);
    }
    private static byte getPrimitiveConversionInstruction(Type fromType, Type toType)
    {
        if (fromType == RuntimeType.INT) {
            if (toType == RuntimeType.CHAR)
                return Instructions.i2c;
            if (toType == RuntimeType.BYTE)
                return Instructions.i2b;
            if (toType == RuntimeType.SHORT)
                return Instructions.i2s;
            if (toType == RuntimeType.LONG)
                return Instructions.i2l;
            if (toType == RuntimeType.FLOAT)
                return Instructions.i2f;
            if (toType == RuntimeType.DOUBLE)
                return Instructions.i2d;
            throw null;
        }
        if (fromType == RuntimeType.LONG) {
            if (toType == RuntimeType.INT)
                return Instructions.l2i;
            if (toType == RuntimeType.FLOAT)
                return Instructions.l2f;
            if (toType == RuntimeType.DOUBLE)
                return Instructions.l2d;
            throw null;
        }
        if (fromType == RuntimeType.FLOAT) {
            if (toType == RuntimeType.INT)
                return Instructions.f2i;
            if (toType == RuntimeType.LONG)
                return Instructions.f2l;
            if (toType == RuntimeType.DOUBLE)
                return Instructions.f2d;
            throw null;
        }
        if (fromType == RuntimeType.DOUBLE) {
            if (toType == RuntimeType.INT)
                return Instructions.d2i;
            if (toType == RuntimeType.LONG)
                return Instructions.d2l;
            if (toType == RuntimeType.FLOAT)
                return Instructions.d2f;
            throw null;
        }
        throw null;
    }

    private static void deleteNulls(ListElement<?> listElement)
    {
        Iterator<?> iterator = listElement.elements.iterator();
        while (iterator.hasNext())
            if (iterator.next() == null)
                iterator.remove();
    }
}
