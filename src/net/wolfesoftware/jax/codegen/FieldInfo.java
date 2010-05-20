package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.LinkedList;
import net.wolfesoftware.jax.ast.FieldDeclaration;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#2877
 * <p/>
 * <pre>field_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}</pre>
 */
public class FieldInfo
{
    public static FieldInfo generate(FieldDeclaration fieldDeclaration, ConstantPool constantPool)
    {
        return new FieldInfo(fieldDeclaration, constantPool);
    }

    private short access_flags;
    private short name_index;
    private short descriptor_index;
    private final LinkedList<Attribute> attributes = new LinkedList<Attribute>();

    private FieldInfo(FieldDeclaration fieldDeclaration, ConstantPool constantPool)
    {
        access_flags = fieldDeclaration.fieldModifiers.bitmask;
        name_index = constantPool.getUtf8(fieldDeclaration.fieldName);
        descriptor_index = constantPool.getUtf8(fieldDeclaration.field.getDescriptor());
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
}
