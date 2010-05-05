package net.wolfesoftware.jax.semalysis;

import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.codegen.Instructions;
import net.wolfesoftware.jax.parsing.Parsing;
import net.wolfesoftware.jax.tokenization.Lang;

public class Semalysizer
{
    public static Semalysization semalysize(Parsing parsing, String filePathRelativeToClassPath)
    {
        return new Semalysizer(parsing, filePathRelativeToClassPath).semalysizeRoot();
    }

    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        // import built-in types
        RuntimeType.initPrimitives(importedTypes);
        RuntimeType.initJavaLang(importedTypes);
    }
    private final Root root;
    private final String expectedQualifiedPackageName;
    private final String expectedClassName;
    private String qualifiedPackageName = null;
    private final ArrayList<SemalyticalError> errors = new ArrayList<SemalyticalError>();

    private Semalysizer(Parsing parsing, String filePathRelativeToClassPath)
    {
        root = parsing.root;
        int lastSlash = filePathRelativeToClassPath.lastIndexOf('/');
        expectedQualifiedPackageName = lastSlash != -1 ? filePathRelativeToClassPath.substring(0, lastSlash).replace('/', '.') : "";
        expectedClassName = filePathRelativeToClassPath.substring(lastSlash + 1, filePathRelativeToClassPath.length() - ".jax".length());
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
            errors.add(new SemalyticalError(EmptyElement.INSTANCE, "Things are broken"));
        return new Semalysization(root, errors);
    }

    private void semalysizeCompilationUnit(CompilationUnit compilationUnit)
    {
        semalysizePackageStatements(compilationUnit.packageStatements);
        semalysizeImports(compilationUnit.imports);
        semalysizeClassDeclaration(compilationUnit.classDeclaration);
    }

    private void semalysizePackageStatements(PackageStatements packageStatements)
    {
        for (PackageStatement packageStatement : packageStatements.elements) {
            if (qualifiedPackageName != null) {
                errors.add(new SemalyticalError(packageStatement, "only one package statement is allowed"));
                continue;
            }
            qualifiedPackageName = semalysizePackageStatement(packageStatement);
        }
        // no package statement means default package
        if (qualifiedPackageName == null)
            qualifiedPackageName = "";
    }

    private String semalysizePackageStatement(PackageStatement packageStatement)
    {
        if (expectedQualifiedPackageName.equals("")) {
            errors.add(new SemalyticalError(packageStatement, "no package statement allowed in default package."));
            return expectedQualifiedPackageName;
        }

        String actualQualifiedPackageName = packageStatement.qualifiedName.decompile();
        if (!actualQualifiedPackageName.equals(expectedQualifiedPackageName)) {
            errors.add(new SemalyticalError(packageStatement.qualifiedName, "package name must be \"" + expectedQualifiedPackageName + "\"."));
            return expectedQualifiedPackageName;
        }

        return actualQualifiedPackageName;
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
        BuiltinPackageLister.importPackageStar(importStar.qualifiedName, importedTypes, errors);
    }

    private void semalysizeImportClass(ImportClass importClass)
    {
        resolveQualifiedName(importClass.qualifiedName);
    }

    private void semalysizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        if (!classDeclaration.className.equals(expectedClassName))
            errors.add(new SemalyticalError(classDeclaration.className, "Class name does not match file name \"" + expectedClassName + "\"."));

        semalysizeClassModifiers(classDeclaration.classModifiers);
        String qualifiedClassName = expectedClassName;
        if (!qualifiedPackageName.equals(""))
            qualifiedClassName = qualifiedPackageName + "." + expectedClassName;

        classDeclaration.localType = new LocalType(qualifiedClassName, classDeclaration.className);
        importedTypes.put(expectedClassName, classDeclaration.localType);
        semalysizeClassBody(classDeclaration.localType, classDeclaration.classBody);
    }

    private void semalysizeClassModifiers(ClassModifiers classModifiers)
    {
        // TODO: code duplication
        for (ClassModifier classModifier : classModifiers.elements) {
            if ((classModifiers.bitmask & classModifier.bitmask) != 0)
                errors.add(new SemalyticalError(classModifier, "Please say that it's \"" + classModifier + "\" at most once."));
            classModifiers.bitmask |= classModifier.bitmask;
        }
    }

    private void semalysizeClassBody(LocalType context, ClassBody classBody)
    {
        deleteNulls(classBody);

        for (ClassMember classMember : classBody.elements)
            preSemalysizeClassMemeber(context, classMember);

        // add default constructor if needed
        if (context.constructors.isEmpty()) {
            ClassMember classMember = context.makeDefaultConstructor(classBody);
            classBody.elements.add(classMember);
            preSemalysizeClassMemeber(context, classMember);
        }

        // we have to put off constructor semalysis because field initializer code can turn up after them.
        ArrayList<ConstructorDeclaration> maybeLater = new ArrayList<ConstructorDeclaration>();
        for (ClassMember classMember : classBody.elements) {
            ParseElement content = classMember.content;
            switch (content.getElementType()) {
                case ConstructorDeclaration.TYPE:
                    maybeLater.add((ConstructorDeclaration)content);
                    break;
                case MethodDeclaration.TYPE:
                    semalysizeMethodDeclaration(context, (MethodDeclaration)content);
                    break;
                case FieldDeclaration.TYPE:
                    // it's just a field, guys. nothing to see here.
                    break;
                case FieldCreation.TYPE:
                    semalysizeFieldCreation(context, (FieldCreation)content);
                    break;
                default:
                    throw new RuntimeException(content.getClass().toString());
            }
        }

        // static initializer
        List<Expression> staticInitializerExpressions = context.getStaticInitializerExpressions();
        if (!staticInitializerExpressions.isEmpty())
            staticInitializerExpressions.add(null);
        semalysizeExpression(context.staticInitializer.context, context.staticInitializer.expression);

        // constructors
        for (ConstructorDeclaration constructorDeclaration : maybeLater)
            semalysizeConstructorDeclaration(context, constructorDeclaration);
    }

    private void preSemalysizeClassMemeber(LocalType context, ClassMember classMember)
    {
        if (classMember == null)
            return;
        ParseElement content = classMember.content;
        switch (content.getElementType()) {
            case MethodDeclaration.TYPE:
                preSemalysizeMethodDeclaration(context, (MethodDeclaration)content);
                break;
            case ConstructorDeclaration.TYPE:
                preSemalysizeConstructorDeclaration(context, (ConstructorDeclaration)content);
                break;
            case FieldDeclaration.TYPE:
            case FieldCreation.TYPE:
                preSemalysizeFieldDeclaration(context, (FieldDeclaration)content);
                break;
            default:
                throw new RuntimeException("TODO: implement " + content.getClass().getName());
        }
    }

    private void preSemalysizeFieldDeclaration(LocalType context, FieldDeclaration fieldDeclaration)
    {
        semalysizeFieldModifiers(fieldDeclaration.fieldModifiers);
        resolveType(fieldDeclaration.typeId, true);
        if (fieldDeclaration.typeId.type == RuntimeType.VOID) {
            errors.add(new SemalyticalError(fieldDeclaration.typeId, "No void fields allowed."));
            fieldDeclaration.typeId.type = UnknownType.INSTANCE;
        }
        fieldDeclaration.field = new Field(context, fieldDeclaration.typeId.type, fieldDeclaration.fieldName, fieldDeclaration.fieldModifiers.isStatic());
        context.addField(fieldDeclaration.field);
    }

    private void semalysizeFieldModifiers(FieldModifiers fieldModifiers)
    {
        // TODO: code duplication
        for (FieldModifier fieldModifier : fieldModifiers.elements) {
            if ((fieldModifiers.bitmask & fieldModifier.bitmask) != 0)
                errors.add(new SemalyticalError(fieldModifier, "Please say that it's \"" + fieldModifier + "\" at most once."));
            fieldModifiers.bitmask |= fieldModifier.bitmask;
        }
    }

    private void preSemalysizeConstructorDeclaration(LocalType context, ConstructorDeclaration constructorDeclaration)
    {
        semalysizeMethodModifiers(constructorDeclaration.methodModifiers);
        resolveType(constructorDeclaration.typeId, true);
        if (constructorDeclaration.typeId.type != context)
            errors.add(new SemalyticalError(constructorDeclaration.typeId, "you can't have a constructor for type \"" + constructorDeclaration.typeId.type + "\" in this class."));
        constructorDeclaration.context = new RootLocalContext(context, false);
        Type[] arguemntSignature = semalysizeArgumentDeclarations(constructorDeclaration.context, constructorDeclaration.argumentDeclarations);
        constructorDeclaration.constructor = new Constructor(context, arguemntSignature);
        context.addConstructor(constructorDeclaration.constructor);
    }

    private void semalysizeMethodModifiers(MethodModifiers methodModifiers)
    {
        // TODO: code duplication
        for (MethodModifier methodModifier : methodModifiers.elements) {
            if ((methodModifiers.bitmask & methodModifier.bitmask) != 0)
                errors.add(new SemalyticalError(methodModifier, "Please say that it's \"" + methodModifier + "\" at most once."));
            methodModifiers.bitmask |= methodModifier.bitmask;
        }
    }

    private void preSemalysizeMethodDeclaration(LocalType context, MethodDeclaration methodDeclaration)
    {
        semalysizeMethodModifiers(methodDeclaration.methodModifiers);
        resolveType(methodDeclaration.typeId, true);
        methodDeclaration.context = new RootLocalContext(context, methodDeclaration.isStatic());
        Type[] arguemntSignature = semalysizeArgumentDeclarations(methodDeclaration.context, methodDeclaration.argumentDeclarations);
        methodDeclaration.method = new Method(context, methodDeclaration.typeId.type, methodDeclaration.methodName, arguemntSignature, true);
        context.addMethod(methodDeclaration.method);
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

    private void semalysizeFieldCreation(LocalType context, FieldCreation fieldCreation)
    {
        if (fieldCreation.field.isStatic) {
            StaticFieldAssignment staticFieldAssignment = new StaticFieldAssignment(fieldCreation.field, Lang.SYMBOL_EQUALS, fieldCreation.expression);
            context.getStaticInitializerExpressions().add(new Expression(staticFieldAssignment));
        } else {
            InstanceFieldAssignment fieldAssignment = new InstanceFieldAssignment(new Expression(ThisExpression.INSTANCE), fieldCreation.field, Lang.SYMBOL_EQUALS, fieldCreation.expression);
            context.initializerExpressions.add(new Expression(fieldAssignment));
        }
    }

    private void semalysizeConstructorDeclaration(LocalType typeContext, ConstructorDeclaration constructorDeclaration)
    {
        LocalContext context = new RootLocalContext(typeContext, false);
        // look for a constructor redirect
        if (constructorDeclaration.expression.content.getElementType() != Block.TYPE) {
            errors.add(new SemalyticalError(constructorDeclaration.expression, "Constructor bodies must be blocks."));
            return;
        }
        List<Expression> bodyElements = ((Block)constructorDeclaration.expression.content).blockContents.elements;

        ConstructorRedirect constructorRedirect = null;
        if (!bodyElements.isEmpty()) {
            ParseElement firstExpressionElement = bodyElements.get(0).content;
            if (firstExpressionElement.getElementType() == ConstructorRedirect.TYPE) {
                bodyElements.remove(0); // re add it later
                constructorRedirect = (ConstructorRedirect)firstExpressionElement;
            }
        }
        if (constructorRedirect == null)
            constructorRedirect = new ConstructorRedirect(Lang.KEYWORD_SUPER, new Arguments(new LinkedList<Expression>()));

        Expression constructorRedirectExpression = new Expression(constructorRedirect);
        constructorRedirectExpression.returnBehavior = semalysizeConstructorRedirect(context, constructorRedirect);

        // initial stuff is a block with the redirect and all the initializer code inline
        List<Expression> initialStuffElements = new ArrayList<Expression>();
        initialStuffElements.add(constructorRedirectExpression);
        for (Expression initializerExpression : context.getClassContext().initializerExpressions)
            initialStuffElements.add((Expression)initializerExpression.cloneElement());
        // this block should return null
        initialStuffElements.add(null);

        // add the initial stuff to the constructor body block to be semalysized
        Expression initialStuffExpression = new Expression(new Block(new BlockContents(initialStuffElements)));
        bodyElements.add(0, initialStuffExpression);

        constructorDeclaration.returnBehavior = semalysizeExpression(constructorDeclaration.context, constructorDeclaration.expression);
        if (constructorDeclaration.returnBehavior.type != RuntimeType.VOID)
            errors.add(SemalyticalError.mustBeVoid(constructorDeclaration.expression));
    }

    private void semalysizeMethodDeclaration(LocalType context, MethodDeclaration methodDeclaration)
    {
        semalysizeExpression(methodDeclaration.context, methodDeclaration.expression);
        implicitCast(methodDeclaration.context, methodDeclaration.expression, methodDeclaration.method.returnType);
        methodDeclaration.returnBehavior = methodDeclaration.expression.returnBehavior;
    }

    private ReturnBehavior semalysizeExpression(LocalContext context, Expression expression)
    {
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
            case AmbiguousPreIncrementDecrement.TYPE:
            case AmbiguousPostIncrementDecrement.TYPE:
                returnBehavior = semalysizeAmbiguousIncrementDecrement(context, expression);
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
            case AmbiguousId.TYPE:
                returnBehavior = semalysizeAmbiguousId(context, expression);
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
            case AmbiguousAssignment.TYPE:
                returnBehavior = semalysizeAmbiguousAssignment(context, expression);
                break;
            case StaticFieldAssignment.TYPE:
                // called for statically initialized fields
                returnBehavior = semalysizeStaticFieldAssignment(context, (StaticFieldAssignment)expression.content);
                break;
            case InstanceFieldAssignment.TYPE:
                // called for inlined field creation 
                returnBehavior = semalysizeInstanceFieldAssignment(context, (InstanceFieldAssignment)expression.content);
                break;
            case IfThenElse.TYPE:
            case QuestionColon.TYPE:
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
            case ConstructorInvocation.TYPE:
                returnBehavior = semalysizeConstructorInvocation(context, (ConstructorInvocation)content);
                break;
            case ConstructorRedirect.TYPE:
                // either already done, or an error
                if (expression.returnBehavior == null)
                    errors.add(new SemalyticalError(expression.content, "You can only redirect a constructor at the beginning of a constructor."));
                returnBehavior = ReturnBehavior.VOID;
                break;
            case AmbiguousMethodInvocation.TYPE:
                returnBehavior = semalysizeAmbiguousMethodInvocation(context, expression);
                break;
            case AmbiguousImplicitThisMethodInvocation.TYPE:
                returnBehavior = semalysizeAmbiguousImplicitThisMethodInvocation(context, expression);
                break;
            case AmbiguousFieldExpression.TYPE:
                returnBehavior = semalysizeAmbiguousFieldExpression(context, expression);
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
            case ThisExpression.TYPE:
                returnBehavior = semalysizeThisExpression(context, (ThisExpression)content);
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
        Type thisType = context.getClassContext();
        Type constructorType;
        if (constructorRedirect.keyword == Lang.KEYWORD_THIS)
            constructorType = thisType;
        else if (constructorRedirect.keyword == Lang.KEYWORD_SUPER)
            constructorType = thisType.getParent();
        else
            throw null;
        constructorRedirect.constructor = resolveConstructor(constructorType, argumentSignature);
        if (constructorRedirect.constructor != null)
            implicitCastArguments(context, constructorRedirect.arguments, constructorRedirect.constructor.argumentSignature);
        else
            errors.add(new SemalyticalError(constructorRedirect, "can't resolve this constructor"));
        return ReturnBehavior.VOID;
    }
    private ReturnBehavior semalysizeNullExpression(LocalContext context, NullExpression nullExpression)
    {
        return ReturnBehavior.NULL;
    }
    private ReturnBehavior semalysizeThisExpression(LocalContext context, ThisExpression nullExpression)
    {
        return new ReturnBehavior(context.getClassContext());
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
            errors.add(SemalyticalError.mustBeBoolean(shortCircuitOperator.expression1));
        ReturnBehavior returnBehavior2 = semalysizeExpression(context, shortCircuitOperator.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalError.mustBeBoolean(shortCircuitOperator.expression2));
        shortCircuitOperator.label1 = context.nextLabel();
        shortCircuitOperator.label2 = context.nextLabel();
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior semalysizeBooleanNot(LocalContext context, BooleanNot booleanNot)
    {
        ReturnBehavior returnBehavior = semalysizeExpression(context, booleanNot.expression);
        if (returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalError.mustBeBoolean(booleanNot.expression));
        booleanNot.label1 = context.nextLabel();
        booleanNot.label2 = context.nextLabel();
        return new ReturnBehavior(RuntimeType.BOOLEAN);
    }

    private ReturnBehavior semalysizeNegation(LocalContext context, Negation negation)
    {
        Type operandType = semalysizeExpression(context, negation.expression).type;
        if (!operandType.isNumeric()) {
            errors.add(SemalyticalError.mustBeNumeric(negation.expression, operandType));
            return ReturnBehavior.INT;
        }
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
            errors.add(new SemalyticalError(typeCast.typeId, "can't cast to void."));
            toType = null;
        }
        // fromType
        if (fromType == RuntimeType.VOID) {
            errors.add(new SemalyticalError(typeCast, "can't cast from void."));
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
            if (!(fromType == UnknownType.INSTANCE || toType == UnknownType.INSTANCE))
                errors.add(new SemalyticalError(typeCast.typeId, "Can't cast between primitives and non-primitives")); // TODO: code duplication
            return new ReturnBehavior(toType);
        }
        if (toType.isPrimitive()) {
            // primitive
            if (fromType == RuntimeType.BOOLEAN || toType == RuntimeType.BOOLEAN) {
                errors.add(SemalyticalError.cantCast(typeCast.typeId, fromType, toType));
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
            errors.add(SemalyticalError.mustBeBoolean(whileLoop.expression1));

        ReturnBehavior returnBehavior2 = semalysizeExpression(context, whileLoop.expression2);
        if (returnBehavior2.type != RuntimeType.VOID)
            errors.add(SemalyticalError.mustBeVoid(whileLoop.expression2));

        whileLoop.breakToLabel = context.nextLabel();
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeConstructorInvocation(LocalContext context, ConstructorInvocation constructorInvocation)
    {
        TypeId typeId = TypeId.fromName(constructorInvocation.typeName.text);
        resolveType(typeId, true);
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, constructorInvocation.arguments);
        constructorInvocation.constructor = resolveConstructor(typeId.type, argumentSignature);
        if (constructorInvocation.constructor == null)
            errors.add(new SemalyticalError(constructorInvocation, "can't resolve this constructor"));
        else
            implicitCastArguments(context, constructorInvocation.arguments, constructorInvocation.constructor.argumentSignature);
        return new ReturnBehavior(typeId.type);
    }

    private ReturnBehavior semalysizeAmbiguousIncrementDecrement(LocalContext context, Expression expression)
    {
        AmbiguousIncrementDecrement incrementDecrement = (AmbiguousIncrementDecrement)expression.content;
        if (semalysizeExpression(context, incrementDecrement.expression).type == UnknownType.INSTANCE)
            return ReturnBehavior.UNKNOWN;

        ParseElement content = incrementDecrement.expression.content;
        switch (content.getElementType()) {
            case LocalVariableExpression.TYPE: {
                LocalVariableExpression localVariableExpression = (LocalVariableExpression)content;
                LocalVariableIncrementDecrement localVariableIncrementDecrement = incrementDecrement.makeLocalVariableDisambiguation(localVariableExpression.variable);
                return semalysizeAbstractIncrementDecrement(context, localVariableIncrementDecrement);
            }
            case InstanceFieldExpression.TYPE: {
                InstanceFieldExpression instanceFieldExpression = (InstanceFieldExpression)content;
                InstanceFieldIncrementDecrement instanceFieldIncrementDecrement = incrementDecrement.makeInstanceFieldDisambiguation(instanceFieldExpression.leftExpression, instanceFieldExpression.field);
                return semalysizeAbstractIncrementDecrement(context, instanceFieldIncrementDecrement);
            }
            case StaticFieldExpression.TYPE: {
                StaticFieldExpression staticFieldExpression = (StaticFieldExpression)content;
                StaticFieldIncrementDecrement staticFieldIncrementDecrement = incrementDecrement.makeStaticFieldDisambiguation(staticFieldExpression.field);
                return semalysizeAbstractIncrementDecrement(context, staticFieldIncrementDecrement);
            }
            default: {
                errors.add(new SemalyticalError(incrementDecrement, "Can't assign into this kind of thing"));
                return ReturnBehavior.UNKNOWN;
            }
        }
    }

    private ReturnBehavior semalysizeAbstractIncrementDecrement(LocalContext context, AbstractIncrementDecrement abstractIncrementDecrement)
    {
        Type type = abstractIncrementDecrement.getAssignmentTargetType();
        if (!type.isNumeric()) {
            errors.add(SemalyticalError.mustBeNumeric(abstractIncrementDecrement, type));
            return ReturnBehavior.UNKNOWN;
        }
        return new ReturnBehavior(RuntimeType.promoteBabyPrimitiveNumericTypes(type));
    }

    private ReturnBehavior semalysizeForLoop(LocalContext context, ForLoop forLoop)
    {
        LocalContext innerContext = context.makeSubContext();
        ReturnBehavior returnBehavior1 = semalysizeExpression(innerContext, forLoop.expression1);
        if (returnBehavior1.type != RuntimeType.VOID)
            errors.add(SemalyticalError.mustBeVoid(forLoop.expression1));

        forLoop.continueToLabel = innerContext.nextLabel();
        semalysizeExpression(innerContext, forLoop.expression3);

        forLoop.initialGotoLabel = innerContext.nextLabel();
        ReturnBehavior returnBehavior2 = semalysizeExpression(innerContext, forLoop.expression2);
        if (returnBehavior2.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalError.mustBeBoolean(forLoop.expression2));

        ReturnBehavior returnBehavior4 = semalysizeExpression(innerContext, forLoop.expression4);
        if (returnBehavior4.type != RuntimeType.VOID)
            errors.add(SemalyticalError.mustBeVoid(forLoop.expression4));

        forLoop.breakToLabel = innerContext.nextLabel();

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeArrayDereference(LocalContext context, ArrayDereference arrayDereference)
    {
        ReturnBehavior returnBehavior1 = semalysizeExpression(context, arrayDereference.expression1);
        if (returnBehavior1.type.getType() != ArrayType.TYPE)
            errors.add(new SemalyticalError(arrayDereference, "Can't dereference this thing like an array"));
        ReturnBehavior returnBehavior2 = semalysizeExpression(context, arrayDereference.expression2);
        if (returnBehavior2.type != RuntimeType.INT)
            errors.add(SemalyticalError.mustBeInt(arrayDereference.expression2));

        Type scalarType = ((ArrayType)returnBehavior1.type).scalarType;
        return new ReturnBehavior(scalarType);
    }

    private ReturnBehavior semalysizeStaticMethodInvocation(LocalContext context, StaticMethodInvocation staticMethodInvocation)
    {
        // semalysization is already done for typeId
        return semalysizeAbstractMethodInvocation(context, staticMethodInvocation.typeId.type, staticMethodInvocation);
    }
    private ReturnBehavior semalysizeAbstractMethodInvocation(LocalContext context, Type type, AbstractMethodInvocation methodInvocation)
    {
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, methodInvocation.arguments);
        methodInvocation.method = resolveMethod(type, methodInvocation.methodName, argumentSignature);
        implicitCastArguments(context, methodInvocation.arguments, methodInvocation.method.argumentSignature);
        Type returnType = methodInvocation.method.returnType;
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior semalysizeTryCatch(LocalContext context, TryCatch tryCatch)
    {
        ReturnBehavior tryPartReturnBehavior = semalysizeTryPart(context, tryCatch.tryPart);
        ReturnBehavior catchPartReturnBehavior = semalysizeCatchPart(context, tryCatch.catchPart);
        if (tryPartReturnBehavior.type != catchPartReturnBehavior.type)
            errors.add(new SemalyticalError(tryCatch, "return types must match")); // TODO code duplication
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
                errors.add(new SemalyticalError(catchList, "return types must match"));
        }
        if (returnType == null)
            errors.add(new SemalyticalError(catchList, "must catch something"));
        return new ReturnBehavior(returnType);
    }

    private ReturnBehavior semalysizeCatchBody(LocalContext context, CatchBody catchBody)
    {
        LocalContext nestedContext = context.makeSubContext();
        semalysizeVariableDeclaration(nestedContext, catchBody.variableDeclaration);
        if (!catchBody.variableDeclaration.typeId.type.isInstanceOf(RuntimeType.getType(Throwable.class))) {
            errors.add(new SemalyticalError(catchBody.variableDeclaration, "Type must descend from Throwable. Can't catch a " + catchBody.variableDeclaration.typeId));
            catchBody.variableDeclaration.typeId.type = UnknownType.INSTANCE;
        }
        ReturnBehavior returnBehavior = semalysizeExpression(nestedContext, catchBody.expression);
        return new ReturnBehavior(returnBehavior.type);
    }

    private ReturnBehavior semalysizeAmbiguousFieldExpression(LocalContext context, Expression expression)
    {
        AmbiguousFieldExpression fieldExpresion = (AmbiguousFieldExpression)expression.content;
        if (fieldExpresion.leftExpression.content.getElementType() == AmbiguousId.TYPE)
            semalysizeAmbiguousId(context, fieldExpresion.leftExpression);
        switch (fieldExpresion.leftExpression.content.getElementType()) {
            case TypeId.TYPE: {
                // static field
                TypeId typeId = (TypeId)fieldExpresion.leftExpression.content;
                Field field = resolveField(typeId.type, fieldExpresion.fieldName.text);
                expression.content = new StaticFieldExpression(field);
                return new ReturnBehavior(field.returnType);
            }
            case AmbiguousId.TYPE: {
                // left side is unknown.
                return ReturnBehavior.UNKNOWN;
            }
            default: {
                // instance field
                Type declaringType = semalysizeExpression(context, fieldExpresion.leftExpression).type;
                Field field = resolveField(declaringType, fieldExpresion.fieldName.text);
                if (field == null) {
                    errors.add(SemalyticalError.cantResolveField(declaringType, fieldExpresion.fieldName));
                    return ReturnBehavior.UNKNOWN;
                }
                expression.content = new InstanceFieldExpression(fieldExpresion.leftExpression, field);
                return new ReturnBehavior(field.returnType);
            }
        }
    }
    
    private ReturnBehavior semalysizeAmbiguousMethodInvocation(LocalContext context, Expression expression)
    {
        AmbiguousMethodInvocation methodInvocation = (AmbiguousMethodInvocation)expression.content;
        if (methodInvocation.leftExpression.content.getElementType() != AmbiguousId.TYPE)
            semalysizeAmbiguousId(context, methodInvocation.leftExpression);
        else
            semalysizeExpression(context, methodInvocation.leftExpression);
        if (methodInvocation.leftExpression.content.getElementType() == TypeId.TYPE) {
            // static method invocation
            StaticMethodInvocation staticMethodInvocation = new StaticMethodInvocation((TypeId)methodInvocation.leftExpression.content, methodInvocation.methodName, methodInvocation.arguments);
            expression.content = staticMethodInvocation;
            return semalysizeStaticMethodInvocation(context, staticMethodInvocation);
        } else {
            InstanceMethodInvocation instanceMethodInvocation = new InstanceMethodInvocation(methodInvocation.leftExpression, methodInvocation.methodName, methodInvocation.arguments);
            return semalysizeInstanceMethodInvocation(context, instanceMethodInvocation);
        }
    }
    private ReturnBehavior semalysizeAmbiguousImplicitThisMethodInvocation(LocalContext context, Expression expression)
    {
        AmbiguousImplicitThisMethodInvocation methodInvocation = (AmbiguousImplicitThisMethodInvocation)expression.content;
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, methodInvocation.arguments);
        Method method = resolveMethod(context.getClassContext(), methodInvocation.methodName, argumentSignature);
        if (method.isStatic) {
            // implicit static ClassName.method()
            StaticMethodInvocation staticMethodInvocation = new StaticMethodInvocation(TypeId.fromType(context.getClassContext()), methodInvocation.methodName, methodInvocation.arguments);
            expression.content = staticMethodInvocation;
            return semalysizeStaticMethodInvocation(context, staticMethodInvocation);
        } else {
            // implicit instance this.method()
            InstanceMethodInvocation instanceMethodInvocation = new InstanceMethodInvocation(new Expression(ThisExpression.INSTANCE), methodInvocation.methodName, methodInvocation.arguments);
            expression.content = instanceMethodInvocation;
            return semalysizeInstanceMethodInvocation(context, instanceMethodInvocation);
        }
    }

    private ReturnBehavior semalysizeInstanceMethodInvocation(LocalContext context, InstanceMethodInvocation methodInvocation)
    {
        ReturnBehavior expressionReturnBehavior = semalysizeExpression(context, methodInvocation.leftExpression);
        ReturnBehavior[] argumentSignature = semalysizeArguments(context, methodInvocation.arguments);
        methodInvocation.method = resolveMethod(expressionReturnBehavior.type, methodInvocation.methodName, argumentSignature);
        implicitCastArguments(context, methodInvocation.arguments, methodInvocation.method.argumentSignature);
        return new ReturnBehavior(methodInvocation.method.returnType);
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
                // already semalysized
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
            errors.add(SemalyticalError.mustBeBoolean(ifThenElse.expression1));

        ifThenElse.label1 = context.nextLabel();
        semalysizeExpression(context, ifThenElse.expression2);

        ifThenElse.label2 = context.nextLabel();
        semalysizeExpression(context, ifThenElse.expression3);

        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new SemalyticalError(ifThenElse, "return types must match"));
        return new ReturnBehavior(ifThenElse.expression2.returnBehavior.type);
    }
    private ReturnBehavior semalysizeIfThen(LocalContext context, IfThen ifThen)
    {
        semalysizeExpression(context, ifThen.expression1);
        if (ifThen.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(SemalyticalError.mustBeBoolean(ifThen.expression1));
        ifThen.label = context.nextLabel();

        semalysizeExpression(context, ifThen.expression2);
        if (ifThen.expression2.returnBehavior.type != RuntimeType.VOID)
            errors.add(SemalyticalError.mustBeVoid(ifThen.expression2));

        return ReturnBehavior.VOID;
    }

    private ReturnBehavior semalysizeStaticFieldAssignment(LocalContext context, StaticFieldAssignment assignment)
    {
        semalysizeExpression(context, assignment.rightExpression);
        return semalysizeAbstractAssignment(context, assignment);
    }

    private ReturnBehavior semalysizeInstanceFieldAssignment(LocalContext context, InstanceFieldAssignment assignment)
    {
        semalysizeExpression(context, assignment.leftExpression);
        semalysizeExpression(context, assignment.rightExpression);
        return semalysizeAbstractAssignment(context, assignment);
    }

    private ReturnBehavior semalysizeAmbiguousAssignment(LocalContext context, Expression expression)
    {
        AmbiguousAssignment assignment = (AmbiguousAssignment)expression.content;
        semalysizeExpression(context, assignment.leftExpression);
        semalysizeExpression(context, assignment.rightExpression);
        switch (assignment.leftExpression.content.getElementType()) {
            case LocalVariableExpression.TYPE: {
                LocalVariableExpression localVariableExpression = (LocalVariableExpression)assignment.leftExpression.content;
                LocalVariableAssignment localVariableAssignment = new LocalVariableAssignment(localVariableExpression.variable, assignment.operator, assignment.rightExpression);
                expression.content = localVariableAssignment;
                return semalysizeAbstractAssignment(context, localVariableAssignment);
            }
            case InstanceFieldExpression.TYPE: {
                InstanceFieldExpression fieldExpression = (InstanceFieldExpression)assignment.leftExpression.content;
                InstanceFieldAssignment fieldAssignment = new InstanceFieldAssignment(fieldExpression.leftExpression, fieldExpression.field, assignment.operator, assignment.rightExpression);
                expression.content = fieldAssignment;
                return semalysizeAbstractAssignment(context, fieldAssignment);
            }
            case StaticFieldExpression.TYPE: {
                StaticFieldExpression fieldExpression = (StaticFieldExpression)assignment.leftExpression.content;
                StaticFieldAssignment fieldAssignment = new StaticFieldAssignment(fieldExpression.field, assignment.operator, assignment.rightExpression);
                expression.content = fieldAssignment;
                return semalysizeAbstractAssignment(context, fieldAssignment);
            }
            // TODO: array assignment
            default:
                errors.add(new SemalyticalError(assignment.leftExpression, "This expression is too complex to assign to"));
                return semalysizeExpression(context, assignment.rightExpression);
        }
    }

    private static final int 
    ASSIGNMENT_OPERATOR_TYPE_NUMERIC = 1,
    ASSIGNMENT_OPERATOR_TYPE_BOOLEAN = 2,
    ASSIGNMENT_OPERATOR_TYPE_NUMERIC_BOOLEAN = 3,
    ASSIGNMENT_OPERATOR_TYPE_REFERENCE = 4,
    ASSIGNMENT_OPERATOR_TYPE_ANY = 7;

    private static final HashMap<String, Integer> assignmentOperatorTypes = new HashMap<String, Integer>();
    static {
        assignmentOperatorTypes.put(Lang.SYMBOL_EQUALS, ASSIGNMENT_OPERATOR_TYPE_ANY);
        assignmentOperatorTypes.put(Lang.SYMBOL_PLUS_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_MINUS_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_ASTERISK_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_SLASH_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_PERCENT_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_AMPERSAND_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC_BOOLEAN);
        assignmentOperatorTypes.put(Lang.SYMBOL_CARET_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC_BOOLEAN);
        assignmentOperatorTypes.put(Lang.SYMBOL_PIPE_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC_BOOLEAN);
        assignmentOperatorTypes.put(Lang.SYMBOL_LESS_THAN_LESS_THAN_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_GREATER_THAN_GREATER_THAN_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
        assignmentOperatorTypes.put(Lang.SYMBOL_GREATER_THAN_GREATER_THAN_GREATER_THAN_EQUALS, ASSIGNMENT_OPERATOR_TYPE_NUMERIC);
    }
    private ReturnBehavior semalysizeAbstractAssignment(LocalContext context, AbstractAssignment assignment)
    {
        Type rightType = assignment.rightExpression.returnBehavior.type;
        Type leftType = assignment.getLeftType();
        if (assignment.operator == Lang.SYMBOL_EQUALS)
            implicitCast(context, assignment.rightExpression, leftType);
        int requiredOperatorType;
        if (!leftType.isPrimitive())
            requiredOperatorType = ASSIGNMENT_OPERATOR_TYPE_REFERENCE;
        else if (leftType == RuntimeType.BOOLEAN)
            requiredOperatorType = ASSIGNMENT_OPERATOR_TYPE_BOOLEAN;
        else if (leftType == RuntimeType.VOID) {
            // don't this will ever happen
            errors.add(new SemalyticalError(assignment, "Can't assign to type void"));
            return new ReturnBehavior(rightType);
        } else
            requiredOperatorType = ASSIGNMENT_OPERATOR_TYPE_NUMERIC;
        int actualOperatorType = assignmentOperatorTypes.get(assignment.operator);
        if ((actualOperatorType & requiredOperatorType) == 0) {
            String vowelA = getVowelA(leftType.simpleName);
            errors.add(new SemalyticalError(assignment, "Can't modify " + vowelA + " \"" + leftType.simpleName + "\" with this operator"));
        }
        return new ReturnBehavior(rightType);
    }

    /** lol */
    private static String getVowelA(String beforeThisText)
    {
        if (beforeThisText.equals(""))
            return "a";
        switch (beforeThisText.toLowerCase().charAt(0)) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return "an";
            default:
                return "a";
        }
    }

    private ReturnBehavior semalysizeAmbiguousId(LocalContext context, Expression expression)
    {
        AmbiguousId id = (AmbiguousId)expression.content;
        LocalVariable variable = resolveId(context, id.text);
        if (variable != null) {
            // it's a local variable
            expression.content = new LocalVariableExpression(variable);
            return new ReturnBehavior(variable.type);
        }
        Field field = resolveField(context.getClassContext(), id.text);
        if (field != null) {
            // it's a field of this class
            if (field.isStatic) {
                // it's a static field
                expression.content = new StaticFieldExpression(field);
            } else {
                // it's a non-static field
                Expression leftExpression = new Expression(ThisExpression.INSTANCE);
                semalysizeExpression(context, leftExpression);
                expression.content = new InstanceFieldExpression(leftExpression, field);
            }
            return new ReturnBehavior(field.returnType);
        }
        TypeId typeId = TypeId.fromName(id.text);
        if (resolveType(typeId, false)) {
            // it's a class name
            expression.content = typeId;
            // isn't a valid expression. we'll return void, even though it's not true.
            return ReturnBehavior.VOID;
        }
        errors.add(new SemalyticalError(id, "Can't resolve this identifier"));
        return ReturnBehavior.UNKNOWN;
    }

    private void implicitCast(LocalContext context, Expression expression, Type toType)
    {
        Type fromType = expression.returnBehavior.type;
        if (fromType == toType)
            return; // no need to cast
        boolean primitive = fromType.isPrimitive();
        if (primitive != toType.isPrimitive()) {
            errors.add(new SemalyticalError(expression, "Can't cast between primitives and non-primitives")); // TODO: code duplication
            return;
        }
        if (primitive) {
            if ((fromType == RuntimeType.BOOLEAN) != (toType == RuntimeType.BOOLEAN)) {
                errors.add(SemalyticalError.cantConvert(expression, fromType, toType));
                return;
            }
            switch (RuntimeType.getPrimitiveConversionType(fromType, toType)) {
                case -1:
                    errors.add(SemalyticalError.cantConvert(expression, fromType, toType));
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
                errors.add(SemalyticalError.cantConvert(expression, fromType, toType));
        }
        expression.returnBehavior = new ReturnBehavior(toType); // TODO this is overwriting a valid object at least sometimes
    }

    private ReturnBehavior semalysizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        if (!resolveType(variableDeclaration.typeId, true))
            errors.add(new SemalyticalError(variableDeclaration, "You can't have a void variable."));
        variableDeclaration.variable = context.addLocalVariable(variableDeclaration.variableName, variableDeclaration.typeId.type, errors);
        return ReturnBehavior.VOID;
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
        LocalContext localContext = context.makeSubContext();
        return semalysizeBlockContents(localContext, block.blockContents);
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
            expression.content = new InstanceMethodInvocation(string1, new AmbiguousId("concat"), new Arguments(Arrays.asList(string2)));
            return semalysizeExpression(context, expression);
        } else {
            return semalysizeNumericOperator(context, addition);
        }
    }
    private Expression stringValueOf(Expression expression)
    {
        return new Expression(new InstanceMethodInvocation(new Expression(new AmbiguousId("String")), new AmbiguousId("valueOf"), new Arguments(Arrays.asList(expression))));
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
        boolean good = true;

        Type returnType1 = lazySemalysizeExpression(context, operator.expression1).type;
        if (!returnType1.isNumeric()) {
            errors.add(SemalyticalError.mustBeNumeric(operator.expression1, returnType1));
            good = false;
        }

        Type returnType2 = lazySemalysizeExpression(context, operator.expression2).type;
        if (!returnType2.isNumeric()) {
            errors.add(SemalyticalError.mustBeNumeric(operator.expression2, returnType2));
            good = false;
        }

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
                errors.add(new SemalyticalError(operator, "operand types are incompatible."));
        } else {
            if (!returnType1.isPrimitive())
                errors.add(new SemalyticalError(operator.expression1, "operand can't be a reference type."));
            if (!returnType2.isPrimitive())
                errors.add(new SemalyticalError(operator.expression2, "operand can't be a reference type."));
        }
        returnType = returnType != null ? returnType : returnType1;
        return new ReturnBehavior(returnType);
    }

    private void resolveQualifiedName(QualifiedName qualifiedName)
    {
        String fullTypeName = qualifiedName.qualifiedName;
        String typeName = fullTypeName.substring(fullTypeName.lastIndexOf('.') + 1);
        try {
            // TODO: this is cheating
            Class<?> runtimeType = Class.forName(fullTypeName);
            importedTypes.put(typeName, RuntimeType.getType(runtimeType));
        } catch (ClassNotFoundException e) {
            errors.add(SemalyticalError.cantResolveImport(qualifiedName));
        }
    }

    /** @return success */
    private boolean resolveType(TypeId typeId, boolean errorOnFailure)
    {
        boolean failure = false;
        Type type = importedTypes.get(typeId.scalarTypeName.toString());
        if (type == null) {
            failure = true;
            type = UnknownType.INSTANCE;
        } else {
            int arrayOrder = typeId.arrayDimensions.elements.size();
            while (arrayOrder-- > 0)
                type = ArrayType.getType(type);
        }
        typeId.type = type;
        if (failure && errorOnFailure)
            errors.add(SemalyticalError.cantResolveType(typeId));
        return !failure;
    }

    private Method resolveMethod(Type type, AmbiguousId methodName, ReturnBehavior[] argumentSignature)
    {
        Method method = type.resolveMethod(methodName.text, getArgumentTypes(argumentSignature));
        if (method == null) {
            errors.add(SemalyticalError.cantResolveMethod(type, methodName, argumentSignature));
            return Method.UNKNOWN;
        }
        return method;
    }

    private Constructor resolveConstructor(Type type, ReturnBehavior[] argumentSignature)
    {
        return type.resolveConstructor(getArgumentTypes(argumentSignature));
    }

    private LocalVariable resolveId(LocalContext context, String name)
    {
        return context.getLocalVariable(name);
    }
    private Field resolveField(Type type, String name)
    {
        return type.resolveField(name);
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
