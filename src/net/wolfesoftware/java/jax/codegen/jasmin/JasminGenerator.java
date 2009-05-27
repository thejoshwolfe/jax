package net.wolfesoftware.java.jax.codegen.jasmin;

import java.io.*;
import java.util.regex.*;
import net.wolfesoftware.java.common.TestUtils;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.codegen.*;
import net.wolfesoftware.java.jax.lexiconizer.Type;

public class JasminGenerator extends CodeGenerator
{
    private final Program root;
    private final PrintWriter out;
    private final String className;
    private final String outputFilename;
    public JasminGenerator(Program root, String outputFilename) throws FileNotFoundException
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
        public void generateCode(Program root, String outputFilename) throws FileNotFoundException
        {
            new JasminGenerator(root, outputFilename).generateCode();
        }
    };

    protected void generateCode()
    {
        genProgram(root);
        out.close();
        TestUtils.compileJasmin(outputFilename);
    }

    protected void genProgram(Program program)
    {
        out.println(".class public " + className);
        out.println(".super java/lang/Object"); // hard code super class for now
        out.println();

        out.println(IJasminConstants.defualtConstructor);

        for (TopLevelItem element : program.elements)
        {
            genTopLevelItem(element);
            out.println();
        }
    }

    private void genTopLevelItem(TopLevelItem element)
    {
        if (element == null)
            return;
        switch (element.content.getElementType())
        {
            case FunctionDefinition.TYPE:
                genFunctionDefinition((FunctionDefinition)element.content);
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
        out.print(IJasminConstants.indentation + ".limit stack " + functionDefinition.returnBehavior.stackRequirement);
        out.println();

        // main body
        evalExpression(functionDefinition.expression);

        // return statement
        String rtnStmt = null;
        if (functionDefinition.returnBehavior.type == Type.KEYWORD_VOID)
            rtnStmt = "return";
        else if (functionDefinition.returnBehavior.type == Type.KEYWORD_INT)
            rtnStmt = "ireturn";
        printStatement(rtnStmt);

        // footer
        out.println(".end method");
    }

    private String getTypeCode(Type type)
    {
        if (type == Type.KEYWORD_INT)
            return "I";
        if (type == Type.KEYWORD_VOID)
            return "V";
        throw new RuntimeException();
    }

    private void evalExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType())
        {
            case IntLiteral.TYPE:
                evalIntLiteral((IntLiteral)content);
                break;
            case Addition.TYPE:
                evalAddition((Addition)content);
                break;
            case Quantity.TYPE:
                evalQuantity((Quantity)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void evalQuantity(Quantity quantity)
    {
        evalExpression(quantity.expression);
    }

    private void evalAddition(Addition addition)
    {
        evalExpression(addition.expression1);
        evalExpression(addition.expression2);
        printStatement("iadd");
    }

    private void evalIntLiteral(IntLiteral intLiteral)
    {
        printStatement("ldc " +  intLiteral.value);
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
