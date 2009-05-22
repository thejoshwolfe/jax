package net.wolfesoftware.java.jax.lexiconizer;

import java.util.HashMap;
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
    
    private final Parsing parsing;
    private Lexiconizer(Parsing parsing)
    {
        this.parsing = parsing;
    }
    
    private Lexiconization lexiconize()
    {
        // TODO: ensure types match up.
        // There is no type coercion or even implicit type casting yet, 
        // so exact matches is all that must be verified.
        lexiconizeProgram(parsing.root);
        
        return null;
    }
    
    private void lexiconizeProgram(Program program)
    {
        for (TopLevelItem topLevelItem : parsing.root.elements)
            registerName(topLevelItem);
        
        for (TopLevelItem topLevelItem : parsing.root.elements)
            lexiconizeTopLevelItem(topLevelItem);
    }

    private void registerName(TopLevelItem topLevelItem)
    {
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
        ReturnBehavior returnBehavior = lexiconizeExpression(functionDefinition.expression);
        if (returnType != returnBehavior.type)
            ; // TODO: error
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
            case Id.TYPE:
                // TODO
            case Block.TYPE:
                // TODO
            default:
                throw new RuntimeException();
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeAddition(Addition content)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(content.expression1);
        ReturnBehavior returnBehavior2 = lexiconizeExpression(content.expression2);
        if (returnBehavior1.type != returnBehavior2.type)
            return null;
        return new ReturnBehavior(returnBehavior1.type);
    }

    private Type resolveType(TypeId typeId)
    {
        return importedTypes.get(typeId.toString());
    }
    
    private class Context
    {
        public void pushScope()
        {
        }
        public void popScope()
        {
        }
        public boolean declareSomething(String name)
        {
            return false;
        }
        public String resolveSomething(String name)
        {
            return null;
        }
    }
}
