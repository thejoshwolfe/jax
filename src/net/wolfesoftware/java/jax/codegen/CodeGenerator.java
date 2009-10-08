package net.wolfesoftware.java.jax.codegen;

import java.io.*;
import java.util.ArrayList;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.lexiconizer.*;
import net.wolfesoftware.java.jax.util.TestUtils;

/**
 * JVM instructions: http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html
 * Jasmin User Guide: http://jasmin.sourceforge.net/guide.html
 * Jasmin Instructions: http://jasmin.sourceforge.net/instructions.html
 */
public class CodeGenerator
{
    private static String defualtConstructor =
        ".method public <init>()V\n" +
        "    aload_0\n" +
        "    invokenonvirtual java/lang/Object/<init>()V\n" +
        "    return\n" +
        ".end method\n";

    private static String indentation = "    ";

    public static void generate(Lexiconization lexiconization, String outputFilename) throws FileNotFoundException
    {
        new CodeGenerator(lexiconization.root, outputFilename).generateCode();
    }


    private final Root root;
    private final PrintWriter out;
    private final String className;
    private final String outputFilename;
    private final ArrayList<String> exceptionTable = new ArrayList<String>();
    public CodeGenerator(Root root, String outputFilename) throws FileNotFoundException
    {
        this.root = root;
        out = new PrintWriter(outputFilename);
        className = root.content.classDeclaration.id.name;
        this.outputFilename = outputFilename;
    }


    protected void generateCode()
    {
        genCompilationUnit(root.content);
        out.close();
        TestUtils.compileJasmin(outputFilename);
    }

    private void genCompilationUnit(CompilationUnit compilationUnit)
    {
        genClassDeclaration(compilationUnit.classDeclaration);
    }

    private void genClassDeclaration(ClassDeclaration classDeclaration)
    {
        out.println(".class public " + className);
        out.println(".super java/lang/Object"); // hard code super class for now
        out.println();

        out.println(defualtConstructor);

        genClassBody(classDeclaration.classBody);
    }

    private void genClassBody(ClassBody classBody)
    {
        for (ClassMember element : classBody.elements)
        {
            genClassMember(element);
            out.println();
        }
    }

    private void genClassMember(ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                genFunctionDefinition((FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void genFunctionDefinition(FunctionDefinition functionDefinition)
    {
        // header
        out.print(".method public static ");
        out.print(functionDefinition.id + "(");
        genArgumentDeclarations(functionDefinition.argumentDeclarations);
        out.print(")");
        out.print(getTypeCode(functionDefinition.returnBehavior.type));
        out.println();
        // .limit stack
        printStatement(".limit stack " + functionDefinition.returnBehavior.stackRequirement);
        // .limit locals
        printStatement(".limit locals " + functionDefinition.context.capacity);

        // main body
        evalExpression(functionDefinition.expression);

        // return statement
        String rtnStmt = null;
        if (!functionDefinition.returnBehavior.type.isPrimitive())
            rtnStmt = "areturn";
        else if (functionDefinition.returnBehavior.type == RuntimeType.VOID)
            rtnStmt = "return";
        else if (functionDefinition.returnBehavior.type == RuntimeType.INT || functionDefinition.returnBehavior.type == RuntimeType.BOOLEAN)
            rtnStmt = "ireturn";
        if (rtnStmt == null)
            throw new RuntimeException(functionDefinition.returnBehavior.type.toString());
        printStatement(rtnStmt);

        // exception table
        for (String exceptionLine : exceptionTable)
            printStatement(exceptionLine);
        exceptionTable.clear();

        // footer
        out.println(".end method");
    }


    private void evalExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType())
        {
            case IntLiteral.TYPE:
                evalIntLiteral((IntLiteral)content);
                break;
            case BooleanLiteral.TYPE:
                evalBooleanLiteral((BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                evalStringLiteral((StringLiteral)content);
                break;
            case Id.TYPE:
                evalId((Id)content);
                break;
            case Addition.TYPE:
                evalAddition((Addition)content);
                break;
            case Subtraction.TYPE:
                evalSubtraction((Subtraction)content);
                break;
            case Multiplication.TYPE:
                evalMultiplication((Multiplication)content);
                break;
            case Division.TYPE:
                evalDivision((Division)content);
                break;
            case LessThan.TYPE:
                evalLessThan((LessThan)content);
                break;
            case GreaterThan.TYPE:
                evalGreaterThan((GreaterThan)content);
                break;
            case LessThanOrEqual.TYPE:
                evalLessThanOrEqual((LessThanOrEqual)content);
                break;
            case GreaterThanOrEqual.TYPE:
                evalGreaterThanOrEqual((GreaterThanOrEqual)content);
                break;
            case Equality.TYPE:
                evalEquality((Equality)content);
                break;
            case Inequality.TYPE:
                evalInequality((Inequality)content);
                break;
            case Quantity.TYPE:
                evalQuantity((Quantity)content);
                break;
            case Block.TYPE:
                evalBlock((Block)content);
                break;
            case VariableCreation.TYPE:
                evalVariableCreation((VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                evalVariableDeclaration((VariableDeclaration)content);
                break;
            case Assignment.TYPE:
                evalAssignment((Assignment)content);
                break;
            case IfThenElse.TYPE:
                evalIfThenElse((IfThenElse)content);
                break;
            case IfThen.TYPE:
                evalIfThen((IfThen)content);
                break;
            case FunctionInvocation.TYPE:
                evalFunctionInvocation((FunctionInvocation)content);
                break;
            case DereferenceMethod.TYPE:
                evalDereferenceMethod((DereferenceMethod)content);
                break;
            case StaticDereferenceField.TYPE:
                evalStaticDereferenceField((StaticDereferenceField)content);
                break;
            case StaticFunctionInvocation.TYPE:
                evalStaticFunctionInvocation((StaticFunctionInvocation)content);
                break;
            case TryCatch.TYPE:
                evalTryCatch((TryCatch)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private void evalStaticFunctionInvocation(StaticFunctionInvocation staticFunctionInvocation)
    {
        evalFunctionInvocation(staticFunctionInvocation.functionInvocation);
    }

    private void evalTryCatch(TryCatch tryCatch)
    {
        evalTryPart(tryCatch.tryPart);
        printStatement("goto " + tryCatch.endLabel);
        evalCatchPart(tryCatch.catchPart);
        printLabel(tryCatch.endLabel);
        for (CatchBody catchBody : tryCatch.catchPart.catchList.elements) {
            String typeName = getTypeName(catchBody.variableDeclaration.typeId.type);
            exceptionTable.add(".catch " + typeName + " from " + tryCatch.tryPart.startLabel + " to " + tryCatch.tryPart.endLabel + " using " + catchBody.startLabel);
        }
    }

    private void evalTryPart(TryPart tryPart)
    {
        printLabel(tryPart.startLabel);
        // TODO: store stack in locals
        evalExpression(tryPart.expression);
        printLabel(tryPart.endLabel);
        // TODO: restore locals onto stack
    }

    private void evalCatchPart(CatchPart catchPart)
    {
        evalCatchList(catchPart.catchList);
    }

    private void evalCatchList(CatchList catchList)
    {
        for (CatchBody catchBody : catchList.elements)
            evalCatchBody(catchBody);
    }

    private void evalCatchBody(CatchBody catchBody)
    {
        printLabel(catchBody.startLabel);
        printStatement("astore " + catchBody.variableDeclaration.id.variable.number);
        evalExpression(catchBody.expression);
        // TODO: restore locals onto stack
    }

    private void evalStaticDereferenceField(StaticDereferenceField staticDereferenceField)
    {
        String statement = "getstatic " + getTypeName(staticDereferenceField.field.declaringType) + '/' + staticDereferenceField.id.name + ' ' + getTypeCode(staticDereferenceField.field.returnType);
        printStatement(statement);
    }

    private void evalDereferenceMethod(DereferenceMethod dereferenceMethod)
    {
        evalExpression(dereferenceMethod.expression);
        evalFunctionInvocation(dereferenceMethod.functionInvocation);
    }

    private void evalFunctionInvocation(FunctionInvocation functionInvocation)
    {
        evalArguments(functionInvocation.arguments);

        String invokeInstruction = functionInvocation.method.isStatic ? "invokestatic " : "invokevirtual ";

        printStatement(invokeInstruction + getMethodCode(functionInvocation.method));
    }

    private void evalArguments(Arguments arguments)
    {
        for (Expression element : arguments.elements)
            evalExpression(element);
    }

    private void evalIfThenElse(IfThenElse ifThenElse)
    {
        evalExpression(ifThenElse.expression1);
        printStatement("ifeq " + ifThenElse.label1);
        evalExpression(ifThenElse.expression2);
        printStatement("goto " + ifThenElse.label2);
        printLabel(ifThenElse.label1);
        evalExpression(ifThenElse.expression3);
        printLabel(ifThenElse.label2);
    }
    private void evalIfThen(IfThen ifThen)
    {
        evalExpression(ifThen.expression1);
        printStatement("ifeq " + ifThen.label);
        evalExpression(ifThen.expression2);
        printLabel(ifThen.label);
    }

    private void evalAssignment(Assignment assignment)
    {
        evalExpression(assignment.expression);
        printStatement("dup");
        String typeLetter = getTypeLetter(assignment.id.variable.type);
        printStatement(typeLetter + "store " + assignment.id.variable.number);
    }

    private String getTypeLetter(Type type)
    {
        if (!type.isPrimitive())
            return "a";
        if (type == RuntimeType.INT || type == RuntimeType.BOOLEAN)
            return "i";
        throw new RuntimeException(type.toString());
    }

    private void evalVariableDeclaration(VariableDeclaration variableDeclaration)
    {
        // do nothing
    }

    private void evalVariableCreation(VariableCreation variableCreation)
    {
        evalVariableDeclaration(variableCreation.variableDeclaration);

        evalExpression(variableCreation.expression);
        String typeLetter = getTypeLetter(variableCreation.variableDeclaration.id.variable.type);
        printStatement(typeLetter + "store " + variableCreation.variableDeclaration.id.variable.number);
    }

    private void evalId(Id id)
    {
        String typeLetter = getTypeLetter(id.variable.type);
        printStatement(typeLetter + "load " + id.variable.number);
    }

    private void evalBlock(Block block)
    {
        evalBlockContents(block.blockContents);
    }

    private void evalBlockContents(BlockContents blockContents)
    {
        for (int i = 0; i < blockContents.elements.size(); i++)
        {
            Expression element = blockContents.elements.get(i);
            evalExpression(element);
            if (element.returnBehavior.type != RuntimeType.VOID)
                if (i < blockContents.elements.size() - 1)
                    printStatement("pop");
        }
    }

    private void evalQuantity(Quantity quantity)
    {
        evalExpression(quantity.expression);
    }

    private void evalAddition(Addition addition)
    {
        evalOperator(addition, "add");
    }
    private void evalSubtraction(Subtraction subtraction)
    {
        evalOperator(subtraction, "sub");
    }
    private void evalMultiplication(Multiplication multiplication)
    {
        evalOperator(multiplication, "mul");
    }
    private void evalDivision(Division division)
    {
        evalOperator(division, "div");
    }
    private void evalOperator(BinaryOperatorElement operator, String operation)
    {
        evalExpression(operator.expression1);
        evalExpression(operator.expression2);
        printStatement("i" + operation);
    }
    private void evalLessThan(LessThan lessThan)
    {
        evalComparison(lessThan, "lt");
    }
    private void evalGreaterThan(GreaterThan greaterThan)
    {
        evalComparison(greaterThan, "gt");
    }
    private void evalLessThanOrEqual(LessThanOrEqual lessThanOrEqual)
    {
        evalComparison(lessThanOrEqual, "le");
    }
    private void evalGreaterThanOrEqual(GreaterThanOrEqual greaterThanOrEqual)
    {
        evalComparison(greaterThanOrEqual, "ge");
    }
    private void evalEquality(Equality equality)
    {
        evalComparison(equality, "eq");
    }
    private void evalInequality(Inequality inequality)
    {
        evalComparison(inequality, "ne");
    }
    private void evalComparison(ComparisonOperator operator, String condition)
    {
        evalExpression(operator.expression1);
        evalExpression(operator.expression2);
        printStatement("if_icmp" + condition + " " + operator.label1);
        printStatement("iconst_0");
        printStatement("goto " + operator.label2);
        printLabel(operator.label1);
        printStatement("iconst_1");
        printLabel(operator.label2);
    }

    private void evalIntLiteral(IntLiteral intLiteral)
    {
        printStatement("ldc " + intLiteral.value);
    }
    private void evalBooleanLiteral(BooleanLiteral booleanLiteral)
    {
        printStatement("ldc " + (booleanLiteral.value ? 1 : 0));
    }
    private void evalStringLiteral(StringLiteral stringLiteral)
    {
        printStatement("ldc " + stringLiteral.source);
    }

    private void genArgumentDeclarations(ArgumentDeclarations argumentDeclarations)
    {
        for (VariableDeclaration variableDeclaration : argumentDeclarations.elements)
            out.print(getTypeCode(variableDeclaration.typeId.type));
    }

    private void printStatement(String s)
    {
        out.println(indentation + s);
    }
    private void printLabel(String labelName)
    {
        out.println(labelName + ":");
    }
    private String getTypeCode(Type type)
    {
        // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
        if (type.isPrimitive())
        {
            if (type == RuntimeType.INT)
                return "I";
            if (type == RuntimeType.VOID)
                return "V";
            if (type == RuntimeType.BOOLEAN)
                return "Z";
            throw new RuntimeException(type.toString());
        }
        return "L" + getTypeName(type) + ";";
    }
    private String getTypeName(Type type)
    {
        return type.fullName.replace('.', '/');
    }
    private String getMethodCode(Method method)
    {
        StringBuilder builder = new StringBuilder(getTypeName(method.declaringType));
        builder.append('/').append(method.id).append('(');
        for (Type type : method.argumentSignature)
            builder.append(getTypeCode(type));
        builder.append(')');
        builder.append(getTypeCode(method.returnType));
        return builder.toString();
    }
}
