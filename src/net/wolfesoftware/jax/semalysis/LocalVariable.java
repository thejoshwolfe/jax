package net.wolfesoftware.jax.semalysis;

public class LocalVariable extends SecretLocalVariable
{
    public final String name;

    public LocalVariable(String name, Type type)
    {
        super(type);
        this.name = name;
    }

    public String toString()
    {
        return type + " " + name;
    }
}
