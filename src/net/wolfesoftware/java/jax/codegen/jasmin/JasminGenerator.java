package net.wolfesoftware.java.jax.codegen.jasmin;

import java.io.*;
import net.wolfesoftware.java.common.TestUtils;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.codegen.*;
import net.wolfesoftware.java.jax.lexiconizer.*;

public class JasminGenerator extends CodeGenerator
{
    private final Root root;
    private final PrintWriter out;
    private final String className;
    private final String outputFilename;
    public JasminGenerator(Root root, String outputFilename) throws FileNotFoundException
    {
        this.root = root;
        out = new PrintWriter(outputFilename);
        className = root.content.classDeclaration.id.name;
        this.outputFilename = outputFilename;
    }

    public static final CodeGenStrategy STRATEGY = new CodeGenStrategy() {
        public void generateCode(Root root, String outputFilename) throws FileNotFoundException
        {
            new JasminGenerator(root, outputFilename).generateCode();
        }
    };

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

        out.println(IJasminConstants.defualtConstructor);

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
            default:
                throw new RuntimeException(content.getClass().toString());
        }
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

        String invokeinstruction = functionInvocation.method.isStatic ? "invokestatic " : "invokevirtual ";

        printStatement(invokeinstruction + getMethodCode(functionInvocation.method));
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
        out.println(ifThenElse.label1 + ":");
        evalExpression(ifThenElse.expression3);
        out.println(ifThenElse.label2 + ":");
    }
    private void evalIfThen(IfThen ifThen)
    {
        evalExpression(ifThen.expression1);
        printStatement("ifeq " + ifThen.label);
        evalExpression(ifThen.expression2);
        out.println(ifThen.label + ":");
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
        out.println(operator.label1 + ":");
        printStatement("iconst_1");
        out.println(operator.label2 + ":");
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
        // TODO
    }

    private void printStatement(String s)
    {
        out.println(IJasminConstants.indentation + s);
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
        if (type == null) // TODO tmp workaround for not having class declarations in the syntax
            return className;
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
