package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.parser.Parsing;

public class Lexiconizer
{
    public static Lexiconization lexiconize(Parsing parsing)
    {
        return new Lexiconizer(parsing).lexiconize();
    }
    
    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        Type.initPrimitives(importedTypes);
        // we don't have any "import " statements in the lang yet. primitives is all we got so far.
    }
    private final HashMap<String, FunctionDefinition> localMethods = new HashMap<String, FunctionDefinition>();

    private final Program root;
    private final ArrayList<LexicalException> errors = new ArrayList<LexicalException>();
    private Lexiconizer(Parsing parsing)
    {
        root = parsing.root;
    }

    private Lexiconization lexiconize()
    {
        // ensure types match up.
        // There is no type coercion or even implicit type casting yet, 
        // so exact matches is all that must be verified.
        lexiconizeProgram(root);

        return new Lexiconization(root, errors);
    }

    private void lexiconizeProgram(Program program)
    {
        for (TopLevelItem topLevelItem : program.elements)
            registerName(topLevelItem);
        
        for (TopLevelItem topLevelItem : program.elements)
            lexiconizeTopLevelItem(topLevelItem);
    }

    private void registerName(TopLevelItem topLevelItem)
    {
        if (topLevelItem == null)
            return;
        ParseElement content = topLevelItem.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                registerName((FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void registerName(FunctionDefinition functionDefinition)
    {
        localMethods.put(functionDefinition.id.name, functionDefinition);
    }

    private void lexiconizeTopLevelItem(TopLevelItem topLevelItem)
    {
        if (topLevelItem == null)
            return;
        ParseElement content = topLevelItem.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                lexiconizeFunctionDefinition((FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void lexiconizeFunctionDefinition(FunctionDefinition functionDefinition)
    {
        Type returnType = resolveType(functionDefinition.typeId);
        functionDefinition.returnBehavior = lexiconizeExpression(functionDefinition.expression);
        if (returnType != functionDefinition.returnBehavior.type)
            errors.add(new LexicalException()); // TODO
    }

    private ReturnBehavior lexiconizeExpression(Expression expression)
    {
        ParseElement content = expression.content;
        ReturnBehavior returnBehavior;
        switch (content.getElementType())
        {
            case Addition.TYPE:
                returnBehavior = lexiconizeAddition((Addition)content);
                break;
//            case Id.TYPE:
//                // TODO
//            case Block.TYPE:
//                // TODO
            case IntLiteral.TYPE:
                returnBehavior = lexiconizeIntLiteral((IntLiteral)content);
                break;
            case Quantity.TYPE:
                returnBehavior = lexiconizeQuantity((Quantity)content);
                break;
            default:
                throw new RuntimeException();
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeQuantity(Quantity quantity)
    {
        return lexiconizeExpression(quantity.expression);
    }

    private ReturnBehavior lexiconizeIntLiteral(IntLiteral content)
    {
        return new ReturnBehavior(Type.KEYWORD_INT, 1);
    }

    private ReturnBehavior lexiconizeAddition(Addition addition)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(addition.expression1);
        ReturnBehavior returnBehavior2 = lexiconizeExpression(addition.expression2);
        if (returnBehavior1.type != returnBehavior2.type)
            return null;
        int stackRequirement = Math.max(returnBehavior1.stackRequirement, returnBehavior2.stackRequirement + 1);
        return new ReturnBehavior(returnBehavior1.type, stackRequirement);
    }

    private Type resolveType(TypeId typeId)
    {
        return importedTypes.get(typeId.toString());
    }

}
