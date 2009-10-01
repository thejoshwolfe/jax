package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.parser.Parsing;

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
        lexiconizeCompilationUnit(root.content);

        return new Lexiconization(root, errors);
    }

    private void lexiconizeCompilationUnit(CompilationUnit compilationUnit)
    {
        lexiconizeImports(compilationUnit.imports);
        lexiconizeClassDeclaration(compilationUnit.classDeclaration);
    }

    private void lexiconizeImports(Imports imports)
    {
        // TODO Auto-generated method stub

    }

    private void lexiconizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        // verify package here
        // verify className and fileName match
        String classNameFromFile = filePath.substring(filePath.lastIndexOf('\\') + 1, filePath.lastIndexOf('.'));
        if (!classDeclaration.id.name.equals(classNameFromFile))
            errors.add(new LexicalException("Class name does not match file name"));
    
        LocalType context = new LocalType("", classDeclaration.id.name);
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
        lexiconizeType(functionDefinition.typeId);
        functionDefinition.context = new RootLocalContext(context);
        Type[] arguemntSignature = lexiconizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.method = new LocalMethod(functionDefinition.typeId.type, functionDefinition.id.name, arguemntSignature, true);
        context.addMethod(functionDefinition.method);
    }

    private Type[] lexiconizeArgumentDeclarations(LocalContext context, ArgumentDeclarations argumentDeclarations)
    {
        Type[] argumentSignature = new Type[argumentDeclarations.elements.size()];
        int i = 0;
        for (VariableDeclaration variableDeclaration : argumentDeclarations.elements)
            argumentSignature[i++] = lexiconizeVariableDeclaration(context, variableDeclaration).type;
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
            errors.add(new LexicalException());
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
            case FunctionInvocation.TYPE:
                returnBehavior = lexiconizeFunctionInvocation(context, (FunctionInvocation)content);
                break;
            case DereferenceMethod.TYPE:
                switch (disambuateDereferenceMethod(context, expression)) {
                    case DereferenceMethod.TYPE:
                        returnBehavior = lexiconizeDereferenceMethod(context, (DereferenceMethod)content);
                        break;
                    case StaticFunctionInvocation.TYPE:
                        // lexiconization is partially done
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
            case TryCatch.TYPE:
                returnBehavior = lexiconizeTryCatch(context, (TryCatch)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeStaticDereferenceField(LocalContext context, StaticDereferenceField staticDereferenceField)
    {
        // lexiconization already done for typeId
        staticDereferenceField.field = resolveField(staticDereferenceField.typeId.type, staticDereferenceField.id);
        return new ReturnBehavior(staticDereferenceField.field.returnType, 1);
    }

    private ReturnBehavior lexiconizeStaticFunctionInvocation(LocalContext context, StaticFunctionInvocation staticFunctionInvocation)
    {
        // lexiconization already done for typeId
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, staticFunctionInvocation.functionInvocation.arguments);
        staticFunctionInvocation.functionInvocation.method = resolveMethod(staticFunctionInvocation.typeId.type, staticFunctionInvocation.functionInvocation.id, argumentSignature);
        Type returnType = staticFunctionInvocation.functionInvocation.method.returnType;
        int stackRequirement = returnType != RuntimeType.VOID ? 1 : 0;
        stackRequirement = Math.max(stackRequirement, getStackRequirement(argumentSignature));
        return new ReturnBehavior(returnType, stackRequirement);
    }

    private ReturnBehavior lexiconizeTryCatch(LocalContext context, TryCatch tryCatch)
    {
        ReturnBehavior tryPartReturnBehavior = lexiconizeTryPart(context, tryCatch.tryPart);
        ReturnBehavior catchPartReturnBehavior = lexiconizeCatchPart(context, tryCatch.catchPart);
        if (tryPartReturnBehavior.type != catchPartReturnBehavior.type)
            errors.add(new LexicalException("return types must match"));
        return tryPartReturnBehavior.clone(catchPartReturnBehavior.stackRequirement);
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
        int stackRequirement = 0;
        Type returnType = null;
        for (CatchBody catchBody : catchList.elements) {
            ReturnBehavior returnBehavior = lexiconizeCatchBody(context, catchBody);
            stackRequirement = Math.max(stackRequirement, returnBehavior.stackRequirement);
            if (returnType == null)
                returnType = returnBehavior.type;
            else if (returnType != returnBehavior.type)
                errors.add(new LexicalException("return types must match"));
        }
        if (returnType == null)
            errors.add(new LexicalException("must catch something"));
        return new ReturnBehavior(returnType, stackRequirement);
    }

    private ReturnBehavior lexiconizeCatchBody(LocalContext context, CatchBody catchBody)
    {
        LocalContext nestedContext = new LocalContext(context);
        catchBody.startLabel = context.nextLabel();
        lexiconizeVariableDeclaration(nestedContext, catchBody.variableDeclaration);
        if (!catchBody.variableDeclaration.typeId.type.isInstanceOf(importedTypes.get("Throwable")))
            errors.add(new LexicalException("Type must descend from Throwable. Can't catch a " + catchBody.variableDeclaration.typeId));
        nestedContext.addLocalVariable(catchBody.variableDeclaration.id, catchBody.variableDeclaration.typeId.type, errors);
        ReturnBehavior returnBehavior = lexiconizeExpression(nestedContext, catchBody.expression);
        return returnBehavior.clone(1); // one for the exception object
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
        TypeId typeId = new TypeId(dereferenceField.expression.content);
        lexiconizeType(typeId);
        if (typeId.type != null) {
            // convert to StaticDereferenceField
            expression.content = new StaticDereferenceField(typeId, dereferenceField.id);
            return StaticDereferenceField.TYPE;
        }
        throw new RuntimeException(); // TODO what do we do here?
    }

    private ReturnBehavior lexiconizeDereferenceField(LocalContext context, DereferenceField dereferenceField)
    {
        throw new RuntimeException();
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
        TypeId typeId = new TypeId(dereferenceMethod.expression.content);
        lexiconizeType(typeId);
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
        int stackRequirement = expressionReturnBehavior.stackRequirement;
        stackRequirement = Math.max(stackRequirement, 1 + getStackRequirement(argumentSignature));
        return new ReturnBehavior(dereferenceMethod.functionInvocation.method.returnType, stackRequirement);
    }

    private ReturnBehavior lexiconizeFunctionInvocation(LocalContext context, FunctionInvocation functionInvocation)
    {
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, functionInvocation.arguments);
        functionInvocation.method = resolveFunction(context.getClassContext(), functionInvocation.id, argumentSignature);
        int stackRequirement = functionInvocation.method.returnType == RuntimeType.VOID ? 0 : 1;
        stackRequirement = Math.max(stackRequirement, getStackRequirement(argumentSignature));
        return new ReturnBehavior(functionInvocation.method.returnType, stackRequirement);
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
            errors.add(new LexicalException());
        ifThenElse.label1 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression2);
        ifThenElse.label2 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression3);
        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new LexicalException("return types must match"));
        return ifThenElse.expression2.returnBehavior.clone(Math.max(ifThenElse.expression1.returnBehavior.stackRequirement, ifThenElse.expression3.returnBehavior.stackRequirement));
    }
    private ReturnBehavior lexiconizeIfThen(LocalContext context, IfThen ifThen)
    {
        lexiconizeExpression(context, ifThen.expression1);
        if (ifThen.expression1.returnBehavior.type != RuntimeType.BOOLEAN)
            errors.add(new LexicalException());
        ifThen.label = context.nextLabel();
        lexiconizeExpression(context, ifThen.expression2);
        if (ifThen.expression2.returnBehavior.type != RuntimeType.VOID)
            errors.add(new LexicalException("return type must be void"));
        return new ReturnBehavior(RuntimeType.VOID, Math.max(ifThen.expression2.returnBehavior.stackRequirement, ifThen.expression1.returnBehavior.stackRequirement));
    }

    private ReturnBehavior lexiconizeAssignment(LocalContext context, Assignment assignment)
    {
        assignment.id.variable = context.getLocalVariable(assignment.id.name);
        if (assignment.id.variable == null)
            errors.add(new LexicalException());
        ReturnBehavior returnBehavior = lexiconizeExpression(context, assignment.expression);
        if (assignment.id.variable != null && assignment.id.variable.type != returnBehavior.type)
            errors.add(new LexicalException());
        return returnBehavior.clone(2);
    }

    private ReturnBehavior lexiconizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        lexiconizeType(variableDeclaration.typeId);
        if (variableDeclaration.typeId.type == null)
            errors.add(new LexicalException("Dunno what this type is: " + variableDeclaration.typeId));
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeId(LocalContext context, Id id)
    {
        id.variable = resolveId(context, id);
        if (id.variable == null) {
            errors.add(new LexicalException());
            // TODO: return something
        }
        return new ReturnBehavior(id.variable.type, 1);
    }

    private ReturnBehavior lexiconizeVariableCreation(LocalContext context, VariableCreation variableCreation)
    {
        lexiconizeVariableDeclaration(context, variableCreation.variableDeclaration);
        ReturnBehavior returnBehavior = lexiconizeExpression(context, variableCreation.expression);
        if (variableCreation.variableDeclaration.typeId.type != returnBehavior.type)
            errors.add(new LexicalException());
        return new ReturnBehavior(RuntimeType.VOID, returnBehavior.stackRequirement);
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
        int stackRequirement = 0;
        for (Expression element : blockContents.elements) {
            ReturnBehavior returnBehavior = lexiconizeExpression(context, element);
            returnType = returnBehavior.type;
            stackRequirement = Math.max(stackRequirement, returnBehavior.stackRequirement);
            VariableDeclaration variableDeclaration;
            switch (element.content.getElementType()) {
                case VariableDeclaration.TYPE:
                    variableDeclaration = (VariableDeclaration)element.content;
                    break;
                case VariableCreation.TYPE:
                    variableDeclaration = ((VariableCreation)element.content).variableDeclaration;
                    break;
                default:
                    variableDeclaration = null;
            }
            if (variableDeclaration != null) {
                context.addLocalVariable(variableDeclaration.id, variableDeclaration.typeId.type, errors);
            }
        }
        if (blockContents.forceVoid)
            returnType = RuntimeType.VOID;
        return new ReturnBehavior(returnType, stackRequirement);
    }

    private ReturnBehavior lexiconizeQuantity(LocalContext context, Quantity quantity)
    {
        return lexiconizeExpression(context, quantity.expression);
    }

    private ReturnBehavior lexiconizeIntLiteral(LocalContext context, IntLiteral intLiteral)
    {
        return new ReturnBehavior(RuntimeType.INT, 1);
    }
    private ReturnBehavior lexiconizeBooleanLiteral(LocalContext context, BooleanLiteral booleanLiteral)
    {
        return new ReturnBehavior(RuntimeType.BOOLEAN, 1);
    }
    private ReturnBehavior lexiconizeStringLiteral(LocalContext context, StringLiteral stringLiteral)
    {
        return new ReturnBehavior(importedTypes.get("String"), 1);
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
            errors.add(new LexicalException());
        return returnBehavior;
    }
    private ReturnBehavior lexiconizeInequality(LocalContext context, Inequality inequality)
    {
        return lexiconizeComparisonOperator(context, inequality);
    }
    private ReturnBehavior lexiconizeEquality(LocalContext context, Equality equality)
    {
        return lexiconizeComparisonOperator(context, equality);
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
            errors.add(new LexicalException());
        int stackRequirement = Math.max(returnBehavior1.stackRequirement, returnBehavior2.stackRequirement + 1);
        return new ReturnBehavior(returnType != null ? returnType : returnBehavior1.type, stackRequirement);
    }

    private void lexiconizeType(TypeId typeId)
    {
        typeId.type = importedTypes.get(typeId.toString());
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

    private static int getStackRequirement(ReturnBehavior[] argumentSignature)
    {
        int stackRequirement = 0;
        for (int i = 0; i < argumentSignature.length; i++)
            stackRequirement = Math.max(stackRequirement, i + argumentSignature[i].stackRequirement);
        return stackRequirement;
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
