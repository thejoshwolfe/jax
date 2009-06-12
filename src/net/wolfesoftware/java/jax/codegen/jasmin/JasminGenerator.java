package net.wolfesoftware.java.jax.codegen.jasmin;

import java.io.*;
import java.util.regex.*;
import net.wolfesoftware.java.common.TestUtils;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.codegen.*;
import net.wolfesoftware.java.jax.lexiconizer.Type;

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
        // for now, use the file name as the class name
        Matcher classNameFinder = Pattern.compile("([^\\\\\\/]*)\\..+$").matcher(outputFilename);
        classNameFinder.find();
        className = classNameFinder.group(1);
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
        out.print(IJasminConstants.indentation + ".limit stack ");
        out.print(functionDefinition.returnBehavior.stackRequirement);
        out.println();
        // .limit locals
        out.print(IJasminConstants.indentation + ".limit locals ");
        out.print(functionDefinition.context.capacity);
        out.println();

        // main body
        evalExpression(functionDefinition.expression);

        // return statement
        String rtnStmt = null;
        if (functionDefinition.returnBehavior.type.packageId != null)
            rtnStmt = "areturn";
        else if (functionDefinition.returnBehavior.type == Type.KEYWORD_VOID)
            rtnStmt = "return";
        else if (functionDefinition.returnBehavior.type == Type.KEYWORD_INT || functionDefinition.returnBehavior.type == Type.KEYWORD_BOOLEAN)
            rtnStmt = "ireturn";
        if (rtnStmt == null)
            throw new RuntimeException(functionDefinition.returnBehavior.type.toString());
        printStatement(rtnStmt);

        // footer
        out.println(".end method");
    }

    private String getTypeCode(Type type)
    {
        // http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
        if (type.packageId == null)
        {
            if (type == Type.KEYWORD_INT)
                return "I";
            if (type == Type.KEYWORD_VOID)
                return "V";
            if (type == Type.KEYWORD_BOOLEAN)
                return "Z";
            throw new RuntimeException(type.toString());
        }
        return "L" + type.packageId.replace('.', '/') + "/" + type.id + ";";
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
            default:
                throw new RuntimeException(content.getClass().toString());
        }
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
        if (type.packageId != null)
            return "a";
        if (type == Type.KEYWORD_INT || type == Type.KEYWORD_BOOLEAN)
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
            if (element.returnBehavior.type != Type.KEYWORD_VOID)
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
}
