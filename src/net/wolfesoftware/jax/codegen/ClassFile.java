package net.wolfesoftware.jax.codegen;

import java.io.*;
import java.util.LinkedList;
import net.wolfesoftware.jax.ast.*;
import net.wolfesoftware.jax.semalysis.*;

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
    public static ClassFile generate(String sourceFile, ClassDeclaration classDeclaration)
    {
        ClassFile classFile = new ClassFile(sourceFile, classDeclaration.localType);
        classFile.internalGenerate(classDeclaration);
        return classFile;
    }

    private static final int magic = 0xCAFEBABE;
    private static final short minor_version = 0, major_version = 50;
    private final ConstantPool constant_pool = new ConstantPool();
    private final short access_flags;
    private final short this_class;
    private final short super_class;
    private final short[] interfaces;
    private final LinkedList<FieldInfo> fields = new LinkedList<FieldInfo>();
    private final LinkedList<MethodInfo> methods = new LinkedList<MethodInfo>();
    private final LinkedList<Attribute> attributes = new LinkedList<Attribute>();

    private ClassFile(String sourceFile, LocalType type)
    {
        // "All new compilers to the instruction set of the Java virtual machine should set the ACC_SUPER flag."
        access_flags = (short)(type.getFlags() | Modifier.ACC_SUPER);
        this_class = constant_pool.getClass(type);
        super_class = constant_pool.getClass(type.getParent());
        Type[] interfaces = type.getInterfaces();
        this.interfaces = new short[interfaces.length];
        for (int i = 0; i < interfaces.length; i++)
            this.interfaces[i] = constant_pool.getClass(interfaces[i]);
        attributes.add(Attribute.sourceFile(sourceFile, constant_pool));
    }

    public void write(DataOutputStream out)
    {
        try {
            out.writeInt(magic);
            out.writeShort(minor_version);
            out.writeShort(major_version);
            
            constant_pool.write(out);
            
            out.writeShort(access_flags);
            out.writeShort(this_class);
            out.writeShort(super_class);
            
            out.writeShort(interfaces.length);
            for (Short index : interfaces)
                out.writeShort(index);
            
            out.writeShort(fields.size());
            for (FieldInfo field : fields)
                field.write(out);
            
            out.writeShort(methods.size());
            for (MethodInfo method : methods)
                method.write(out);
            
            out.writeShort(attributes.size());
            for (Attribute attribute : attributes)
                attribute.write(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void internalGenerate(ClassDeclaration classDeclaration)
    {
        if (!classDeclaration.localType.getStaticInitializerExpressions().isEmpty())
            methods.add(MethodInfo.generate(classDeclaration.localType.staticInitializer, constant_pool));

        for (ClassMember element : classDeclaration.classBody.elements) {
            ParseElement content = element.content;
            switch (content.getElementType()) {
                case MethodDeclaration.TYPE:
                    methods.add(MethodInfo.generate((MethodDeclaration)content, constant_pool));
                    break;
                case ConstructorDeclaration.TYPE:
                    methods.add(MethodInfo.generate((ConstructorDeclaration)content, constant_pool));
                    break;
                case FieldDeclaration.TYPE:
                case FieldCreation.TYPE:
                    fields.add(FieldInfo.generate((FieldDeclaration)content, constant_pool));
                    break;
                case Initializer.TYPE:
                    break;
                default:
                    throw new RuntimeException(content.getClass().toString());
            }
        }
    }
}
