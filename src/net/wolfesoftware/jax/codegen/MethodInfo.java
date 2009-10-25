package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.lexiconizer.*;

/**
 * JVM instructions: http://java.sun.com/docs/books/jvms/second_edition/html/Instructions2.doc.html
 * 
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
    private final ConstantPool constantPool;
    private final ByteArrayOutputStream codeBufferArray;
    private final DataOutputStream codeBuffer;
    private final LinkedList<Attribute> attributes = new LinkedList<Attribute>();
    @Deprecated private ArrayList<String> exceptionTable = new ArrayList<String>();
    private MethodInfo(Method method, ConstantPool constantPool)
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

    private void internalGenerate(FunctionDefinition functionDefinition)
    {
        evalExpression(functionDefinition.expression);
        _return(functionDefinition.returnBehavior.type);
        attributes.add(Attribute.code(codeBufferArray.toByteArray(), constantPool));
    }

    private void _return(Type type)
    {
        if (!type.isPrimitive())
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
    }

    private void evalExpression(Expression expression)
    {
        ParseElement content = expression.content;
        switch (content.getElementType())
        {
            case IntLiteral.TYPE:
                evalIntLiteral((IntLiteral)content);
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
            case DereferenceMethod.TYPE:
                evalDereferenceMethod((DereferenceMethod)content);
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

    private void evalShortCircuitAnd(ShortCircuitAnd shortCircuitAnd)
    {
        evalExpression(shortCircuitAnd.expression1);
        printStatement("ifne " + shortCircuitAnd.label1);
        printStatement("iconst_0");
        printStatement("goto " + shortCircuitAnd.label2);
        printLabel(shortCircuitAnd.label1);
        evalExpression(shortCircuitAnd.expression2);
        printLabel(shortCircuitAnd.label2);
    }
    private void evalShortCircuitOr(ShortCircuitOr shortCircuitOr)
    {
        evalExpression(shortCircuitOr.expression1);
        printStatement("ifeq " + shortCircuitOr.label1);
        printStatement("iconst_1");
        printStatement("goto " + shortCircuitOr.label2);
        printLabel(shortCircuitOr.label1);
        evalExpression(shortCircuitOr.expression2);
        printLabel(shortCircuitOr.label2);
    }

    private void evalBooleanNot(BooleanNot booleanNot)
    {
        evalExpression(booleanNot.expression);
        printStatement("ifne " + booleanNot.label1);
        printStatement("iconst_1");
        printStatement("goto " + booleanNot.label2);
        printLabel(booleanNot.label1);
        printStatement("iconst_0");
        printLabel(booleanNot.label2);
    }

    private void evalNullExpression(NullExpression nullExpression)
    {
        printStatement("aconst_null");
    }


    private void evalNegation(Negation negation)
    {
        evalExpression(negation.expression);
        printStatement("ineg");
    }

    private void evalReferenceConversion(ReferenceConversion referenceConversion)
    {
        evalExpression(referenceConversion.expression);
        printStatement("checkcast " + referenceConversion.toType.getTypeName());
    }

    private void evalPrimitiveConversion(PrimitiveConversion primitiveConversion)
    {
        evalExpression(primitiveConversion.expression);
        printStatement(primitiveConversion.instruction);
    }

    private void evalWhileLoop(WhileLoop whileLoop)
    {
        printLabel(whileLoop.continueToLabel);
        evalExpression(whileLoop.expression1);
        printStatement("ifeq " + whileLoop.breakToLabel);
        evalExpression(whileLoop.expression2);
        printStatement("goto " + whileLoop.continueToLabel);
        printLabel(whileLoop.breakToLabel);
    }

    private void evalConstructorInvocation(ConstructorInvocation constructorInvocation)
    {
        printStatement("new " + constructorInvocation.constructor.type.getTypeName());
        printStatement("dup");
        evalArguments(constructorInvocation.functionInvocation.arguments);
        printStatement("invokespecial " + constructorInvocation.constructor.getMethodCode());
    }

    private void evalPreIncrement(PreIncrement preIncrement)
    {
        printStatement("iinc " + preIncrement.id.variable.number + " 1");
        evalId(preIncrement.id);
    }
    private void evalPreDecrement(PreDecrement preDecrement)
    {
        printStatement("iinc " + preDecrement.id.variable.number + " -1");
        evalId(preDecrement.id);
    }
    private void evalPostIncrement(PostIncrement postIncrement)
    {
        evalId(postIncrement.id);
        printStatement("iinc " + postIncrement.id.variable.number + " 1");
    }
    private void evalPostDecrement(PostDecrement postDecrement)
    {
        evalId(postDecrement.id);
        printStatement("iinc " + postDecrement.id.variable.number + " -1");
    }

    private void evalForLoop(ForLoop forLoop)
    {
        evalExpression(forLoop.expression1);
        printStatement("goto " + forLoop.initialGotoLabel);
        printLabel(forLoop.continueToLabel);
        evalExpression(forLoop.expression3);
        if (forLoop.expression3.returnBehavior.type != RuntimeType.VOID)
            printStatement("pop");
        printLabel(forLoop.initialGotoLabel);
        evalExpression(forLoop.expression2);
        printStatement("ifeq " + forLoop.breakToLabel);
        evalExpression(forLoop.expression4);
        printStatement("goto " + forLoop.continueToLabel);
        printLabel(forLoop.breakToLabel);
    }


    private void evalArrayDereference(ArrayDereference arrayDereference)
    {
        evalExpression(arrayDereference.expression1);
        evalExpression(arrayDereference.expression2);
        printStatement("aaload");
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
            String typeName = catchBody.variableDeclaration.typeId.type.getTypeCode();
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
        String statement = "getstatic " + staticDereferenceField.field.declaringType.getTypeName() + '/' + staticDereferenceField.id.name + ' ' + staticDereferenceField.field.returnType.getTypeCode();
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

        printStatement(invokeInstruction + functionInvocation.method.getMethodCode());
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
        if (type == RuntimeType.BOOLEAN || type == RuntimeType.BYTE || type == RuntimeType.SHORT || type == RuntimeType.INT || type == RuntimeType.CHAR)
            return "i";
        if (type == RuntimeType.LONG)
            return "l";
        if (type == RuntimeType.FLOAT)
            return "f";
        if (type == RuntimeType.DOUBLE)
            return "d";
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
        for (int i = 0; i < blockContents.elements.size(); i++) {
            Expression element = blockContents.elements.get(i);
            evalExpression(element);
            if (element.returnBehavior.type != RuntimeType.VOID) {
                if (i < blockContents.elements.size() - 1 || blockContents.forceVoid)
                    printStatement("pop");
            }
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
        boolean referenceCompare = !operator.expression1.returnBehavior.type.isPrimitive();
        printStatement("if_" + (referenceCompare ? "a" : "i") + "cmp" + condition + " " + operator.label1);
        printStatement("iconst_0");
        printStatement("goto " + operator.label2);
        printLabel(operator.label1);
        printStatement("iconst_1");
        printLabel(operator.label2);
    }

    private void evalIntLiteral(IntLiteral intLiteral)
    {
        ldc(constantPool.getInteger(intLiteral.value));
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

    private void writeByte(byte value)
    {
        try {
            codeBuffer.writeByte(value);
        } catch (IOException e) {
            throw null;
        }
    }

    private void writeShort(short value)
    {
        try {
            codeBuffer.writeShort(value);
        } catch (IOException e) {
            throw null;
        }
    }

    private void evalFloatLiteral(FloatLiteral floatLiteral)
    {
        printStatement("ldc " + floatLiteral.value);
    }
    private void evalDoubleLiteral(DoubleLiteral doubleLiteral)
    {
        printStatement("ldc2_w " + doubleLiteral.value);
    }
    private void evalBooleanLiteral(BooleanLiteral booleanLiteral)
    {
        printStatement("ldc " + (booleanLiteral.value ? 1 : 0));
    }
    private void evalStringLiteral(StringLiteral stringLiteral)
    {
        printStatement("ldc " + stringLiteral.source);
    }

    private void printStatement(String s)
    {
        throw null;
    }
    private void printLabel(String labelName)
    {
        throw null;
    }

}
