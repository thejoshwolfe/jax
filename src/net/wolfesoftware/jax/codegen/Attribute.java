package net.wolfesoftware.jax.codegen;

import java.io.*;
import net.wolfesoftware.jax.lexiconizer.RootLocalContext;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#43817
 */
public abstract class Attribute
{
    public final int length;
    private Attribute(int length)
    {
        this.length = length;
    }

    public abstract void write(DataOutputStream out) throws IOException;

    /**
     * <pre>Code_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 max_stack;
    u2 max_locals;
    u4 code_length;
    u1 code[code_length];
    u2 exception_table_length;
    {       u2 start_pc;
            u2 end_pc;
            u2  handler_pc;
            u2  catch_type;
    }   exception_table[exception_table_length];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}</pre>
     */
    public static Attribute code(byte[] codeBytes, RootLocalContext context, ConstantPool constantPool)
    {
        final short attribute_name_index = constantPool.getUtf8("Code");
        final int attribute_length;
        final short max_stack = (short)context.stackCapacity;
        final short max_locals = (short)context.variableCapacity;
        final int code_length = codeBytes.length;
        final byte[] code = codeBytes;
        final short excpetion_table_length = (short)context.exceptionTable.size();
        final short[][] excpetion_table = context.exceptionTable.toArray(new short[excpetion_table_length][]);
        final short attributes_count = 0;
        final Attribute[] attributes = {};
        int attributeLength = 2 + 4 + 2 + 2 + 4 + code.length + 2 + (2 + 2 + 2 + 2) * excpetion_table.length + 2;
        for (Attribute attribute : attributes)
            attributeLength += attribute.length;
        attribute_length = attributeLength;
        return new Attribute(attribute_length) {
            public void write(DataOutputStream out) throws IOException
            {
                out.writeShort(attribute_name_index);
                out.writeInt(attribute_length);
                out.writeShort(max_stack);
                out.writeShort(max_locals);
                out.writeInt(code_length);
                out.write(code);
                out.writeShort(excpetion_table_length);
                for (short[] exception : excpetion_table)
                    for (short s : exception)
                        out.writeShort(s);
                out.writeShort(attributes_count);
                for (Attribute attribute : attributes)
                    attribute.write(out);
            }
        };
    }
}
