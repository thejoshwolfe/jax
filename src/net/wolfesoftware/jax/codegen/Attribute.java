package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.LinkedList;
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
     * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#1546
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
    public static Attribute code(byte[] codeBytes, RootLocalContext context, LinkedList<Long> exceptionTable, ConstantPool constantPool)
    {
        final short attribute_name_index = constantPool.getUtf8("Code");
        final int attribute_length;
        final short max_stack = (short)context.stackCapacity;
        final short max_locals = (short)context.variableCapacity;
        final int code_length = codeBytes.length;
        final byte[] code = codeBytes;
        final short excpetion_table_length = (short)exceptionTable.size();
        final LinkedList<Long> exception_table = exceptionTable;
        final short attributes_count = 0;
        final Attribute[] attributes = {};
        // "The value of the attribute_length item indicates the length of the attribute, excluding the initial six bytes."
        int attributeLength = 2 + 2 + 4 + code.length + 2 + 4 * exception_table.size() + 2;
        for (Attribute attribute : attributes)
            attributeLength += 6 + attribute.length;
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
                for (Long exception : exception_table)
                    out.writeLong(exception);
                out.writeShort(attributes_count);
                for (Attribute attribute : attributes)
                    attribute.write(out);
            }
        };
    }

    public static Attribute sourceFile(String sourceFile, ConstantPool constantPool)
    {
        final short attribute_name_index = constantPool.getUtf8("SourceFile");
        final int attribute_length = 2;
        final short sourcefile_index = constantPool.getUtf8(sourceFile);
        return new Attribute(attribute_length) {
            public void write(DataOutputStream out) throws IOException
            {
                out.writeShort(attribute_name_index);
                out.writeInt(attribute_length);
                out.writeShort(sourcefile_index);
            }
        };
    }
}
