package net.wolfesoftware.jax.lexiconizer;

public abstract class Type
{
    public final String fullName;
    public final String id;

    public Type(String fullName, String id)
    {
        this.fullName = fullName;
        this.id = id;
    }

    public abstract Method resolveMethod(String name, Type[] argumentSignature);
    public abstract Field resolveField(String name);
    public abstract boolean isInstanceOf(Type type);
    /** example: java/lang/String */
    public String getTypeName()
    {
        return fullName.replace('.', '/');
    }
    /** example: Ljava/lang/String; 
     * <p/>
     * http://java.sun.com/docs/books/jvms/second_edition/html/ClassFile.doc.html#84645
     */
    public String getTypeCode()
    {
        return "L" + getTypeName() + ";";
    }

    public abstract int getType();

    public String toString()
    {
        return fullName;
    }

    public boolean isPrimitive()
    {
        return false;
    }

}
