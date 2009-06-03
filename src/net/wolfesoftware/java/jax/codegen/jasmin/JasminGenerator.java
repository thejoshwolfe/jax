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
        genProgram(root.content);
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
            case Id.TYPE:
                evalId((Id)content);
                break;
            case Addition.TYPE:
                evalAddition((Addition)content);
                break;
            case Subtraction.TYPE:
                evalSubtraction((Subtraction)content);
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
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private void evalAssignment(Assignment assignment)
    {
        evalExpression(assignment.expression);
        printStatement("dup");
        printStatement("istore " + assignment.id.variable.number);
    }

    private void evalVariableDeclaration(VariableDeclaration variableDeclaration)
    {
        // do nothing
    }

    private void evalVariableCreation(VariableCreation variableCreation)
    {
        evalVariableDeclaration(variableCreation.variableDeclaration);
        
        evalExpression(variableCreation.expression);
        printStatement("istore " + variableCreation.variableDeclaration.id.variable.number);
    }

    private void evalId(Id id)
    {
        printStatement("iload " + id.variable.number);
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
        evalExpression(addition.expression1);
        evalExpression(addition.expression2);
        printStatement("iadd");
    }
    private void evalSubtraction(Subtraction subtraction)
    {
        evalExpression(subtraction.expression1);
        evalExpression(subtraction.expression2);
        printStatement("isub");
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
