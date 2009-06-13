package net.wolfesoftware.java.jax.lexiconizer;

import java.util.*;
import net.wolfesoftware.java.jax.ast.*;
import net.wolfesoftware.java.jax.parser.Parsing;

public class Lexiconizer
{
    public static Lexiconization lexiconize(Parsing parsing)
    {
        return new Lexiconizer(parsing).lexiconizeRoot();
    }
    
    private final HashMap<String, Type> importedTypes = new HashMap<String, Type>();
    {
        Type.initPrimitives(importedTypes);
        Type.initJavaLang(importedTypes);
    }

    private final Root root;
    private final ArrayList<LexicalException> errors = new ArrayList<LexicalException>();
    private Lexiconizer(Parsing parsing)
    {
        root = parsing.root;
    }

    private Lexiconization lexiconizeRoot()
    {
        // ensure types match up.
        // There is no type coercion or even implicit type casting yet, 
        // so exact matches is all that must be verified.
        lexiconizeCompilationUnit(root.content);

        return new Lexiconization(root, errors);
    }

    private void lexiconizeCompilationUnit(CompilationUnit compilationUnit)
    {
        lexiconizeImports(compilationUnit.imports);
        lexiconizeClassDeclaration(compilationUnit.classDeclaration);
    }

    private void lexiconizeImports(Imports imports)
    {
        // TODO Auto-generated method stub
        
    }

    private void lexiconizeClassDeclaration(ClassDeclaration classDeclaration)
    {
        ClassContext context = new ClassContext();
        lexiconizeClassBody(context, classDeclaration.classBody);
    }

    private void lexiconizeClassBody(ClassContext context, ClassBody classBody)
    {
        deleteNulls(classBody);

        for (ClassMember classMember : classBody.elements)
            preLexiconizeClassMemeber(context, classMember);

        for (ClassMember classMember : classBody.elements)
            lexiconizeClassMemeber(context, classMember);
    }

    private void preLexiconizeClassMemeber(ClassContext context, ClassMember topLevelItem)
    {
        if (topLevelItem == null)
            return;
        ParseElement content = topLevelItem.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                preLexiconizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }
    private void preLexiconizeFunctionDefinition(ClassContext context, FunctionDefinition functionDefinition)
    {
        Type returnType = resolveType(functionDefinition.typeId);
        functionDefinition.context = new RootLocalContext(context);
        Type[] arguemntSignature = lexiconizeArgumentDeclarations(functionDefinition.context, functionDefinition.argumentDeclarations);
        functionDefinition.method = new Method(returnType, arguemntSignature);
        context.addMethod(functionDefinition.method);
    }

    private void lexiconizeClassMemeber(ClassContext context, ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                lexiconizeFunctionDefinition(context, (FunctionDefinition)content);
                break;
            default:
                throw new RuntimeException();
        }
    }

    private void lexiconizeFunctionDefinition(ClassContext context, FunctionDefinition functionDefinition)
    {
        functionDefinition.returnBehavior = lexiconizeExpression(functionDefinition.context, functionDefinition.expression);
        if (functionDefinition.method.returnType != functionDefinition.returnBehavior.type)
            errors.add(new LexicalException());
    }

    private Type[] lexiconizeArgumentDeclarations(LocalContext context, ArgumentDeclarations argumentDeclarations)
    {
        Type[] argumentSignature = new Type[argumentDeclarations.elements.size()];
        int i = 0;
        for(VariableDeclaration variableDeclaration : argumentDeclarations.elements)
            argumentSignature[i++] = lexiconizeVariableDeclaration(context, variableDeclaration).type;
        return argumentSignature;
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
            case Subtraction.TYPE:
                returnBehavior = lexiconizeSubtraction(context, (Subtraction)content);
                break;
            case Multiplication.TYPE:
                returnBehavior = lexiconizeMultiplication(context, (Multiplication)content);
                break;
            case Division.TYPE:
                returnBehavior = lexiconizeDivision(context, (Division)content);
                break;
            case Equality.TYPE:
                returnBehavior = lexiconizeEquality(context, (Equality)content);
                break;
            case Inequality.TYPE:
                returnBehavior = lexiconizeInequality(context, (Inequality)content);
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
            case BooleanLiteral.TYPE:
                returnBehavior = lexiconizeBooleanLiteral(context, (BooleanLiteral)content);
                break;
            case StringLiteral.TYPE:
                returnBehavior = lexiconizeStringLiteral(context, (StringLiteral)content);
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
            case Assignment.TYPE:
                returnBehavior = lexiconizeAssignment(context, (Assignment)content);
                break;
            case IfThenElse.TYPE:
                returnBehavior = lexiconizeIfThenElse(context, (IfThenElse)content);
                break;
            case IfThen.TYPE:
                returnBehavior = lexiconizeIfThen(context, (IfThen)content);
                break;
            case FunctionInvocation.TYPE:
                returnBehavior = lexiconizeFunctionInvocation(context, (FunctionInvocation)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
        expression.returnBehavior = returnBehavior;
        return returnBehavior;
    }


    private ReturnBehavior lexiconizeFunctionInvocation(LocalContext context, FunctionInvocation functionInvocation)
    {
        ReturnBehavior[] argumentSignature = lexiconizeArguments(context, functionInvocation.arguments);
        functionInvocation.method = resolveFunction(context.getClassContext(), functionInvocation.id, argumentSignature);

        return null;
    }


    private ReturnBehavior[] lexiconizeArguments(LocalContext context, Arguments arguments)
    {
        ReturnBehavior[] rtnArr = new ReturnBehavior[arguments.elements.size()];
        int i = 0;
        for (Expression element : arguments.elements)
            rtnArr[i++] = lexiconizeExpression(context, element);
        return rtnArr;
    }

    private ReturnBehavior lexiconizeIfThenElse(LocalContext context, IfThenElse ifThenElse)
    {
        lexiconizeExpression(context, ifThenElse.expression1);
        if (ifThenElse.expression1.returnBehavior.type != Type.KEYWORD_BOOLEAN)
            errors.add(new LexicalException());
        ifThenElse.label1 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression2);
        ifThenElse.label2 = context.nextLabel();
        lexiconizeExpression(context, ifThenElse.expression3);
        if (ifThenElse.expression2.returnBehavior.type != ifThenElse.expression3.returnBehavior.type)
            errors.add(new LexicalException());
        return ifThenElse.expression2.returnBehavior.clone(Math.max(ifThenElse.expression1.returnBehavior.stackRequirement, ifThenElse.expression3.returnBehavior.stackRequirement));
    }
    private ReturnBehavior lexiconizeIfThen(LocalContext context, IfThen ifThen)
    {
        lexiconizeExpression(context, ifThen.expression1);
        if (ifThen.expression1.returnBehavior.type != Type.KEYWORD_BOOLEAN)
            errors.add(new LexicalException());
        ifThen.label = context.nextLabel();
        lexiconizeExpression(context, ifThen.expression2);
        if (ifThen.expression2.returnBehavior.type != Type.KEYWORD_VOID)
            errors.add(new LexicalException());
        return new ReturnBehavior(Type.KEYWORD_VOID, Math.max(ifThen.expression2.returnBehavior.stackRequirement, ifThen.expression1.returnBehavior.stackRequirement));
    }

    private ReturnBehavior lexiconizeAssignment(LocalContext context, Assignment assignment)
    {
        assignment.id.variable = context.getLocalVariable(assignment.id.name);
        if (assignment.id.variable == null)
            errors.add(new LexicalException());
        ReturnBehavior returnBehavior = lexiconizeExpression(context, assignment.expression);
        if (assignment.id.variable != null && assignment.id.variable.type != returnBehavior.type)
            errors.add(new LexicalException());
        return returnBehavior.clone(2);
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
        ReturnBehavior returnBehavior = lexiconizeExpression(context, variableCreation.expression);
        if (variableCreation.variableDeclaration.type != returnBehavior.type)
            errors.add(new LexicalException());
        return new ReturnBehavior(Type.KEYWORD_VOID, returnBehavior.stackRequirement);
    }

    private ReturnBehavior lexiconizeBlock(LocalContext context, Block block)
    {
        block.context = new LocalContext(context);
        return lexiconizeBlockContents(block.context, block.blockContents);
    }

    private ReturnBehavior lexiconizeBlockContents(LocalContext context, BlockContents blockContents)
    {
        blockContents.forceVoid = blockContents.elements.size() == 0 || blockContents.elements.get(blockContents.elements.size() -1) == null;

        deleteNulls(blockContents);

        Type returnType = Type.KEYWORD_VOID;
        int stackRequirement = 0;
        for (Expression element : blockContents.elements)
        {
            ReturnBehavior returnBehavior = lexiconizeExpression(context, element);
            returnType = returnBehavior.type;
            stackRequirement = Math.max(stackRequirement, returnBehavior.stackRequirement);
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
                context.addLocalVariable(variableDeclaration.id, variableDeclaration.type, errors);
            }
        }
        if (blockContents.forceVoid)
            returnType = Type.KEYWORD_VOID;
        return new ReturnBehavior(returnType, stackRequirement);
    }

    private ReturnBehavior lexiconizeQuantity(LocalContext context, Quantity quantity)
    {
        return lexiconizeExpression(context, quantity.expression);
    }

    private ReturnBehavior lexiconizeIntLiteral(LocalContext context, IntLiteral intLiteral)
    {
        return new ReturnBehavior(Type.KEYWORD_INT, 1);
    }
    private ReturnBehavior lexiconizeBooleanLiteral(LocalContext context, BooleanLiteral booleanLiteral)
    {
        return new ReturnBehavior(Type.KEYWORD_BOOLEAN, 1);
    }
    private ReturnBehavior lexiconizeStringLiteral(LocalContext context, StringLiteral stringLiteral)
    {
        return new ReturnBehavior(importedTypes.get("String"), 1);
    }

    private ReturnBehavior lexiconizeAddition(LocalContext context, Addition addition)
    {
        return lexiconizeIntOperator(context, addition);
    }
    private ReturnBehavior lexiconizeSubtraction(LocalContext context, Subtraction subtraction)
    {
        return lexiconizeIntOperator(context, subtraction);
    }
    private ReturnBehavior lexiconizeMultiplication(LocalContext context, Multiplication multiplication)
    {
        return lexiconizeIntOperator(context, multiplication);
    }
    private ReturnBehavior lexiconizeDivision(LocalContext context, Division division)
    {
        return lexiconizeIntOperator(context, division);
    }
    private ReturnBehavior lexiconizeIntOperator(LocalContext context, BinaryOperatorElement operator)
    {
        ReturnBehavior returnBehavior = lexiconizeOperator(context, operator, null);
        if (returnBehavior.type != Type.KEYWORD_INT)
            errors.add(new LexicalException());
        return returnBehavior;
    }
    private ReturnBehavior lexiconizeInequality(LocalContext context, Inequality inequality)
    {
        return lexiconizeComparisonOperator(context, inequality);
    }
    private ReturnBehavior lexiconizeEquality(LocalContext context, Equality equality)
    {
        return lexiconizeComparisonOperator(context, equality);
    }
    private ReturnBehavior lexiconizeComparisonOperator(LocalContext context, ComparisonOperator operator)
    {
        operator.label1 = context.nextLabel();
        operator.label2 = context.nextLabel();
        return lexiconizeOperator(context, operator, Type.KEYWORD_BOOLEAN);
    }
    private ReturnBehavior lexiconizeOperator(LocalContext context, BinaryOperatorElement operator, Type returnType)
    {
        ReturnBehavior returnBehavior1 = lexiconizeExpression(context, operator.expression1);
        ReturnBehavior returnBehavior2 = lexiconizeExpression(context, operator.expression2);
        if (returnBehavior1.type != returnBehavior2.type)
            errors.add(new LexicalException());
        int stackRequirement = Math.max(returnBehavior1.stackRequirement, returnBehavior2.stackRequirement + 1);
        return new ReturnBehavior(returnType != null ? returnType : returnBehavior1.type, stackRequirement);
    }

    private Type resolveType(TypeId typeId)
    {
        return importedTypes.get(typeId.toString());
    }

    private Method resolveFunction(ClassContext context, Id id, ReturnBehavior[] argumentSignature)
    {
        // TODO Auto-generated method stub
        return null;
    }

    private static void deleteNulls(ListElement<?> listElement)
    {
        Iterator<?> iterator = listElement.elements.iterator();
        while (iterator.hasNext())
            if (iterator.next() == null)
                iterator.remove();
    }
}
