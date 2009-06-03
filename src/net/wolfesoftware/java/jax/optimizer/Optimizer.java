package net.wolfesoftware.java.jax.optimizer;

import net.wolfesoftware.java.jax.ast.*;

public class Optimizer
{
    private Optimizer()
    {
    }

    public static void optimize(Root root, OprimizationOptions options)
    {
        optimizeProgram(root.content);
    }

    private static void optimizeProgram(Program program)
    {
        for (TopLevelItem topLevelItem : program.elements)
            optimizeTopLevelItem(topLevelItem);
    }

    private static void optimizeTopLevelItem(TopLevelItem topLevelItem)
    {
        ParseElement content = topLevelItem.content;
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
            default:
                throw new RuntimeException(content.getClass().toString());
        }
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
