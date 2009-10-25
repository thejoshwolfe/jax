package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.*;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.lexiconizer.LocalType;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html
 * <pre>ClassFile {
    u4 magic;
    u2 minor_version;
    u2 major_version;
    u2 constant_pool_count;
    cp_info constant_pool[constant_pool_count-1];
    u2 access_flags;
    u2 this_class;
    u2 super_class;
    u2 interfaces_count;
    u2 interfaces[interfaces_count];
    u2 fields_count;
    field_info fields[fields_count];
    u2 methods_count;
    method_info methods[methods_count];
    u2 attributes_count;
    attribute_info attributes[attributes_count];
}</pre>
 */
public class ClassFile
{
    public static final short ACC_PUBLIC = 0x0001, ACC_FINAL = 0x0010, ACC_SUPER = 0x0020, ACC_INTERFANCE = 0x0200, ACC_ABSTRACT = 0x0400;

    public static ClassFile generate(ClassDeclaration classDeclaration)
    {
        ClassFile classFile = new ClassFile(classDeclaration.localType);
        classFile.generate(classDeclaration.classBody);
        return classFile;
    }

    private static final int magic = 0xCAFEBABE;
    private static final short minor_version = 3, major_version = 45; // taken from jasmin source code. dunno what it means
    private final ConstantPool constant_pool = new ConstantPool();
    private final short access_flags;
    private final LinkedList<String> interfaces = new LinkedList<String>();
    private final LinkedList<FieldInfo> fields = new LinkedList<FieldInfo>();
    private final LinkedList<MethodInfo> methods = new LinkedList<MethodInfo>();
    private final LinkedList<Attribute> attributes = new LinkedList<Attribute>();

    private ClassFile(LocalType localType)
    {
        short accessFlags = localType.getFlags();
        // "All new compilers to the instruction set of the Java virtual machine should set the ACC_SUPER flag."
        accessFlags |= ACC_SUPER;
        this.access_flags = accessFlags;
    }

    public void write(DataOutputStream out) throws IOException
    {
        out.writeInt(magic);
        out.writeShort(minor_version);
        out.writeShort(major_version);
        constant_pool.write(out);
        out.writeShort(access_flags);

        out.writeShort(interfaces.size());
        for (String _interface : interfaces)
            out.writeShort(constant_pool.getClass(_interface));

        out.writeShort(fields.size());
        for (FieldInfo field : fields)
            field.write(out);

        out.writeShort(methods.size());
        for (MethodInfo method : methods)
            method.write(out);

        out.writeShort(attributes.size());
        for (Attribute attribute : attributes)
            attribute.write(out);
    }

    private void generate(ClassBody classBody)
    {
        for (ClassMember element : classBody.elements)
            genClassMember(element);
    }

    private void genClassMember(ClassMember classMember)
    {
        ParseElement content = classMember.content;
        switch (content.getElementType())
        {
            case FunctionDefinition.TYPE:
                methods.add(MethodInfo.generate((FunctionDefinition)content, constant_pool));
                break;
            default:
                throw new RuntimeException();
        }
    }
}
