package net.wolfesoftware.jax.staticalysis;

import net.wolfesoftware.jax.JaxcOptions;
import net.wolfesoftware.jax.ast.*;

public class Staticalysizer
{
    public static void staticalysize(Root root, JaxcOptions options)
    {
        new Staticalysizer(root, options).staticalysize();
    }

    private final Root root;
    private final JaxcOptions options;
    private Staticalysizer(Root root, JaxcOptions options)
    {
        this.root = root;
        this.options = options;
    }

    private void staticalysize()
    {
        staticalysizeCompilationUnit(root.content);
    }

    private void staticalysizeCompilationUnit(CompilationUnit program)
    {
        staticalysizeImports(program.imports);
        staticalysizeClassDeclaration(program.classDeclaration);
    }

    private void staticalysizeImports(Imports imports)
    {
    }

    private void staticalysizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        staticalysizeId(classDeclaration.id);
        staticalysizeClassBody(classDeclaration.classBody);
    }

    private void staticalysizeClassBody(ClassBody program)
    {
        for (ClassMember classMember : program.elements)
            staticalysizeClassMember(classMember);
    }

    private void staticalysizeClassMember(ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType()) {
            case MethodDeclaration.TYPE:
                staticalysizeMethodDeclaration((MethodDeclaration)content);
                break;
            case ConstructorDeclaration.TYPE:
                staticalysizeConstructorDeclaration((ConstructorDeclaration)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void staticalysizeConstructorDeclaration(ConstructorDeclaration constructorDeclaration)
    {
        staticalysizeExpression(constructorDeclaration.expression);
    }

    private void staticalysizeMethodDeclaration(MethodDeclaration methodDeclaration)
    {
        staticalysizeExpression(methodDeclaration.expression);
    }

    private void staticalysizeExpression(Expression expression)
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
            case IdAssignment.TYPE:
                staticalysizeAssignment((IdAssignment)content);
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
            case MethodInvocation.TYPE:
                staticalysizeMethodInvocation((MethodInvocation)content);
                break;
            case ConstructorInvocation.TYPE:
                staticalysizeConstructorInvocation((ConstructorInvocation)content);
                break;
            case ConstructorRedirectThis.TYPE:
                staticalysizeConstructorRedirect((ConstructorRedirectThis)content);
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
            case StaticMethodInvocation.TYPE:
                staticalysizeStaticMethodInvocation((StaticMethodInvocation)content);
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

    private void staticalysizeConstructorRedirect(ConstructorRedirectThis constructorRedirect)
    {
        staticalysizeArguments(constructorRedirect.arguments);
    }

    private void staticalysizeNullExpression(NullExpression nullExpression)
    {
        // do nothing
    }

    private void staticalysizeShortCircuitAnd(ShortCircuitAnd shortCircuitAnd)
    {
        staticalysizeExpression(shortCircuitAnd.expression1);
        staticalysizeExpression(shortCircuitAnd.expression2);
    }

    private void staticalysizeShortCircuitOr(ShortCircuitOr shortCircuitOr)
    {
        staticalysizeExpression(shortCircuitOr.expression1);
        staticalysizeExpression(shortCircuitOr.expression2);
    }

    private void staticalysizeBooleanNot(BooleanNot booleanNot)
    {
        staticalysizeExpression(booleanNot.expression);
    }

    private void staticalysizeNegation(Negation negation)
    {
        staticalysizeExpression(negation.expression);
    }

    private void staticalysizeReferenceConversion(ReferenceConversion referenceConversion)
    {
        staticalysizeExpression(referenceConversion.expression);
    }

    private void staticalysizePrimitiveConversion(PrimitiveConversion primitiveConversion)
    {
        staticalysizeExpression(primitiveConversion.expression);
    }

    private void staticalysizeWhileLoop(WhileLoop whileLoop)
    {
        staticalysizeExpression(whileLoop.expression1);
        staticalysizeExpression(whileLoop.expression2);
    }

    private void staticalysizeConstructorInvocation(ConstructorInvocation constructorInvocation)
    {
        staticalysizeArguments(constructorInvocation.methodInvocation.arguments);
    }

    private void staticalysizePreDecrement(PreDecrement preDecrement)
    {
        staticalysizeIncrementDecrement(preDecrement);
    }
    private void staticalysizePostDecrement(PostDecrement postDecrement)
    {
        staticalysizeIncrementDecrement(postDecrement);
    }
    private void staticalysizePreIncrement(PreIncrement preDecrement)
    {
        staticalysizeIncrementDecrement(preDecrement);
    }
    private void staticalysizePostIncrement(PostIncrement postIncrement)
    {
        staticalysizeIncrementDecrement(postIncrement);
    }
    private void staticalysizeIncrementDecrement(IncrementDecrement incrementDecrement)
    {
        staticalysizeId(incrementDecrement.id);
    }

    private void staticalysizeForLoop(ForLoop forLoop)
    {
        staticalysizeExpression(forLoop.expression1);
        staticalysizeExpression(forLoop.expression2);
        staticalysizeExpression(forLoop.expression3);
        staticalysizeExpression(forLoop.expression4);
    }

    private void staticalysizeArrayDereference(ArrayDereference arrayDereference)
    {
        staticalysizeExpression(arrayDereference.expression1);
        staticalysizeExpression(arrayDereference.expression2);
    }

    private void staticalysizeStaticMethodInvocation(StaticMethodInvocation staticMethodInvocation)
    {
        staticalysizeMethodInvocation(staticMethodInvocation.methodInvocation);
    }

    private void staticalysizeTryCatch(TryCatch tryCatch)
    {
        staticalysizeTryPart(tryCatch.tryPart);
        staticalysizeCatchPart(tryCatch.catchPart);
    }

    private void staticalysizeCatchPart(CatchPart catchPart)
    {
        staticalysizeCatchList(catchPart.catchList);
    }

    private void staticalysizeCatchList(CatchList catchList)
    {
        for (CatchBody catchBody : catchList.elements)
            staticalysizeCatchBody(catchBody);
    }

    private void staticalysizeCatchBody(CatchBody catchBody)
    {
        staticalysizeVariableDeclaration(catchBody.variableDeclaration);
        staticalysizeExpression(catchBody.expression);
    }

    private void staticalysizeTryPart(TryPart tryPart)
    {
        staticalysizeExpression(tryPart.expression);
    }

    private void staticalysizeStaticDereferenceField(StaticDereferenceField staticDereferenceField)
    {
        // do nothing
    }

    private void staticalysizeDereferenceMethod(DereferenceMethod dereferenceMethod)
    {
        staticalysizeExpression(dereferenceMethod.expression);
        staticalysizeMethodInvocation(dereferenceMethod.methodInvocation);
    }

    private void staticalysizeDereferenceField(DereferenceField dereferenceField)
    {
        staticalysizeExpression(dereferenceField.expression);
    }

    private void staticalysizeMethodInvocation(MethodInvocation methodInvocation)
    {
        staticalysizeId(methodInvocation.id);
        staticalysizeArguments(methodInvocation.arguments);
    }

    private void staticalysizeArguments(Arguments arguments)
    {
        for (Expression element : arguments.elements)
            staticalysizeExpression(element);
    }

    private void staticalysizeIfThenElse(IfThenElse ifThenElse)
    {
        staticalysizeExpression(ifThenElse.expression1);
        staticalysizeExpression(ifThenElse.expression2);
        staticalysizeExpression(ifThenElse.expression3);
    }
    private void staticalysizeIfThen(IfThen ifThen)
    {
        staticalysizeExpression(ifThen.expression1);
        staticalysizeExpression(ifThen.expression2);
    }

    private void staticalysizeAssignment(IdAssignment assignment)
    {
        staticalysizeId(assignment.id);
        staticalysizeExpression(assignment.expression2);
    }


    private void staticalysizeVariableDeclaration(VariableDeclaration variableDeclaration)
    {
    }

    private void staticalysizeVariableCreation(VariableCreation variableCreation)
    {
        staticalysizeExpression(variableCreation.expression);
    }

    private void staticalysizeId(Id id)
    {
    }

    private void staticalysizeQuantity(Quantity quantity)
    {
        staticalysizeExpression(quantity.expression);
    }

    private void staticalysizeIntLiteral(IntLiteral intLiteral)
    {
        // do nothing
    }
    private void staticalysizeLongLiteral(LongLiteral content)
    {
        // do nothing
    }
    private void staticalysizeFloatLiteral(FloatLiteral floatLiteral)
    {
        // do nothing
    }
    private void staticalysizeDoubleLiteral(DoubleLiteral doubleLiteral)
    {
        // do nothing
    }
    private void staticalysizeBooleanLiteral(BooleanLiteral intLiteral)
    {
        // do nothing
    }
    private void staticalysizeStringLiteral(StringLiteral intLiteral)
    {
        // do nothing
    }

    private void staticalysizeBlock(Block block)
    {
        staticalysizeBlockContents(block.blockContents);
    }

    private void staticalysizeBlockContents(BlockContents blockContents)
    {
        for (Expression element : blockContents.elements)
            staticalysizeExpression(element);
    }

    private void staticalysizeAddition(Addition addition)
    {
        staticalysizeBinaryOperator(addition);
    }
    private void staticalysizeSubtraction(Subtraction subtraction)
    {
        staticalysizeBinaryOperator(subtraction);
    }
    private void staticalysizeMultiplication(Multiplication multiplication)
    {
        staticalysizeBinaryOperator(multiplication);
    }
    private void staticalysizeDivision(Division division)
    {
        staticalysizeBinaryOperator(division);
    }
    private void staticalysizeLessThan(LessThan lessThan)
    {
        staticalysizeBinaryOperator(lessThan);
    }
    private void staticalysizeGreaterThan(GreaterThan greaterThan)
    {
        staticalysizeBinaryOperator(greaterThan);
    }
    private void staticalysizeLessThanOrEqual(LessThanOrEqual lessThanOrEqual)
    {
        staticalysizeBinaryOperator(lessThanOrEqual);
    }
    private void staticalysizeGreaterThanOrEqual(GreaterThanOrEqual greaterThanOrEqual)
    {
        staticalysizeBinaryOperator(greaterThanOrEqual);
    }
    private void staticalysizeEquality(Equality equality)
    {
        staticalysizeBinaryOperator(equality);
    }
    private void staticalysizeInequality(Inequality inequality)
    {
        staticalysizeBinaryOperator(inequality);
    }
    private void staticalysizeBinaryOperator(BinaryOperatorElement operator)
    {
        staticalysizeExpression(operator.expression1);
        staticalysizeExpression(operator.expression2);
    }
}
