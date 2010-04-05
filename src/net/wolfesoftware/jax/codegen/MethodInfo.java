package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.semalysis.*;
import net.wolfesoftware.jax.tokenization.Lang;
import net.wolfesoftware.jax.util.Util;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html
 * <p/>
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#1513
 * <pre>method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}</pre>
 * 
 */
public class MethodInfo
{
    public static MethodInfo generate(FunctionDefinition functionDefinition, ConstantPool constantPool)
    {
        MethodInfo methodInfo = new MethodInfo(functionDefinition.method, constantPool);
        methodInfo.internalGenerate(functionDefinition);
        return methodInfo;
    }
    public static MethodInfo generate(ConstructorDefinition constructorDefinition, ConstantPool constantPool)
    {
        MethodInfo methodInfo = new MethodInfo(constructorDefinition.constructor, constantPool);
        methodInfo.internalGenerate(constructorDefinition);
        return methodInfo;
    }

    public static final short
    ACC_PUBLIC = 0x0001,
    ACC_PRIVATE = 0x0002,
    ACC_PROTECTED = 0x0004,
    ACC_STATIC = 0x0008,
    ACC_FINAL = 0x0010,
    ACC_SYNCHRONIZED = 0x0020,
    ACC_NATIVE = 0x0100,
    ACC_ABSTRACT = 0x0400,
    ACC_STRICT = 0x0800;

    private final short access_flags;
    private final short name_index;
    private final short descriptor_index;
    public LinkedList<Long> exception_table = new LinkedList<Long>();
    private final LinkedList<Attribute> attributes = new LinkedList<Attribute>();

    private final ConstantPool constantPool;
    private final ByteArrayOutputStream codeBufferArray;
    private final DataOutputStream codeBuffer;
    private int offset = 0;
    private RootLocalContext context;
    private final ArrayList<Fillin> fillins = new ArrayList<Fillin>();
    private MethodInfo(TakesArguments method, ConstantPool constantPool)
    {
        access_flags = method.getFlags();
        name_index = constantPool.getUtf8(method.getName());
        descriptor_index = constantPool.getUtf8(method.getDescriptor());
        this.constantPool = constantPool;
        codeBufferArray = new ByteArrayOutputStream();
        codeBuffer = new DataOutputStream(codeBufferArray);
    }

    public void write(DataOutputStream out) throws IOException
    {
        out.writeShort(access_flags);
        out.writeShort(name_index);
        out.writeShort(descriptor_index);
        out.writeShort(attributes.size());
        for (Attribute attribute : attributes)
            attribute.write(out);
    }

    private void internalGenerate(ConstructorOrMethodElement functionDefinition)
    {
        context = functionDefinition.context;
        // ensure parameters have proper numbers
        if (!functionDefinition.isStatic())
            context.getLocalVariable(Lang.KEYWORD_THIS).getNumber();
        for (VariableDeclaration variableDeclaration : functionDefinition.argumentDeclarations.elements)
            variableDeclaration.id.variable.getNumber();
        evalExpression(functionDefinition.expression);
        _return(functionDefinition.returnBehavior.type);
        Util._assert(context.stackSize == 0);
        byte[] codeBytes = codeBufferArray.toByteArray();
        for (Fillin fillin : fillins) {
            codeBytes[fillin.address + 0] = (byte)(fillin.value >> 8);
            codeBytes[fillin.address + 1] = (byte)(fillin.value >> 0);
        }
        attributes.add(Attribute.code(codeBytes, context, exception_table, constantPool));
    }

    private void evalExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType()) {
            case IntLiteral.TYPE:
                evalIntLiteral((IntLiteral)content);
                break;
            case LongLiteral.TYPE:
                evalLongLiteral((LongLiteral)content);
                break;
            case FloatLiteral.TYPE:
                evalFloatLiteral((FloatLiteral)content);
                break;
            case DoubleLiteral.TYPE:
                evalDoubleLiteral((DoubleLiteral)content);
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
            case PreIncrement.TYPE:
                evalPreIncrement((PreIncrement)content);
                break;
            case PreDecrement.TYPE:
                evalPreDecrement((PreDecrement)content);
                break;
            case PostIncrement.TYPE:
                evalPostIncrement((PostIncrement)content);
                break;
            case PostDecrement.TYPE:
                evalPostDecrement((PostDecrement)content);
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
            case ShortCircuitAnd.TYPE:
                evalShortCircuitAnd((ShortCircuitAnd)content);
                break;
            case ShortCircuitOr.TYPE:
                evalShortCircuitOr((ShortCircuitOr)content);
                break;
            case Negation.TYPE:
                evalNegation((Negation)content);
                break;
            case BooleanNot.TYPE:
                evalBooleanNot((BooleanNot)content);
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
                // do nothing
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
            case ForLoop.TYPE:
                evalForLoop((ForLoop)content);
                break;
            case WhileLoop.TYPE:
                evalWhileLoop((WhileLoop)content);
                break;
            case FunctionInvocation.TYPE:
                evalFunctionInvocation((FunctionInvocation)content);
                break;
            case ConstructorInvocation.TYPE:
                evalConstructorInvocation((ConstructorInvocation)content);
                break;
            case ConstructorRedirect.TYPE:
                evalConstructorRedirect((ConstructorRedirect)content);
                break;
            case DereferenceMethod.TYPE:
                evalDereferenceMethod((DereferenceMethod)content);
                break;
            case DereferenceField.TYPE:
                evalDereferenceField((DereferenceField)content);
                break;
            case StaticDereferenceField.TYPE:
                evalStaticDereferenceField((StaticDereferenceField)content);
                break;
            case StaticFunctionInvocation.TYPE:
                evalStaticFunctionInvocation((StaticFunctionInvocation)content);
                break;
            case ArrayDereference.TYPE:
                evalArrayDereference((ArrayDereference)content);
                break;
            case TryCatch.TYPE:
                evalTryCatch((TryCatch)content);
                break;
            case PrimitiveConversion.TYPE:
                evalPrimitiveConversion((PrimitiveConversion)content);
                break;
            case ReferenceConversion.TYPE:
                evalReferenceConversion((ReferenceConversion)content);
                break;
            case NullExpression.TYPE:
                evalNullExpression((NullExpression)content);
                break;
            default:
                throw new RuntimeException(content.getClass().toString());
        }
    }

    private void evalConstructorRedirect(ConstructorRedirect constructorRedirect)
    {
        aload(0);
        context.pushOperand(RuntimeType.OBJECT); // this is a lie, but whatever
        evalArguments(constructorRedirect.arguments);
        writeByte(Instructions.invokespecial);
        context.popOperands(1); // TODO hard-coded for default constructor
        short index = constantPool.getMethod(constructorRedirect.constructor);
        writeShort(index);
    }
    private void evalShortCircuitAnd(ShortCircuitAnd shortCircuitAnd)
    {
        evalExpression(shortCircuitAnd.expression1);
        int ifOffset = offset;
        writeByte(Instructions.ifne);
        context.popOperand();
        writeDummyShort();
        writeByte(Instructions.iconst_0);
        // a boolean goes on the stack here, but we'll let the second expression leave it's boolean on the stack
        int gotoOffset = offset;
        writeByte(Instructions._goto);
        writeDummyShort();
        fillins.add(new Fillin(ifOffset));
        evalExpression(shortCircuitAnd.expression2);
        fillins.add(new Fillin(gotoOffset));
    }
    private void evalShortCircuitOr(ShortCircuitOr shortCircuitOr)
    {
        evalExpression(shortCircuitOr.expression1);
        int ifOffset = offset;
        writeByte(Instructions.ifeq);
        context.popOperand();
        writeDummyShort();
        writeByte(Instructions.iconst_1);
        // a boolean goes on the stack here, but we'll let the second expression leave it's boolean on the stack
        int gotoOffset = offset;
        writeByte(Instructions._goto);
        writeDummyShort();
        fillins.add(new Fillin(ifOffset));
        evalExpression(shortCircuitOr.expression2);
        fillins.add(new Fillin(gotoOffset));
    }

    private void evalBooleanNot(BooleanNot booleanNot)
    {
        evalExpression(booleanNot.expression);
        writeByte(Instructions.ifne);
        writeShort((short)7); // skip to the iconst_0
        writeByte(Instructions.iconst_1);
        writeByte(Instructions._goto);
        writeShort((short)4); // jump over the iconst_0
        writeByte(Instructions.iconst_0);
    }

    private void evalNullExpression(NullExpression nullExpression)
    {
        writeShort(Instructions.aconst_null);
        context.pushOperand(NullType.INSTANCE);
    }

    private void evalNegation(Negation negation)
    {
        evalExpression(negation.expression);
        writeByte(negation.instruction);
    }

    private void evalReferenceConversion(ReferenceConversion referenceConversion)
    {
        evalExpression(referenceConversion.expression);
        writeByte(Instructions.checkcast);
        writeShort(constantPool.getClass(referenceConversion.toType));
    }

    private void evalPrimitiveConversion(PrimitiveConversion primitiveConversion)
    {
        evalExpression(primitiveConversion.expression);
        writeByte(primitiveConversion.instruction);
        context.popOperand();
        context.pushOperand(primitiveConversion.toType);
    }

    private void evalWhileLoop(WhileLoop whileLoop)
    {
        int continueToOffset = offset;
        evalExpression(whileLoop.expression1);
        int ifOffset = offset;
        writeByte(Instructions.ifeq);
        context.popOperand();
        writeDummyShort();
        evalExpression(whileLoop.expression2);
        short continueToDelta = (short)(continueToOffset - offset);
        writeByte(Instructions._goto);
        writeShort(continueToDelta);
        fillins.add(new Fillin(ifOffset));
    }

    private void evalConstructorInvocation(ConstructorInvocation constructorInvocation)
    {
        writeByte(Instructions._new);
        writeShort(constantPool.getClass(constructorInvocation.constructor.declaringType));
        context.pushOperand(constructorInvocation.constructor.declaringType);
        writeByte(Instructions.dup);
        context.pushOperand(RuntimeType.OBJECT); // this is a lie, but whatever
        evalArguments(constructorInvocation.functionInvocation.arguments);
        writeByte(Instructions.invokespecial);
        writeShort(constantPool.getMethod(constructorInvocation.constructor));
        context.popOperands(1 + constructorInvocation.functionInvocation.arguments.elements.size());
    }

    private void evalPreIncrement(PreIncrement preIncrement)
    {
        writeByte(Instructions.iinc);
        writeByte((byte)preIncrement.id.variable.getNumber());
        writeByte((byte)1);
        evalId(preIncrement.id);
    }
    private void evalPreDecrement(PreDecrement preDecrement)
    {
        writeByte(Instructions.iinc);
        writeByte((byte)preDecrement.id.variable.getNumber());
        writeByte((byte)-1);
        evalId(preDecrement.id);
    }
    private void evalPostIncrement(PostIncrement postIncrement)
    {
        evalId(postIncrement.id);
        writeByte(Instructions.iinc);
        writeByte((byte)postIncrement.id.variable.getNumber());
        writeByte((byte)1);
    }
    private void evalPostDecrement(PostDecrement postDecrement)
    {
        evalId(postDecrement.id);
        writeByte(Instructions.iinc);
        writeByte((byte)postDecrement.id.variable.getNumber());
        writeByte((byte)-1);
    }

    private void evalForLoop(ForLoop forLoop)
    {
        evalExpression(forLoop.expression1);
        int initialGotoOffset = offset;
        writeByte(Instructions._goto);
        writeDummyShort();
        int continueToOffset = offset;
        evalExpression(forLoop.expression3);
        if (forLoop.expression3.returnBehavior.type != RuntimeType.VOID)
            pop(forLoop.expression3.returnBehavior.type);
        fillins.add(new Fillin(initialGotoOffset));
        evalExpression(forLoop.expression2);
        int breakToOffset = offset;
        writeByte(Instructions.ifeq);
        context.popOperand();
        writeDummyShort();
        evalExpression(forLoop.expression4);
        short continueToDelta = (short)(continueToOffset - offset);
        writeByte(Instructions._goto);
        writeShort(continueToDelta);
        fillins.add(new Fillin(breakToOffset));
    }

    private void evalArrayDereference(ArrayDereference arrayDereference)
    {
        evalExpression(arrayDereference.expression1);
        evalExpression(arrayDereference.expression2);
        Type type = ((ArrayType)arrayDereference.expression1.returnBehavior.type).scalarType;
        if (!type.isPrimitive())
            writeByte(Instructions.aaload);
        else if (type == RuntimeType.INT)
            writeByte(Instructions.iaload);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.laload);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.faload);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.daload);
        else if (type == RuntimeType.BYTE || type == RuntimeType.BOOLEAN)
            writeByte(Instructions.baload);
        else if (type == RuntimeType.CHAR)
            writeByte(Instructions.caload);
        else if (type == RuntimeType.SHORT)
            writeByte(Instructions.saload);
        else
            throw null;
        context.popOperands(2);
        context.pushOperand(type);
    }

    private void evalStaticFunctionInvocation(StaticFunctionInvocation staticFunctionInvocation)
    {
        evalFunctionInvocation(staticFunctionInvocation.functionInvocation);
    }

    private void evalTryCatch(TryCatch tryCatch)
    {
        // preserve operand stack
        ArrayList<SecretLocalVariable> preservedOperands = new ArrayList<SecretLocalVariable>();
        while (!context.isOperandStackEmpty()) {
            Type operandType = context.popOperand();
            SecretLocalVariable secretLocalVariable = context.addSecretLocalVariable(operandType);
            store(secretLocalVariable.type, secretLocalVariable.getNumber());
            preservedOperands.add(secretLocalVariable);
        }

        tryCatch.tryPart.startOffset = offset;
        evalExpression(tryCatch.tryPart.expression);
        if (tryCatch.tryPart.expression.returnBehavior.type != RuntimeType.VOID)
            context.popOperand(); // take this off for now. it's added as part of the last catch
        int successfulTryEndOffset = offset;
        tryCatch.tryPart.endOffset = offset;
        writeByte(Instructions._goto);
        writeDummyShort();
        evalCatchPart(tryCatch.catchPart);
        fillins.add(new Fillin(successfulTryEndOffset));
        List<CatchBody> catchElements = tryCatch.catchPart.catchList.elements;
        for (int i = 0; i < catchElements.size() - 1; i++)
            fillins.add(new Fillin(catchElements.get(i).endGotoOffset));

        // restore operand stack
        for (int i = preservedOperands.size() - 1; i >= 0; i--) {
            SecretLocalVariable secretLocalVariable = preservedOperands.get(i);
            load(secretLocalVariable.type, secretLocalVariable.getNumber());
        }

        for (CatchBody catchBody : catchElements) {
            Type type = catchBody.variableDeclaration.typeId.type;
            short start_pc = (short)tryCatch.tryPart.startOffset;
            short end_pc = (short)tryCatch.tryPart.endOffset;
            short handler_pc = (short)catchBody.startOffset;
            short catch_type = constantPool.getClass(type);
            long exception = (long)start_pc << 48 | (long)end_pc << 32 | (long)handler_pc << 16 | (long)catch_type << 0;
            exception_table.add(exception);
        }
    }

    private void evalCatchPart(CatchPart catchPart)
    {
        evalCatchList(catchPart.catchList);
    }

    private void evalCatchList(CatchList catchList)
    {
        for (int i = 0; i < catchList.elements.size(); i++)
            evalCatchBody(catchList.elements.get(i), i < catchList.elements.size() - 1);
    }

    private void evalCatchBody(CatchBody catchBody, boolean addGoto)
    {
        context.pushOperand(RuntimeType.OBJECT); // exception object
        catchBody.startOffset = offset;
        astore(catchBody.variableDeclaration.id.variable.getNumber());
        evalExpression(catchBody.expression);
        if (addGoto) {
            if (catchBody.expression.returnBehavior.type != RuntimeType.VOID)
                context.popOperand(); // take this off for now
            catchBody.endGotoOffset = offset;
            writeByte(Instructions._goto);
            writeDummyShort();
        }
    }

    private void evalStaticDereferenceField(StaticDereferenceField staticDereferenceField)
    {
        writeByte(Instructions.getstatic);
        writeShort(constantPool.getField(staticDereferenceField.field));
        context.pushOperand(staticDereferenceField.field.returnType);
    }

    private void evalDereferenceMethod(DereferenceMethod dereferenceMethod)
    {
        evalExpression(dereferenceMethod.expression);
        evalFunctionInvocation(dereferenceMethod.functionInvocation);
    }

    private void evalDereferenceField(DereferenceField dereferenceField)
    {
        evalExpression(dereferenceField.expression);
        if (dereferenceField.field.isArrayLength())
            writeByte(Instructions.arraylength);
        else {
            writeByte(Instructions.getfield);
            writeShort(constantPool.getField(dereferenceField.field));
        }
        context.popOperand();
        context.pushOperand(dereferenceField.field.returnType);
    }

    private void evalFunctionInvocation(FunctionInvocation functionInvocation)
    {
        evalArguments(functionInvocation.arguments);

        Method method = functionInvocation.method;
        if (method.declaringType.isInterface()) {
            // interface method
            writeByte(Instructions.invokeinterface);
            writeShort(constantPool.getInterfaceMethod(functionInvocation.method));
            // this is dumb. "The redundancy is historical."
            int stackSize = 0;
            for (Expression expression : functionInvocation.arguments.elements)
                stackSize += expression.returnBehavior.type.getSize();
            if (!method.isStatic)
                stackSize += 1;
            writeByte((byte)stackSize);
            writeByte((byte)0);
        } else {
            // non-interface method
            // TODO: "special handling for superclass, private, and instance initialization method invocations"
            byte invokeInstruction;
            if (method.isStatic)
                invokeInstruction = Instructions.invokestatic;
            else
                invokeInstruction = Instructions.invokevirtual;
            writeByte(invokeInstruction);
            writeShort(constantPool.getMethod(functionInvocation.method));
        }
        context.popOperands(functionInvocation.arguments.elements.size());
        if (!method.isStatic)
            context.popOperand();
        context.pushOperand(method.returnType);
    }

    private void evalArguments(Arguments arguments)
    {
        for (Expression element : arguments.elements)
            evalExpression(element);
    }

    private void evalIfThenElse(IfThenElse ifThenElse)
    {
        evalExpression(ifThenElse.expression1);
        int ifOffset = offset;
        writeByte(Instructions.ifeq);
        context.popOperand();
        writeDummyShort();
        evalExpression(ifThenElse.expression2);
        if (ifThenElse.expression2.returnBehavior.type != RuntimeType.VOID)
            context.popOperand(); // take this off for now
        int gotoOffset = offset;
        writeByte(Instructions._goto);
        writeDummyShort();
        fillins.add(new Fillin(ifOffset));
        evalExpression(ifThenElse.expression3); // leave this stack size on
        fillins.add(new Fillin(gotoOffset));
    }
    private void evalIfThen(IfThen ifThen)
    {
        evalExpression(ifThen.expression1);
        int ifOffset = offset;
        writeByte(Instructions.ifeq);
        context.popOperand();
        writeDummyShort();
        evalExpression(ifThen.expression2);
        fillins.add(new Fillin(ifOffset));
    }

    private void evalAssignment(Assignment assignment)
    {
        evalExpression(assignment.expression);
        dup(assignment.expression.returnBehavior.type);
        store(assignment.id.variable.type, assignment.id.variable.getNumber());
    }

    private void evalVariableCreation(VariableCreation variableCreation)
    {
        evalExpression(variableCreation.expression);
        LocalVariable variable = variableCreation.variableDeclaration.id.variable;
        store(variable.type, variable.getNumber());
    }
    private void evalId(Id id)
    {
        Type type = translateTypeForInstructions(id.variable.type);
        if (!type.isPrimitive())
            aload(id.variable.getNumber());
        else if (type == RuntimeType.INT)
            iload(id.variable.getNumber());
        else if (type == RuntimeType.LONG)
            lload(id.variable.getNumber());
        else if (type == RuntimeType.FLOAT)
            fload(id.variable.getNumber());
        else if (type == RuntimeType.DOUBLE)
            dload(id.variable.getNumber());
        else
            throw null;
        context.pushOperand(type);
    }

    private void evalBlock(Block block)
    {
        evalBlockContents(block.blockContents);
    }

    private void evalBlockContents(BlockContents blockContents)
    {
        for (int i = 0; i < blockContents.elements.size(); i++) {
            Expression element = blockContents.elements.get(i);
            evalExpression(element);
            if (element.returnBehavior.type != RuntimeType.VOID)
                if (i < blockContents.elements.size() - 1 || blockContents.forceVoid)
                    pop(element.returnBehavior.type);
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
        Type type = addition.type;
        if (type == RuntimeType.INT)
            writeByte(Instructions.iadd);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.ladd);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.fadd);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.dadd);
        else
            throw null;
        modifyOperatorStack(addition);
    }
    private void evalSubtraction(Subtraction subtraction)
    {
        evalExpression(subtraction.expression1);
        evalExpression(subtraction.expression2);
        Type type = subtraction.type;
        if (type == RuntimeType.INT)
            writeByte(Instructions.isub);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.lsub);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.fsub);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.dsub);
        else
            throw null;
        modifyOperatorStack(subtraction);
    }
    private void evalMultiplication(Multiplication multiplication)
    {
        evalExpression(multiplication.expression1);
        evalExpression(multiplication.expression2);
        Type type = multiplication.type;
        if (type == RuntimeType.INT)
            writeByte(Instructions.imul);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.lmul);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.fmul);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.dmul);
        else
            throw null;
        modifyOperatorStack(multiplication);
    }
    private void evalDivision(Division division)
    {
        evalExpression(division.expression1);
        evalExpression(division.expression2);
        Type type = division.type;
        if (type == RuntimeType.INT)
            writeByte(Instructions.idiv);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.ldiv);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.fdiv);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.ddiv);
        else
            throw null;
        modifyOperatorStack(division);
    }
    private void evalLessThan(LessThan lessThan)
    {
        evalComparison(lessThan, Instructions.if_icmplt);
    }
    private void evalGreaterThan(GreaterThan greaterThan)
    {
        evalComparison(greaterThan, Instructions.if_icmpgt);
    }
    private void evalLessThanOrEqual(LessThanOrEqual lessThanOrEqual)
    {
        evalComparison(lessThanOrEqual, Instructions.if_icmple);
    }
    private void evalGreaterThanOrEqual(GreaterThanOrEqual greaterThanOrEqual)
    {
        evalComparison(greaterThanOrEqual, Instructions.if_icmpge);
    }
    private void evalEquality(Equality equality)
    {
        byte instruction = equality.expression1.returnBehavior.type.isPrimitive() ? Instructions.if_icmpeq : Instructions.if_acmpeq;
        evalComparison(equality, instruction);
    }
    private void evalInequality(Inequality inequality)
    {
        byte instruction = inequality.expression1.returnBehavior.type.isPrimitive() ? Instructions.if_icmpne : Instructions.if_acmpne;
        evalComparison(inequality, instruction);
    }
    private void evalComparison(ComparisonOperator operator, byte instruction)
    {
        evalExpression(operator.expression1);
        evalExpression(operator.expression2);
        writeByte(instruction);
        writeShort((short)7); // jump forward to the iconst_1
        writeByte(Instructions.iconst_0);
        writeByte(Instructions._goto);
        writeShort((short)4); // jump over the iconst_1
        writeByte(Instructions.iconst_1);
        operator.type = RuntimeType.BOOLEAN;
        modifyOperatorStack(operator);
    }
    private void modifyOperatorStack(BinaryOperatorElement operator)
    {
        context.popOperand();
        context.popOperand();
        context.pushOperand(operator.type);
    }

    private void evalIntLiteral(IntLiteral intLiteral)
    {
        // TODO use iconst_<n> sometimes
        ldc(constantPool.getInteger(intLiteral.value));
        context.pushOperand(RuntimeType.INT);
    }
    private void evalLongLiteral(LongLiteral longLiteral)
    {
        writeByte(Instructions.ldc2_w);
        writeShort(constantPool.getLong(longLiteral.value));
        context.pushOperand(RuntimeType.LONG);
    }
    private void evalFloatLiteral(FloatLiteral floatLiteral)
    {
        ldc(constantPool.getFloat(floatLiteral.value));
        context.pushOperand(RuntimeType.FLOAT);
    }
    private void evalDoubleLiteral(DoubleLiteral doubleLiteral)
    {
        writeByte(Instructions.ldc2_w);
        writeShort(constantPool.getDouble(doubleLiteral.value));
        context.pushOperand(RuntimeType.DOUBLE);
    }
    private void evalBooleanLiteral(BooleanLiteral booleanLiteral)
    {
        writeByte(booleanLiteral.value ? Instructions.iconst_1 : Instructions.iconst_0);
        context.pushOperand(RuntimeType.BOOLEAN);
    }
    private void evalStringLiteral(StringLiteral stringLiteral)
    {
        ldc(constantPool.getString(stringLiteral.value));
        context.pushOperand(RuntimeType.STRING);
    }

    private Type translateTypeForInstructions(Type type)
    {
        if (type == RuntimeType.BOOLEAN || type == RuntimeType.BYTE || type == RuntimeType.SHORT || type == RuntimeType.CHAR)
            return RuntimeType.INT;
        return type;
    }
    private void ldc(short index)
    {
        if (index <= 0xFF) {
            writeByte(Instructions.ldc);
            writeByte((byte)index);
        } else {
            writeByte(Instructions.ldc_w);
            writeShort(index);
        }
    }
    private void pop(Type type)
    {
        switch (type.getSize()) {
            case 1:
                writeByte(Instructions.pop);
                break;
            case 2:
                writeByte(Instructions.pop2);
                break;
            default:
                throw null;
        }
        context.popOperand();
    }
    private void dup(Type type)
    {
        switch (type.getSize()) {
            case 1:
                writeByte(Instructions.dup);
                break;
            case 2:
                writeByte(Instructions.dup2);
                break;
            default:
                throw null;
        }
        context.pushOperand(type);
    }
    private void store(Type type, int number)
    {
        type = translateTypeForInstructions(type);
        if (!type.isPrimitive())
            astore(number);
        else if (type == RuntimeType.INT)
            istore(number);
        else if (type == RuntimeType.LONG)
            lstore(number);
        else if (type == RuntimeType.FLOAT)
            fstore(number);
        else if (type == RuntimeType.DOUBLE)
            dstore(number);
        else
            throw null;
    }
    private void astore(int number)
    {
        switch (number) {
            case 0:
                writeByte(Instructions.astore_0);
                break;
            case 1:
                writeByte(Instructions.astore_1);
                break;
            case 2:
                writeByte(Instructions.astore_2);
                break;
            case 3:
                writeByte(Instructions.astore_3);
                break;
            default:
                writeByte(Instructions.astore);
                writeByte((byte)number);
                break;
        }
        context.popOperand();
    }
    private void istore(int number)
    {
        switch (number) {
            case 0:
                writeByte(Instructions.istore_0);
                break;
            case 1:
                writeByte(Instructions.istore_1);
                break;
            case 2:
                writeByte(Instructions.istore_2);
                break;
            case 3:
                writeByte(Instructions.istore_3);
                break;
            default:
                writeByte(Instructions.istore);
                writeByte((byte)number);
                break;
        }
        context.popOperand();
    }
    private void lstore(int number)
    {
        switch (number) {
            case 0:
                writeByte(Instructions.lstore_0);
                break;
            case 1:
                writeByte(Instructions.lstore_1);
                break;
            case 2:
                writeByte(Instructions.lstore_2);
                break;
            case 3:
                writeByte(Instructions.lstore_3);
                break;
            default:
                writeByte(Instructions.lstore);
                writeByte((byte)number);
                break;
        }
        context.popOperand();
    }
    private void fstore(int number)
    {
        switch (number) {
            case 0:
                writeByte(Instructions.fstore_0);
                break;
            case 1:
                writeByte(Instructions.fstore_1);
                break;
            case 2:
                writeByte(Instructions.fstore_2);
                break;
            case 3:
                writeByte(Instructions.fstore_3);
                break;
            default:
                writeByte(Instructions.fstore);
                writeByte((byte)number);
                break;
        }
        context.popOperand();
    }
    private void dstore(int number)
    {
        switch (number) {
            case 0:
                writeByte(Instructions.dstore_0);
                break;
            case 1:
                writeByte(Instructions.dstore_1);
                break;
            case 2:
                writeByte(Instructions.dstore_2);
                break;
            case 3:
                writeByte(Instructions.dstore_3);
                break;
            default:
                writeByte(Instructions.dstore);
                writeByte((byte)number);
                break;
        }
        context.popOperand();
    }

    private void load(Type type, int index)
    {
        type = translateTypeForInstructions(type);
        if (!type.isPrimitive())
            aload(index);
        else if (type == RuntimeType.INT)
            iload(index);
        else if (type == RuntimeType.LONG)
            lload(index);
        else if (type == RuntimeType.FLOAT)
            fload(index);
        else if (type == RuntimeType.DOUBLE)
            dload(index);
        else
            throw null;
    }
    private void aload(int index)
    {
        switch (index) {
            case 0:
                writeByte(Instructions.aload_0);
                break;
            case 1:
                writeByte(Instructions.aload_1);
                break;
            case 2:
                writeByte(Instructions.aload_2);
                break;
            case 3:
                writeByte(Instructions.aload_3);
                break;
            default:
                writeByte(Instructions.aload);
                writeByte((byte)index);
                break;
        }
    }
    private void iload(int index)
    {
        switch (index) {
            case 0:
                writeByte(Instructions.iload_0);
                break;
            case 1:
                writeByte(Instructions.iload_1);
                break;
            case 2:
                writeByte(Instructions.iload_2);
                break;
            case 3:
                writeByte(Instructions.iload_3);
                break;
            default:
                writeByte(Instructions.iload);
                writeByte((byte)index);
                break;
        }
    }
    private void lload(int index)
    {
        switch (index) {
            case 0:
                writeByte(Instructions.lload_0);
                break;
            case 1:
                writeByte(Instructions.lload_1);
                break;
            case 2:
                writeByte(Instructions.lload_2);
                break;
            case 3:
                writeByte(Instructions.lload_3);
                break;
            default:
                writeByte(Instructions.lload);
                writeByte((byte)index);
                break;
        }
    }
    private void fload(int index)
    {
        switch (index) {
            case 0:
                writeByte(Instructions.fload_0);
                break;
            case 1:
                writeByte(Instructions.fload_1);
                break;
            case 2:
                writeByte(Instructions.fload_2);
                break;
            case 3:
                writeByte(Instructions.fload_3);
                break;
            default:
                writeByte(Instructions.fload);
                writeByte((byte)index);
                break;
        }
    }
    private void dload(int index)
    {
        switch (index) {
            case 0:
                writeByte(Instructions.dload_0);
                break;
            case 1:
                writeByte(Instructions.dload_1);
                break;
            case 2:
                writeByte(Instructions.dload_2);
                break;
            case 3:
                writeByte(Instructions.dload_3);
                break;
            default:
                writeByte(Instructions.dload);
                writeByte((byte)index);
                break;
        }
    }
    private void _return(Type type)
    {
        if (!type.isPrimitive())
            writeByte(Instructions.areturn);
        else if (type == RuntimeType.VOID)
            writeByte(Instructions._return);
        else if (type == RuntimeType.BOOLEAN || type == RuntimeType.BYTE || type == RuntimeType.SHORT || type == RuntimeType.INT || type == RuntimeType.CHAR)
            writeByte(Instructions.ireturn);
        else if (type == RuntimeType.LONG)
            writeByte(Instructions.lreturn);
        else if (type == RuntimeType.FLOAT)
            writeByte(Instructions.freturn);
        else if (type == RuntimeType.DOUBLE)
            writeByte(Instructions.dreturn);
        else
            throw null;
        if (type != RuntimeType.VOID)
            context.popOperand();
    }
    private void writeByte(byte value)
    {
        try {
            codeBuffer.writeByte(value);
            offset++;
        } catch (IOException e) {
            throw null;
        }
    }
    private void writeDummyShort()
    {
        writeShort((short)0);
    }
    private void writeShort(short value)
    {
        try {
            codeBuffer.writeShort(value);
            offset += 2;
        } catch (IOException e) {
            throw null;
        }
    }
    private class Fillin
    {
        public final int address;
        public final short value;
        public Fillin(int branchInstructionOffset)
        {
            address = branchInstructionOffset + 1;
            value = (short)(offset - branchInstructionOffset);
        }
    }
}
