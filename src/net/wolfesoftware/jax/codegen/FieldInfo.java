package net.wolfesoftware.jax.codegen;

import java.io.*;

/**
 * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#2877
 */
public class FieldInfo
{
    public static final short
    ACC_PUBLIC = 0x0001,
    ACC_PRIVATE = 0x0002,
    ACC_PROTECTED = 0x0004,
    ACC_STATIC = 0x0008,
    ACC_FINAL = 0x0010,
    ACC_VOLATILE = 0x0040,
    ACC_TRANSIENT = 0x0080;

    public void write(DataOutputStream out) throws IOException
    {
        // TODO
        throw null;
    }
}
