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

    private final Root root;
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
        lexiconizeProgram(root.content);

        return new Lexiconization(root, errors);
    }

    private void lexiconizeProgram(Program program)
    {
        deleteNulls(program);

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
        functionDefinition.context = new RootLocalContext(errors);
        lexiconizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.returnBehavior = lexiconizeExpression(functionDefinition.context, functionDefinition.expression);
        if (returnType != functionDefinition.returnBehavior.type)
            errors.add(new LexicalException());
    }

    private void lexiconizeArgumentDeclarations(LocalContext context, ArgumentDeclarations argumentDeclarations)
    {
        // TODO
    }

    private ReturnBehavior lexiconizeExpression(LocalContext context, Expression expression)
    {
        if (expression == null)
            return ReturnBehavior.VOID;
        ParseElement content = expression.content;
        ReturnBehavior returnBehavior;
        switch (content.getElementType())
        {
            case Addition.TYPE:
                returnBehavior = lexiconizeAddition(context, (Addition)content);
                break;
            case Id.TYPE:
                returnBehavior = lexiconizeId(context, (Id)content);
                break;
            case Block.TYPE:
                returnBehavior = lexiconizeBlock(context, (Block)content);
                break;
            case IntLiteral.TYPE:
                returnBehavior = lexiconizeIntLiteral(context, (IntLiteral)content);
                break;
            case Quantity.TYPE:
                returnBehavior = lexiconizeQuantity(context, (Quantity)content);
                break;
            case VariableCreation.TYPE:
                returnBehavior = lexiconizeVariableCreation(context, (VariableCreation)content);
                break;
            case VariableDeclaration.TYPE:
                returnBehavior = lexiconizeVariableDeclaration(context, (VariableDeclaration)content);
                break;
            default:
                throw new RuntimeException();
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeVariableDeclaration(LocalContext context, VariableDeclaration variableDeclaration)
    {
        variableDeclaration.type = resolveType(variableDeclaration.typeId);
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeId(LocalContext context, Id id)
    {
        id.variable = context.getLocalVariable(id.name);
        if (id.variable == null) {
            errors.add(new LexicalException());
            // TODO: return something
        }
        return new ReturnBehavior(id.variable.type, 1);
    }

    private ReturnBehavior lexiconizeVariableCreation(LocalContext context, VariableCreation variableCreation)
    {
        lexiconizeVariableDeclaration(context, variableCreation.variableDeclaration);
        variableCreation.variableDeclaration.type = resolveType(variableCreation.variableDeclaration.typeId);
        ReturnBehavior returnBehavior = lexiconizeExpression(context, variableCreation.expression);
        if (variableCreation.variableDeclaration.type != returnBehavior.type)
            errors.add(new LexicalException());
        return ReturnBehavior.VOID;
    }

    private ReturnBehavior lexiconizeBlock(LocalContext context, Block block)
    {
        block.context = new LocalContext(errors, context);
        return lexiconizeBlockContents(block.context, block.blockContents);
    }

    private ReturnBehavior lexiconizeBlockContents(LocalContext context, BlockContents blockContents)
    {
        deleteNulls(blockContents);

        ReturnBehavior returnBehavior = ReturnBehavior.VOID;
        for (Expression element : blockContents.elements)
        {
            returnBehavior = lexiconizeExpression(context, element);
            VariableDeclaration variableDeclaration;
            switch (element.content.getElementType())
            {
                case VariableDeclaration.TYPE:
                    variableDeclaration = (VariableDeclaration)element.content;
                    break;
                case VariableCreation.TYPE:
                    variableDeclaration = ((VariableCreation)element.content).variableDeclaration;
                    break;
                default:
                    variableDeclaration = null;
            }
            if (variableDeclaration != null)
            {
                context.addLocalVariable(variableDeclaration.id, variableDeclaration.type);
            }
        }
        return returnBehavior;
    }

    private ReturnBehavior lexiconizeQuantity(LocalContext context, Quantity quantity)
    {
        return lexiconizeExpression(context, quantity.expression);
    }

    private ReturnBehavior lexiconizeIntLiteral(LocalContext context, IntLiteral content)
    {
        return new ReturnBehavior(Type.KEYWORD_INT, 1);
    }

    private ReturnBehavior lexiconizeAddition(LocalContext context, Addition addition)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, addition.expression1);
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, addition.expression2);
        if (returnBehavior1.type != returnBehavior2.type)
            return null;
        int stackRequirement = Math.max(returnBehavior1.stackRequirement, returnBehavior2.stackRequirement + 1);
        return new ReturnBehavior(returnBehavior1.type, stackRequirement);
    }

    private Type resolveType(TypeId typeId)
    {
        return importedTypes.get(typeId.toString());
    }

    private static void deleteNulls(ListElement<?> listElement)
    {
        Iterator<?> iterator = listElement.elements.iterator();
        while (iterator.hasNext())
            if (iterator.next() == null)
                iterator.remove();
    }
}
