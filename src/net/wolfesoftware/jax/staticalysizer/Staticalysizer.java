package net.wolfesoftware.jax.staticalysizer;

import net.wolfesoftware.jax.ast.*;

public class Staticalysizer
{
    private Staticalysizer()
    {
    }

    public static void staticalysize(Root root)
    {
        staticalysizeCompilationUnit(root.content);
    }

    private static void staticalysizeCompilationUnit(CompilationUnit program)
    {
        staticalysizeImports(program.imports);
        staticalysizeClassDeclaration(program.classDeclaration);
    }

    private static void staticalysizeImports(Imports imports)
    {
    }

    private static void staticalysizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        staticalysizeId(classDeclaration.id);
        staticalysizeClassBody(classDeclaration.classBody);
    }

    private static void staticalysizeClassBody(ClassBody program)
    {
        for (ClassMember classMember : program.elements)
            staticalysizeClassMember(classMember);
    }

    private static void staticalysizeClassMember(ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                staticalysizeFunctionDefinition((FunctionDefinition)content);
                break;
            case ConstructorDefinition.TYPE:
                staticalysizeConstructorDefinition((ConstructorDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private static void staticalysizeConstructorDefinition(ConstructorDefinition constructorDefinition)
    {
        staticalysizeExpression(constructorDefinition.expression);
    }

    private static void staticalysizeFunctionDefinition(FunctionDefinition functionDefinition)
    {
        staticalysizeExpression(functionDefinition.expression);
    }

    private static void staticalysizeExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType())
        {
            case Addition.TYPE:
                staticalysizeAddition((Addition)content);
                break;
            case Subtraction.TYPE:
                staticalysizeSubtraction((Subtraction)content);
                break;
            case Multiplication.TYPE:
                staticalysizeMultiplication((Multiplication)content);
                break;
            case Division.TYPE:
                staticalysizeDivision((Division)content);
                break;
            case PostIncrement.TYPE:
                staticalysizePostIncrement((PostIncrement)content);
                break;
            case PreIncrement.TYPE:
                staticalysizePreIncrement((PreIncrement)content);
                break;
            case PostDecrement.TYPE:
                staticalysizePostDecrement((PostDecrement)content);
                break;
            case PreDecrement.TYPE:
                staticalysizePreDecrement((PreDecrement)content);
                break;
            case LessThan.TYPE:
                staticalysizeLessThan((LessThan)content);
                break;
            case GreaterThan.TYPE:
                staticalysizeGreaterThan((GreaterThan)content);
                break;
            case LessThanOrEqual.TYPE:
                staticalysizeLessThanOrEqual((LessThanOrEqual)content);
                break;
            case GreaterThanOrEqual.TYPE:
                staticalysizeGreaterThanOrEqual((GreaterThanOrEqual)content);
                break;
            case Equality.TYPE:
                staticalysizeEquality((Equality)content);
                break;
            case Inequality.TYPE:
                staticalysizeInequality((Inequality)content);
                break;
            case ShortCircuitAnd.TYPE:
                staticalysizeShortCircuitAnd((ShortCircuitAnd)content);
                break;
            case ShortCircuitOr.TYPE:
                staticalysizeShortCircuitOr((ShortCircuitOr)content);
                break;
            case Negation.TYPE:
                staticalysizeNegation((Negation)content);
                break;
            case BooleanNot.TYPE:
                staticalysizeBooleanNot((BooleanNot)content);
                break;
            case Id.TYPE:
                staticalysizeId((Id)content);
                break;
            case Block.TYPE:
                staticalysizeBlock((Block)content);
                break;
            case IntLiteral.TYPE:
                staticalysizeIntLiteral((IntLiteral)content);
                break;
            case LongLiteral.TYPE:
                staticalysizeLongLiteral((LongLiteral)content);
                break;
            case FloatLiteral.TYPE:
                staticalysizeFloatLiteral((FloatLiteral)content);
                break;
            case DoubleLiteral.TYPE:
                staticalysizeDoubleLiteral((DoubleLiteral)content);
                break;
            case BooleanLiteral.TYPE:
                staticalysizeBooleanLiteral((BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                staticalysizeStringLiteral((StringLiteral)content);
                break;
            case Quantity.TYPE:
                staticalysizeQuantity((Quantity)content);
                break;
            case VariableCreation.TYPE:
                staticalysizeVariableCreation((VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                staticalysizeVariableDeclaration((VariableDeclaration)content);
                break;
            case Assignment.TYPE:
                staticalysizeAssignment((Assignment)content);
                break;
            case IfThenElse.TYPE:
                staticalysizeIfThenElse((IfThenElse)content);
                break;
            case IfThen.TYPE:
                staticalysizeIfThen((IfThen)content);
                break;
            case ForLoop.TYPE:
                staticalysizeForLoop((ForLoop)content);
                break;
            case WhileLoop.TYPE:
                staticalysizeWhileLoop((WhileLoop)content);
                break;
            case FunctionInvocation.TYPE:
                staticalysizeFunctionInvocation((FunctionInvocation)content);
                break;
            case ConstructorInvocation.TYPE:
                staticalysizeConstructorInvocation((ConstructorInvocation)content);
                break;
            case ConstructorRedirect.TYPE:
                staticalysizeConstructorRedirect((ConstructorRedirect)content);
                break;
            case DereferenceMethod.TYPE:
                staticalysizeDereferenceMethod((DereferenceMethod)content);
                break;
            case DereferenceField.TYPE:
                staticalysizeDereferenceField((DereferenceField)content);
                break;
            case StaticDereferenceField.TYPE:
                staticalysizeStaticDereferenceField((StaticDereferenceField)content);
                break;
            case ArrayDereference.TYPE:
                staticalysizeArrayDereference((ArrayDereference)content);
                break;
            case TryCatch.TYPE:
                staticalysizeTryCatch((TryCatch)content);
                break;
            case StaticFunctionInvocation.TYPE:
                staticalysizeStaticFunctionInvocation((StaticFunctionInvocation)content);
                break;
            case PrimitiveConversion.TYPE:
                staticalysizePrimitiveConversion((PrimitiveConversion)content);
                break;
            case ReferenceConversion.TYPE:
                staticalysizeReferenceConversion((ReferenceConversion)content);
                break;
            case NullExpression.TYPE:
                staticalysizeNullExpression((NullExpression)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private static void staticalysizeConstructorRedirect(ConstructorRedirect constructorRedirect)
    {
        staticalysizeArguments(constructorRedirect.arguments);
    }

    private static void staticalysizeNullExpression(NullExpression nullExpression)
    {
        // do nothing
    }

    private static void staticalysizeShortCircuitAnd(ShortCircuitAnd shortCircuitAnd)
    {
        staticalysizeExpression(shortCircuitAnd.expression1);
        staticalysizeExpression(shortCircuitAnd.expression2);
    }

    private static void staticalysizeShortCircuitOr(ShortCircuitOr shortCircuitOr)
    {
        staticalysizeExpression(shortCircuitOr.expression1);
        staticalysizeExpression(shortCircuitOr.expression2);
    }

    private static void staticalysizeBooleanNot(BooleanNot booleanNot)
    {
        staticalysizeExpression(booleanNot.expression);
    }

    private static void staticalysizeNegation(Negation negation)
    {
        staticalysizeExpression(negation.expression);
    }

    private static void staticalysizeReferenceConversion(ReferenceConversion referenceConversion)
    {
        staticalysizeExpression(referenceConversion.expression);
    }

    private static void staticalysizePrimitiveConversion(PrimitiveConversion primitiveConversion)
    {
        staticalysizeExpression(primitiveConversion.expression);
    }

    private static void staticalysizeWhileLoop(WhileLoop whileLoop)
    {
        staticalysizeExpression(whileLoop.expression1);
        staticalysizeExpression(whileLoop.expression2);
    }

    private static void staticalysizeConstructorInvocation(ConstructorInvocation constructorInvocation)
    {
        staticalysizeArguments(constructorInvocation.functionInvocation.arguments);
    }

    private static void staticalysizePreDecrement(PreDecrement preDecrement)
    {
        staticalysizeIncrementDecrement(preDecrement);
    }
    private static void staticalysizePostDecrement(PostDecrement postDecrement)
    {
        staticalysizeIncrementDecrement(postDecrement);
    }
    private static void staticalysizePreIncrement(PreIncrement preDecrement)
    {
        staticalysizeIncrementDecrement(preDecrement);
    }
    private static void staticalysizePostIncrement(PostIncrement postIncrement)
    {
        staticalysizeIncrementDecrement(postIncrement);
    }
    private static void staticalysizeIncrementDecrement(IncrementDecrement incrementDecrement)
    {
        staticalysizeId(incrementDecrement.id);
    }

    private static void staticalysizeForLoop(ForLoop forLoop)
    {
        staticalysizeExpression(forLoop.expression1);
        staticalysizeExpression(forLoop.expression2);
        staticalysizeExpression(forLoop.expression3);
        staticalysizeExpression(forLoop.expression4);
    }

    private static void staticalysizeArrayDereference(ArrayDereference arrayDereference)
    {
        staticalysizeExpression(arrayDereference.expression1);
        staticalysizeExpression(arrayDereference.expression2);
    }

    private static void staticalysizeStaticFunctionInvocation(StaticFunctionInvocation staticFunctionInvocation)
    {
        staticalysizeFunctionInvocation(staticFunctionInvocation.functionInvocation);
    }

    private static void staticalysizeTryCatch(TryCatch tryCatch)
    {
        staticalysizeTryPart(tryCatch.tryPart);
        staticalysizeCatchPart(tryCatch.catchPart);
    }

    private static void staticalysizeCatchPart(CatchPart catchPart)
    {
        staticalysizeCatchList(catchPart.catchList);
    }

    private static void staticalysizeCatchList(CatchList catchList)
    {
        for (CatchBody catchBody : catchList.elements)
            staticalysizeCatchBody(catchBody);
    }

    private static void staticalysizeCatchBody(CatchBody catchBody)
    {
        staticalysizeVariableDeclaration(catchBody.variableDeclaration);
        staticalysizeExpression(catchBody.expression);
    }

    private static void staticalysizeTryPart(TryPart tryPart)
    {
        staticalysizeExpression(tryPart.expression);
    }

    private static void staticalysizeStaticDereferenceField(StaticDereferenceField staticDereferenceField)
    {
        // do nothing
    }

    private static void staticalysizeDereferenceMethod(DereferenceMethod dereferenceMethod)
    {
        staticalysizeExpression(dereferenceMethod.expression);
        staticalysizeFunctionInvocation(dereferenceMethod.functionInvocation);
    }

    private static void staticalysizeDereferenceField(DereferenceField dereferenceField)
    {
        staticalysizeExpression(dereferenceField.expression);
    }

    private static void staticalysizeFunctionInvocation(FunctionInvocation functionInvocation)
    {
        staticalysizeId(functionInvocation.id);
        staticalysizeArguments(functionInvocation.arguments);
    }

    private static void staticalysizeArguments(Arguments arguments)
    {
        for (Expression element : arguments.elements)
            staticalysizeExpression(element);
    }

    private static void staticalysizeIfThenElse(IfThenElse ifThenElse)
    {
        staticalysizeExpression(ifThenElse.expression1);
        staticalysizeExpression(ifThenElse.expression2);
        staticalysizeExpression(ifThenElse.expression3);
    }
    private static void staticalysizeIfThen(IfThen ifThen)
    {
        staticalysizeExpression(ifThen.expression1);
        staticalysizeExpression(ifThen.expression2);
    }

    private static void staticalysizeAssignment(Assignment assignment)
    {
        staticalysizeId(assignment.id);
        staticalysizeExpression(assignment.expression);
    }


    private static void staticalysizeVariableDeclaration(VariableDeclaration variableDeclaration)
    {
    }

    private static void staticalysizeVariableCreation(VariableCreation variableCreation)
    {
        staticalysizeExpression(variableCreation.expression);
    }

    private static void staticalysizeId(Id id)
    {
    }

    private static void staticalysizeQuantity(Quantity quantity)
    {
        staticalysizeExpression(quantity.expression);
    }

    private static void staticalysizeIntLiteral(IntLiteral intLiteral)
    {
        // do nothing
    }
    private static void staticalysizeLongLiteral(LongLiteral content)
    {
        // do nothing
    }
    private static void staticalysizeFloatLiteral(FloatLiteral floatLiteral)
    {
        // do nothing
    }
    private static void staticalysizeDoubleLiteral(DoubleLiteral doubleLiteral)
    {
        // do nothing
    }
    private static void staticalysizeBooleanLiteral(BooleanLiteral intLiteral)
    {
        // do nothing
    }
    private static void staticalysizeStringLiteral(StringLiteral intLiteral)
    {
        // do nothing
    }

    private static void staticalysizeBlock(Block block)
    {
        staticalysizeBlockContents(block.blockContents);
    }

    private static void staticalysizeBlockContents(BlockContents blockContents)
    {
        for (Expression element : blockContents.elements)
            staticalysizeExpression(element);
    }

    private static void staticalysizeAddition(Addition addition)
    {
        staticalysizeBinaryOperator(addition);
    }
    private static void staticalysizeSubtraction(Subtraction subtraction)
    {
        staticalysizeBinaryOperator(subtraction);
    }
    private static void staticalysizeMultiplication(Multiplication multiplication)
    {
        staticalysizeBinaryOperator(multiplication);
    }
    private static void staticalysizeDivision(Division division)
    {
        staticalysizeBinaryOperator(division);
    }
    private static void staticalysizeLessThan(LessThan lessThan)
    {
        staticalysizeBinaryOperator(lessThan);
    }
    private static void staticalysizeGreaterThan(GreaterThan greaterThan)
    {
        staticalysizeBinaryOperator(greaterThan);
    }
    private static void staticalysizeLessThanOrEqual(LessThanOrEqual lessThanOrEqual)
    {
        staticalysizeBinaryOperator(lessThanOrEqual);
    }
    private static void staticalysizeGreaterThanOrEqual(GreaterThanOrEqual greaterThanOrEqual)
    {
        staticalysizeBinaryOperator(greaterThanOrEqual);
    }
    private static void staticalysizeEquality(Equality equality)
    {
        staticalysizeBinaryOperator(equality);
    }
    private static void staticalysizeInequality(Inequality inequality)
    {
        staticalysizeBinaryOperator(inequality);
    }
    private static void staticalysizeBinaryOperator(BinaryOperatorElement operator)
    {
        staticalysizeExpression(operator.expression1);
        staticalysizeExpression(operator.expression2);
    }
}
