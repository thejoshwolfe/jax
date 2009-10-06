package net.wolfesoftware.java.jax.optimizer;

import net.wolfesoftware.java.jax.ast.*;

public class Optimizer
{
    private Optimizer()
    {
    }

    public static void optimize(Root root, OprimizationOptions options)
    {
        optimizeCompilationUnit(root.content);
    }

    private static void optimizeCompilationUnit(CompilationUnit program)
    {
        optimizeImports(program.imports);
        optimizeClassDeclaration(program.classDeclaration);
    }

    private static void optimizeImports(Imports imports)
    {
    }

    private static void optimizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        optimizeId(classDeclaration.id);
        optimizeClassBody(classDeclaration.classBody);
    }

    private static void optimizeClassBody(ClassBody program)
    {
        for (ClassMember classMember : program.elements)
            optimizeClassMember(classMember);
    }

    private static void optimizeClassMember(ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                optimizeFunctionDefinition((FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private static void optimizeFunctionDefinition(FunctionDefinition functionDefinition)
    {
        optimizeExpression(functionDefinition.expression);
    }

    private static void optimizeExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType())
        {
            case Addition.TYPE:
                optimizeAddition((Addition)content);
                break;
            case Subtraction.TYPE:
                optimizeSubtraction((Subtraction)content);
                break;
            case Multiplication.TYPE:
                optimizeMultiplication((Multiplication)content);
                break;
            case Division.TYPE:
                optimizeDivision((Division)content);
                break;
            case LessThan.TYPE:
                optimizeLessThan((LessThan)content);
                break;
            case GreaterThan.TYPE:
                optimizeGreaterThan((GreaterThan)content);
                break;
            case LessThanOrEqual.TYPE:
                optimizeLessThanOrEqual((LessThanOrEqual)content);
                break;
            case GreaterThanOrEqual.TYPE:
                optimizeGreaterThanOrEqual((GreaterThanOrEqual)content);
                break;
            case Equality.TYPE:
                optimizeEquality((Equality)content);
                break;
            case Inequality.TYPE:
                optimizeInequality((Inequality)content);
                break;
            case Id.TYPE:
                optimizeId((Id)content);
                break;
            case Block.TYPE:
                optimizeBlock((Block)content);
                break;
            case IntLiteral.TYPE:
                optimizeIntLiteral((IntLiteral)content);
                break;
            case BooleanLiteral.TYPE:
                optimizeBooleanLiteral((BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                optimizeStringLiteral((StringLiteral)content);
                break;
            case Quantity.TYPE:
                optimizeQuantity((Quantity)content);
                break;
            case VariableCreation.TYPE:
                optimizeVariableCreation((VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                optimizeVariableDeclaration((VariableDeclaration)content);
                break;
            case Assignment.TYPE:
                optimizeAssignment((Assignment)content);
                break;
            case IfThenElse.TYPE:
                optimizeIfThenElse((IfThenElse)content);
                break;
            case IfThen.TYPE:
                optimizeIfThen((IfThen)content);
                break;
            case FunctionInvocation.TYPE:
                optimizeFunctionInvocation((FunctionInvocation)content);
                break;
            case DereferenceMethod.TYPE:
                optimizeDereferenceMethod((DereferenceMethod)content);
                break;
            case StaticDereferenceField.TYPE:
                optimizeStaticDereferenceField((StaticDereferenceField)content);
                break;
            case TryCatch.TYPE:
                optimizeTryCatch((TryCatch)content);
                break;
            case StaticFunctionInvocation.TYPE:
                optimizeStaticFunctionInvocation((StaticFunctionInvocation)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private static void optimizeStaticFunctionInvocation(StaticFunctionInvocation staticFunctionInvocation)
    {
        optimizeFunctionInvocation(staticFunctionInvocation.functionInvocation);
    }

    private static void optimizeTryCatch(TryCatch tryCatch)
    {
        optimizeTryPart(tryCatch.tryPart);
        optimizeCatchPart(tryCatch.catchPart);
    }

    private static void optimizeCatchPart(CatchPart catchPart)
    {
        optimizeCatchList(catchPart.catchList);
    }

    private static void optimizeCatchList(CatchList catchList)
    {
        for (CatchBody catchBody : catchList.elements)
            optimizeCatchBody(catchBody);
    }

    private static void optimizeCatchBody(CatchBody catchBody)
    {
        optimizeVariableDeclaration(catchBody.variableDeclaration);
        optimizeExpression(catchBody.expression);
    }

    private static void optimizeTryPart(TryPart tryPart)
    {
        optimizeExpression(tryPart.expression);
    }

    private static void optimizeStaticDereferenceField(StaticDereferenceField staticDereferenceField)
    {
        // do nothing
    }

    private static void optimizeDereferenceMethod(DereferenceMethod dereferenceMethod)
    {
        optimizeExpression(dereferenceMethod.expression);
        optimizeFunctionInvocation(dereferenceMethod.functionInvocation);
    }

    private static void optimizeFunctionInvocation(FunctionInvocation functionInvocation)
    {
        optimizeId(functionInvocation.id);
        optimizeArguments(functionInvocation.arguments);
    }

    private static void optimizeArguments(Arguments arguments)
    {
        for (Expression element : arguments.elements)
            optimizeExpression(element);
    }

    private static void optimizeIfThenElse(IfThenElse ifThenElse)
    {
        optimizeExpression(ifThenElse.expression1);
        optimizeExpression(ifThenElse.expression2);
        optimizeExpression(ifThenElse.expression3);
    }
    private static void optimizeIfThen(IfThen ifThen)
    {
        optimizeExpression(ifThen.expression1);
        optimizeExpression(ifThen.expression2);
    }

    private static void optimizeAssignment(Assignment assignment)
    {
        optimizeId(assignment.id);
        optimizeExpression(assignment.expression);
    }


    private static void optimizeVariableDeclaration(VariableDeclaration variableDeclaration)
    {
    }

    private static void optimizeVariableCreation(VariableCreation variableCreation)
    {
        optimizeExpression(variableCreation.expression);
    }

    private static void optimizeId(Id id)
    {
    }

    private static void optimizeQuantity(Quantity quantity)
    {
        optimizeExpression(quantity.expression);
    }

    private static void optimizeIntLiteral(IntLiteral intLiteral)
    {
        // do nothing
    }
    private static void optimizeBooleanLiteral(BooleanLiteral intLiteral)
    {
        // do nothing
    }
    private static void optimizeStringLiteral(StringLiteral intLiteral)
    {
        // do nothing
    }

    private static void optimizeBlock(Block block)
    {
        optimizeBlockContents(block.blockContents);
    }

    private static void optimizeBlockContents(BlockContents blockContents)
    {
        for (Expression element : blockContents.elements)
            optimizeExpression(element);
    }

    private static void optimizeAddition(Addition addition)
    {
        optimizeBinaryOperator(addition);
    }
    private static void optimizeSubtraction(Subtraction subtraction)
    {
        optimizeBinaryOperator(subtraction);
    }
    private static void optimizeMultiplication(Multiplication multiplication)
    {
        optimizeBinaryOperator(multiplication);
    }
    private static void optimizeDivision(Division division)
    {
        optimizeBinaryOperator(division);
    }
    private static void optimizeLessThan(LessThan lessThan)
    {
        optimizeBinaryOperator(lessThan);
    }
    private static void optimizeGreaterThan(GreaterThan greaterThan)
    {
        optimizeBinaryOperator(greaterThan);
    }
    private static void optimizeLessThanOrEqual(LessThanOrEqual lessThanOrEqual)
    {
        optimizeBinaryOperator(lessThanOrEqual);
    }
    private static void optimizeGreaterThanOrEqual(GreaterThanOrEqual greaterThanOrEqual)
    {
        optimizeBinaryOperator(greaterThanOrEqual);
    }
    private static void optimizeEquality(Equality equality)
    {
        optimizeBinaryOperator(equality);
    }
    private static void optimizeInequality(Inequality inequality)
    {
        optimizeBinaryOperator(inequality);
    }
    private static void optimizeBinaryOperator(BinaryOperatorElement operator)
    {
        optimizeExpression(operator.expression1);
        optimizeExpression(operator.expression2);
    }
}
